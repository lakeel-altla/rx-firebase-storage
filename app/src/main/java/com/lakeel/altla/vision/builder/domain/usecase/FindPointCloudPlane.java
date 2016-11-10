package com.lakeel.altla.vision.builder.domain.usecase;

import com.lakeel.altla.tango.PointCloud;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class FindPointCloudPlane {

    @Inject
    PointCloud pointCloud;

    @Inject
    public FindPointCloudPlane() {
    }

    public Single<PointCloud.Plane> execute(double timestamp, float u, float v) {
        return Single.<PointCloud.Plane>create(subscriber -> {
            PointCloud.Plane plane = pointCloud.findPlane(timestamp, u, v);
            subscriber.onSuccess(plane);
        }).subscribeOn(Schedulers.io());
    }
}
