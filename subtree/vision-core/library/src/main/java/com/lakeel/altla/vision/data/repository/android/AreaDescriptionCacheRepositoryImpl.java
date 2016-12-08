package com.lakeel.altla.vision.data.repository.android;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionCacheRepository;

import java.io.File;

import rx.Single;

public final class AreaDescriptionCacheRepositoryImpl implements AreaDescriptionCacheRepository {

    private static final String PATH = "areaDescriptions";

    private final File rootDirectory;

    private final FirebaseAuth auth;

    public AreaDescriptionCacheRepositoryImpl(File rootDirectory, FirebaseAuth auth) {
        if (rootDirectory == null) throw new ArgumentNullException("rootDirectory");
        if (auth == null) throw new ArgumentNullException("auth");

        this.rootDirectory = rootDirectory;
        this.auth = auth;
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
        File userDirectory = new File(rootDirectory, resolveUserId());
        File directory = new File(userDirectory, PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private File resolveCacheFile(String id) {
        File directory = ensureCacheDirectory();
        return new File(directory, id);
    }

    private String resolveUserId() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The current user could not be resolved.");
        }
        return user.getUid();
    }
}
