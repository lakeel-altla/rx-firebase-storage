# rx-firebase-database

rx-firebase-database is a library that provides conversion from a Query object of Firebase Realtime Database to a RxJava object.

## Usage

```java
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import rx.Completable;
import rx.Observable;
import rx.Single;
...
Query query = getUserFolder().child("something").orderByValue();

// to Observable by Query#addValueEventListener(ValueEventListener)
Observable<String> observable = RxFirebaseQuery
    .asObservable(query)
    .map(new Func1<DataSnapshot, String>() {
        @Override
        public String call(DataSnapshot snapshot) {
            return snapshot.getValue(String.class);
        }
    });

// to Observable by Query#addListenerForSingleValueEvent(ValueEventListener)
Observable<String> observable = RxFirebaseQuery
    .asObservableForSingleValueEvent(query)
    .map(new Func1<DataSnapshot, String>() {
        @Override
        public String call(DataSnapshot snapshot) {
            return snapshot.getValue(String.class);
        }
    });

// to Single
Single<String> single = RxFirebaseQuery
    .asSingleForSingleValueEvent(query)
    .map(new Func1<DataSnapshot, String>() {
        @Override
        public String call(DataSnapshot snapshot) {
            return snapshot.getValue(String.class);
        }
    });

// to Completable
Completable completable = RxFirebaseQuery
    .asCompletableForSingleValueEvent(query);

// for DatabaseReference extending Query.
DatabaseReference reference = ...
RxFirebaseQuery.asObservable(reference);
```

To convert a method that returns a Task object of GMS such as DatabaseReference#updateChildren(...) to a RxJava object, please use [rx-gms-tasks](https://github.com/lakeel-altla/rx-gms-tasks).

