# rx-firebase-storage

rx-firebase-storage is a library that provides conversion from a StorageTask object of Firebase Straoge to a RxJava object.

## Usage

### UploadTask

```java
import com.lakeel.altla.rx.firebase.storage.RxFirebaseStorageTask;
import com.lakeel.altla.rx.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import rx.Completable;
import rx.Observable;
import rx.Single;

...

UploadTask task = ...

// UploadTask to Observable.
Observable<UploadTask.DataSnapshot> observable = RxFirebaseStorageTask
    .asObservable(task, new OnProgressListener() {
            @Override
            public void onProgress(long totalBytes, long bytesTransferred) {
                ...
            }
        });

// UploadTask to Single.
Single<UploadTask.DataSnapshot> single = RxFirebaseStorageTask
    .asSingle(task, new OnProgressListener() {
            @Override
            public void onProgress(long totalBytes, long bytesTransferred) {
                ...
            }
        });

// UploadTask to Completable.
Completable single = RxFirebaseStorageTask
    .asCompletable(task, new OnProgressListener() {
            @Override
            public void onProgress(long totalBytes, long bytesTransferred) {
                ...
            }
        });
```

### FileDownloadTask

```java
import com.lakeel.altla.rx.firebase.storage.RxFirebaseStorageTask;
import com.lakeel.altla.rx.firebase.storage.OnProgressListener;
import com.google.firebase.storage.FileDownloadTask;
import rx.Completable;
import rx.Observable;
import rx.Single;

...

FileDownloadTask task = ...

// UploadTask to Observable.
Observable<FileDownloadTask.DataSnapshot> observable = RxFirebaseStorageTask
    .asObservable(task, new OnProgressListener() {
            @Override
            public void onProgress(long totalBytes, long bytesTransferred) {
                ...
            }
        });

// UploadTask to Single.
Single<FileDownloadTask.DataSnapshot> single = RxFirebaseStorageTask
    .asSingle(task, new OnProgressListener() {
            @Override
            public void onProgress(long totalBytes, long bytesTransferred) {
                ...
            }
        });

// UploadTask to Completable.
Completable single = RxFirebaseStorageTask
    .asCompletable(task, new OnProgressListener() {
            @Override
            public void onProgress(long totalBytes, long bytesTransferred) {
                ...
            }
        });
```

### StreamDownloadTask

```java
import com.lakeel.altla.rx.firebase.storage.RxFirebaseStorageTask;
import com.lakeel.altla.rx.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StreamDownloadTask;
import rx.Completable;
import rx.Observable;
import rx.Single;

...

StreamDownloadTask task = ...

// UploadTask to Observable.
Observable<StreamDownloadTask.DataSnapshot> observable = RxFirebaseStorageTask
    .asObservable(task, new OnProgressListener() {
            @Override
            public void onProgress(long totalBytes, long bytesTransferred) {
                ...
            }
        });

// UploadTask to Single.
Single<StreamDownloadTask.DataSnapshot> single = RxFirebaseStorageTask
    .asSingle(task, new OnProgressListener() {
            @Override
            public void onProgress(long totalBytes, long bytesTransferred) {
                ...
            }
        });

// UploadTask to Completable.
Completable single = RxFirebaseStorageTask
    .asCompletable(task, new OnProgressListener() {
            @Override
            public void onProgress(long totalBytes, long bytesTransferred) {
                ...
            }
        });
```

To convert a method that returns a Task object of GMS such as StorageReference#delete(...) to a RxJava object, please use [rx-gms-tasks](https://github.com/lakeel-altla/rx-gms-tasks).

