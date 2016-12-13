package com.lakeel.altla.vision.data.repository.android;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionCacheRepository;

import java.io.File;

import rx.Single;

public final class AreaDescriptionCacheRepositoryImpl implements AreaDescriptionCacheRepository {

    private static final String PATH = "areaDescriptions";

    private final File rootDirectory;

    public AreaDescriptionCacheRepositoryImpl(File rootDirectory) {
        if (rootDirectory == null) throw new ArgumentNullException("rootDirectory");

        this.rootDirectory = rootDirectory;
    }

    @Override
    public Single<File> getDirectory() {
        return Single.just(ensureCacheDirectory());
    }

    @Override
    public Single<File> getFile(String id) {
        if (id == null) throw new ArgumentNullException("id");

        return Single.just(resolveCacheFile(id));
    }

    private File ensureCacheDirectory() {
        File directory = new File(rootDirectory, PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private File resolveCacheFile(String id) {
        File directory = ensureCacheDirectory();
        return new File(directory, id);
    }
}
