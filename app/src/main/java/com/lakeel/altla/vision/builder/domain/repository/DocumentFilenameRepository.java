package com.lakeel.altla.vision.builder.domain.repository;

import android.net.Uri;

import rx.Single;

public interface DocumentFilenameRepository {

    Single<String> find(Uri uri);
}
