package com.lakeel.altla.rx.firebase.storage;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Single;
import rx.SingleSubscriber;

public final class UploadTaskSingle {

    @NonNull
    public static Single<UploadTask.TaskSnapshot> create(@NonNull final UploadTask task,
                                                         @Nullable final OnProgressListener onProgressListener) {
        if (task == null) throw new IllegalArgumentException("'task' must be not null.");

        return Single.create(new Single.OnSubscribe<UploadTask.TaskSnapshot>() {

            @Override
            public void call(final SingleSubscriber<? super UploadTask.TaskSnapshot> subscriber) {

                task.addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot snapshot) {
                                subscriber.onSuccess(snapshot);
                            }
                        })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    subscriber.onError(e);
                                }
                            });

                if (onProgressListener != null) {
                    task.addOnProgressListener(
                            new com.google.firebase.storage.OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot snapshot) {
                                    onProgressListener.onProgress(snapshot.getTotalByteCount(),
                                                                  snapshot.getBytesTransferred());
                                }
                            });
                }
            }
        });
    }
}
