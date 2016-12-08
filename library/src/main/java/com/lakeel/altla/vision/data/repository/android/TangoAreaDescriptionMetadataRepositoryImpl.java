package com.lakeel.altla.vision.data.repository.android;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.TangoAreaDescriptionMetadataRepository;

import rx.Observable;
import rx.Single;

public final class TangoAreaDescriptionMetadataRepositoryImpl implements TangoAreaDescriptionMetadataRepository {

    private final Tango tango;

    public TangoAreaDescriptionMetadataRepositoryImpl(Tango tango) {
        if (tango == null) throw new ArgumentNullException("tango");

        this.tango = tango;
    }

    @Override
    public Observable<TangoAreaDescriptionMetaData> find(String id) {
        if (id == null) throw new ArgumentNullException("id");

        return Observable.just(tango.loadAreaDescriptionMetaData(id));
    }

    @Override
    public Observable<TangoAreaDescriptionMetaData> findAll() {
        return Observable.from(tango.listAreaDescriptions())
                         .flatMap(this::find);
    }

    @Override
    public Single<String> delete(String id) {
        if (id == null) throw new ArgumentNullException("id");

        return Single.create(subscriber -> {
            tango.deleteAreaDescription(id);
            subscriber.onSuccess(id);
        });
    }
}
