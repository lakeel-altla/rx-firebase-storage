package com.lakeel.altla.rx.firebase.storage;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.functions.Action0;

/**
 * Provides methods to wrap a task for Firebase Storage in a Rx object.
 */
public final class RxFirebaseStorageTask {

    /**
     * Wraps the specified {@link UploadTask} in {@link Observable}.
     *
     * @param task               The wrapped task.
     * @param onProgressListener The callback to get the progress status, or null.
     * @return The {@link Observable} that wraps the specified {@link UploadTask}.
     */
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
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                task.cancel();
            }
        });
    }

    /**
     * Wraps the specified {@link UploadTask} in {@link Single}.
     *
     * @param task               The wrapped task.
     * @param onProgressListener The callback to get the progress status, or null.
     * @return The {@link Single} that wraps the specified {@link UploadTask}.
     */
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
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                task.cancel();
            }
        });
    }

    /**
     * Wraps the specified {@link UploadTask} in {@link Completable}.
     *
     * @param task               The wrapped task.
     * @param onProgressListener The callback to get the progress status, or null.
     * @return The {@link Completable} that wraps the specified {@link UploadTask}.
     */
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
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                task.cancel();
            }
        });
    }

    /**
     * Wraps the specified {@link FileDownloadTask} in {@link Observable}.
     *
     * @param task               The wrapped task.
     * @param onProgressListener The callback to get the progress status, or null.
     * @return The {@link Observable} that wraps the specified {@link FileDownloadTask}.
     */
    @NonNull
    public static Observable<FileDownloadTask.TaskSnapshot> asObservable(
            @NonNull final FileDownloadTask task, @Nullable final OnProgressListener onProgressListener) {
        if (task == null) throw new IllegalArgumentException("'task' must be not null.");

        return Observable.create(new Observable.OnSubscribe<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void call(final Subscriber<? super FileDownloadTask.TaskSnapshot> subscriber) {

                task.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot snapshot) {
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
                            new com.google.firebase.storage.OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(FileDownloadTask.TaskSnapshot snapshot) {
                                    onProgressListener.onProgress(snapshot.getTotalByteCount(),
                                                                  snapshot.getBytesTransferred());
                                }
                            });
                }
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                task.cancel();
            }
        });
    }

    /**
     * Wraps the specified {@link FileDownloadTask} in {@link Single}.
     *
     * @param task               The wrapped task.
     * @param onProgressListener The callback to get the progress status, or null.
     * @return The {@link Single} that wraps the specified {@link FileDownloadTask}.
     */
    @NonNull
    public static Single<FileDownloadTask.TaskSnapshot> asSingle(
            @NonNull final FileDownloadTask task, @Nullable final OnProgressListener onProgressListener) {
        if (task == null) throw new IllegalArgumentException("'task' must be not null.");

        return Single.create(new Single.OnSubscribe<FileDownloadTask.TaskSnapshot>() {

            @Override
            public void call(final SingleSubscriber<? super FileDownloadTask.TaskSnapshot> subscriber) {

                task.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot snapshot) {
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
                            new com.google.firebase.storage.OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(FileDownloadTask.TaskSnapshot snapshot) {
                                    onProgressListener.onProgress(snapshot.getTotalByteCount(),
                                                                  snapshot.getBytesTransferred());
                                }
                            });
                }
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                task.cancel();
            }
        });
    }

    /**
     * Wraps the specified {@link FileDownloadTask} in {@link Completable}.
     *
     * @param task               The wrapped task.
     * @param onProgressListener The callback to get the progress status, or null.
     * @return The {@link Completable} that wraps the specified {@link FileDownloadTask}.
     */
    @NonNull
    public static Completable asCompletable(@NonNull final FileDownloadTask task,
                                            @Nullable final OnProgressListener onProgressListener) {
        if (task == null) throw new IllegalArgumentException("'task' must be not null.");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(final CompletableSubscriber subscriber) {

                task.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot snapshot) {
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
                            new com.google.firebase.storage.OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(FileDownloadTask.TaskSnapshot snapshot) {
                                    onProgressListener.onProgress(snapshot.getTotalByteCount(),
                                                                  snapshot.getBytesTransferred());
                                }
                            });
                }
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                task.cancel();
            }
        });
    }

    /**
     * Wraps the specified {@link StreamDownloadTask} in {@link Observable}.
     *
     * @param task               The wrapped task.
     * @param onProgressListener The callback to get the progress status, or null.
     * @return The {@link Observable} that wraps the specified {@link StreamDownloadTask}.
     */
    @NonNull
    public static Observable<StreamDownloadTask.TaskSnapshot> asObservable(
            @NonNull final StreamDownloadTask task, @Nullable final OnProgressListener onProgressListener) {
        if (task == null) throw new IllegalArgumentException("'task' must be not null.");

        return Observable.create(new Observable.OnSubscribe<StreamDownloadTask.TaskSnapshot>() {
            @Override
            public void call(final Subscriber<? super StreamDownloadTask.TaskSnapshot> subscriber) {

                task.addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(StreamDownloadTask.TaskSnapshot snapshot) {
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
                            new com.google.firebase.storage.OnProgressListener<StreamDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(StreamDownloadTask.TaskSnapshot snapshot) {
                                    onProgressListener.onProgress(snapshot.getTotalByteCount(),
                                                                  snapshot.getBytesTransferred());
                                }
                            });
                }
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                task.cancel();
            }
        });
    }

    /**
     * Wraps the specified {@link StreamDownloadTask} in {@link Single}.
     *
     * @param task               The wrapped task.
     * @param onProgressListener The callback to get the progress status, or null.
     * @return The {@link Single} that wraps the specified {@link StreamDownloadTask}.
     */
    @NonNull
    public static Single<StreamDownloadTask.TaskSnapshot> asSingle(
            @NonNull final StreamDownloadTask task, @Nullable final OnProgressListener onProgressListener) {
        if (task == null) throw new IllegalArgumentException("'task' must be not null.");

        return Single.create(new Single.OnSubscribe<StreamDownloadTask.TaskSnapshot>() {

            @Override
            public void call(final SingleSubscriber<? super StreamDownloadTask.TaskSnapshot> subscriber) {

                task.addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(StreamDownloadTask.TaskSnapshot snapshot) {
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
                            new com.google.firebase.storage.OnProgressListener<StreamDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(StreamDownloadTask.TaskSnapshot snapshot) {
                                    onProgressListener.onProgress(snapshot.getTotalByteCount(),
                                                                  snapshot.getBytesTransferred());
                                }
                            });
                }
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                task.cancel();
            }
        });
    }

    /**
     * Wraps the specified {@link StreamDownloadTask} in {@link Completable}.
     *
     * @param task               The wrapped task.
     * @param onProgressListener The callback to get the progress status, or null.
     * @return The {@link Completable} that wraps the specified {@link StreamDownloadTask}.
     */
    @NonNull
    public static Completable asCompletable(@NonNull final StreamDownloadTask task,
                                            @Nullable final OnProgressListener onProgressListener) {
        if (task == null) throw new IllegalArgumentException("'task' must be not null.");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(final CompletableSubscriber subscriber) {

                task.addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(StreamDownloadTask.TaskSnapshot snapshot) {
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
                            new com.google.firebase.storage.OnProgressListener<StreamDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(StreamDownloadTask.TaskSnapshot snapshot) {
                                    onProgressListener.onProgress(snapshot.getTotalByteCount(),
                                                                  snapshot.getBytesTransferred());
                                }
                            });
                }
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                task.cancel();
            }
        });
    }
}
