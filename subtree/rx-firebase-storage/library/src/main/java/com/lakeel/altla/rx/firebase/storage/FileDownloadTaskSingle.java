package com.lakeel.altla.rx.firebase.storage;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Single;
import rx.SingleSubscriber;

public final class FileDownloadTaskSingle {

    private FileDownloadTaskSingle() {
    }

    @NonNull
    public static Single<FileDownloadTask.TaskSnapshot> create(@NonNull final FileDownloadTask task,
                                                               @Nullable final OnProgressListener onProgressListener) {
        if (task == null) throw new IllegalArgumentException("'task' must be not null.");

        return Single.create(new Single.OnSubscribe<FileDownloadTask.TaskSnapshot>() {

            @Override
            public void call(final SingleSubscriber<? super FileDownloadTask.TaskSnapshot> subscriber) {

                task.addOnSuccessListener(
                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot snapshot) {
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
                            new com.google.firebase.storage.OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(FileDownloadTask.TaskSnapshot snapshot) {
                                    onProgressListener.onProgress(snapshot.getTotalByteCount(),
                                                                  snapshot.getBytesTransferred());
                                }
                            });
                }
            }
        });
    }
}
