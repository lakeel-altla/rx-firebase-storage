package com.lakeel.altla.rx.firebase.storage;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;

public final class RxFirebaseStorageTask {

    @NonNull
    public static Observable<UploadTask.TaskSnapshot> asObservable(
            @NonNull final UploadTask task, @Nullable final OnProgressListener onProgressListener) {
        if (task == null) throw new IllegalArgumentException("'task' must be not null.");

        return Observable.create(new Observable.OnSubscribe<UploadTask.TaskSnapshot>() {
            @Override
            public void call(final Subscriber<? super UploadTask.TaskSnapshot> subscriber) {

                task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot snapshot) {
                        subscriber.onNext(snapshot);
                        subscriber.onCompleted();
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
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

    @NonNull
    public static Single<UploadTask.TaskSnapshot> asSingle(@NonNull final UploadTask task,
                                                           @Nullable final OnProgressListener onProgressListener) {
        if (task == null) throw new IllegalArgumentException("'task' must be not null.");

        return Single.create(new Single.OnSubscribe<UploadTask.TaskSnapshot>() {

            @Override
            public void call(final SingleSubscriber<? super UploadTask.TaskSnapshot> subscriber) {

                task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot snapshot) {
                        subscriber.onSuccess(snapshot);
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
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

    @NonNull
    public static Completable asCompletable(@NonNull final UploadTask task,
                                            @Nullable final OnProgressListener onProgressListener) {
        if (task == null) throw new IllegalArgumentException("'task' must be not null.");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(final CompletableSubscriber subscriber) {

                task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot snapshot) {
                        subscriber.onCompleted();
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
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
