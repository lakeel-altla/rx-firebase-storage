package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.AreaDescriptionEntry;
import com.lakeel.altla.vision.domain.model.AreaDescriptionMetadata;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionEntryRepository;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionFileRepository;
import com.lakeel.altla.vision.domain.repository.TangoAreaDescriptionMetadataRepository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import rx.Single;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public final class AddAreaDescriptionUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    AreaDescriptionEntryRepository areaDescriptionEntryRepository;

    @Inject
    AreaDescriptionFileRepository areaDescriptionFileRepository;

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;


    private final Action1<? super InputStream> closeStream = stream -> {
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    @Inject
    public AddAreaDescriptionUseCase() {
    }

    public Single<AreaDescriptionEntry> execute(String id, OnProgressListener onProgressListener) {
        if (id == null) throw new ArgumentNullException("id");

        // Convert arguments to the internal model.
        return Single.just(new Model(id, onProgressListener))
                     // Get the metadata from Tango.
                     .flatMap(this::getMetadataFromTango)
                     // Open the stream of the area description file as cache.
                     .flatMap(this::createCacheStream)
                     // Get the total bytes of it.
                     .flatMap(this::getTotalBytes)
                     // Upload it to Firebase Storage.
                     .flatMap(this::upload)
                     // Save the entry and the metadata to Firebase Database.
                     .flatMap(this::saveEntry)
                     // Return the added entry.
                     .map(model -> model.entry)
                     .subscribeOn(Schedulers.io());
    }

    private Single<Model> getMetadataFromTango(Model model) {
        return tangoAreaDescriptionMetadataRepository
                .find(model.id)
                .map(tangoAreaDescriptionMetaData -> {
                    // Get the name.
                    String name = TangoAreaDescriptionMetaDataHelper.getName(tangoAreaDescriptionMetaData);
                    model.entry = new AreaDescriptionEntry(model.id, name);

                    // Get other metadatas.
                    AreaDescriptionMetadata metadata = new AreaDescriptionMetadata();
                    metadata.creationTime =
                            TangoAreaDescriptionMetaDataHelper.getMsSinceEpoch(tangoAreaDescriptionMetaData);

                    double[] transformation =
                            TangoAreaDescriptionMetaDataHelper.getTransformation(tangoAreaDescriptionMetaData);
                    metadata.position = new AreaDescriptionMetadata.Vector3(transformation[0],
                                                                            transformation[1],
                                                                            transformation[2]);
                    metadata.rotation = new AreaDescriptionMetadata.Quaternion(transformation[3],
                                                                               transformation[4],
                                                                               transformation[5],
                                                                               transformation[6]);

                    model.metadata = metadata;

                    return model;
                })
                .toSingle();
    }

    private Single<Model> createCacheStream(Model model) {
        return areaDescriptionCacheRepository.getFile(model.id)
                                             .map(path -> {
                                                 try {
                                                     model.stream = new FileInputStream(path);
                                                     return model;
                                                 } catch (FileNotFoundException e) {
                                                     throw new RuntimeException(e);
                                                 }
                                             });
    }

    private Single<Model> getTotalBytes(Model model) {
        return Single.<Long>create(subscriber -> {
            try {
                long totalBytes = model.stream.available();
                subscriber.onSuccess(totalBytes);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        }).map(totalBytes -> {
            model.totalBytes = totalBytes;
            return model;
        });
    }

    private Single<Model> upload(Model model) {
        return Single.using(
                () -> model.stream,
                stream -> areaDescriptionFileRepository
                        .upload(model.id, model.stream,
                                (totalBytes, bytesTransferred) ->
                                        model.onProgressListener.onProgress(model.totalBytes, bytesTransferred))
                        .map(id -> model),
                closeStream);
    }

    private Single<Model> saveEntry(Model model) {
        return areaDescriptionEntryRepository.save(model.entry, model.metadata)
                                             .map(entry -> model);
    }

    public interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }

    private final class Model {

        final String id;

        final OnProgressListener onProgressListener;

        AreaDescriptionEntry entry;

        AreaDescriptionMetadata metadata;

        InputStream stream;

        long totalBytes;

        Model(String id, OnProgressListener onProgressListener) {
            this.id = id;
            this.onProgressListener = onProgressListener;
        }
    }
}
