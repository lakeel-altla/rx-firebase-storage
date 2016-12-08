package com.lakeel.altla.vision.data.repository.android;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.TextureCacheRepository;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.Single;

public final class TextureCacheRepositoryImpl implements TextureCacheRepository {

    private static final Log LOG = LogFactory.getLog(TextureCacheRepositoryImpl.class);

    private final Context context;

    public TextureCacheRepositoryImpl(Context context) {
        if (context == null) throw new ArgumentNullException("context");

        this.context = context;
    }

    @Override
    public Observable<File> find(String fileId) {
        if (fileId == null) throw new ArgumentNullException("fileId");

        return Observable.create(subscriber -> {
            File file = resolveCacheFile(fileId);
            if (file.exists()) {
                LOG.d("The cache file exists: fileId = %s", fileId);

                subscriber.onNext(file);
            }

            subscriber.onCompleted();
        });
    }

    @Override
    public Single<File> create(String fileId) {
        if (fileId == null) throw new ArgumentNullException("fileId");

        return Single.create(subscriber -> {
            File file = resolveCacheFile(fileId);
            try {
                if (file.createNewFile()) {
                    LOG.d("Created the new cache file: fileId = %s", fileId);
                } else {
                    LOG.w("The cache file already exists: fileId = %s", fileId);
                }

                subscriber.onSuccess(file);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Single<String> delete(String fileId) {
        if (fileId == null) throw new ArgumentNullException("fileId");

        return Single.create(subscriber -> {
            File file = resolveCacheFile(fileId);
            if (file.delete()) {
                LOG.d("Deleted the new cache file: fileId = %s", fileId);
            } else {
                LOG.w("The cache file does not exist: fileId = %s", fileId);
            }

            subscriber.onSuccess(fileId);
        });
    }

    private File resolveCacheFile(String fileId) {
        File directory = new File(context.getCacheDir(), "textures");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return new File(directory, fileId);
    }
}
