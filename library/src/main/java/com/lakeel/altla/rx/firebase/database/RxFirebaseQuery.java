package com.lakeel.altla.rx.firebase.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.support.annotation.NonNull;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Provides methods to wrap a {@link Query} for Firebase Database in a Rx object.
 */
public final class RxFirebaseQuery {

    private RxFirebaseQuery() {
    }

    /**
     * Wraps the specified {@link Query} in {@link Observable}
     * with {@link Query#addValueEventListener(ValueEventListener)}.
     *
     * @param query The wrapped query.
     * @return The {@link Observable} that wraps the specified {@link Query}.
     */
    @NonNull
    public static Observable<DataSnapshot> asObservable(@NonNull final Query query) {
        if (query == null) throw new IllegalArgumentException("'query' must be not null.");

        return Observable.using(new Func0<QueryForValueEvent>() {
            @Override
            public QueryForValueEvent call() {
                return new QueryForValueEvent(query);
            }
        }, new Func1<QueryForValueEvent, Observable<? extends DataSnapshot>>() {
            @Override
            public Observable<? extends DataSnapshot> call(QueryForValueEvent queryForValueEvent) {
                return queryForValueEvent.createObservable();
            }
        }, new Action1<QueryForValueEvent>() {
            @Override
            public void call(QueryForValueEvent queryForValueEvent) {
                queryForValueEvent.dispose();
            }
        });
    }

    /**
     * Wraps the specified {@link Query} in {@link Observable}
     * with {@link Query#addListenerForSingleValueEvent(ValueEventListener)}.
     *
     * @param query The wrapped query.
     * @return The {@link Observable} that wraps the specified {@link Query}.
     */
    @NonNull
    public static Observable<DataSnapshot> asObservableForSingleValueEvent(@NonNull final Query query) {
        if (query == null) throw new IllegalArgumentException("'query' must be not null.");

        return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
            @Override
            public void call(final Subscriber<? super DataSnapshot> subscriber) {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        subscriber.onNext(snapshot);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        subscriber.onError(error.toException());
                    }
                });
            }
        });
    }

    /**
     * Wraps the specified {@link Query} in {@link Single}
     * with {@link Query#addListenerForSingleValueEvent(ValueEventListener)}.
     *
     * @param query The wrapped query.
     * @return The {@link Single} that wraps the specified {@link Query}.
     */
    @NonNull
    public static Single<DataSnapshot> asSingleForSingleValueEvent(@NonNull final Query query) {
        if (query == null) throw new IllegalArgumentException("'query' must be not null.");

        return Single.create(new Single.OnSubscribe<DataSnapshot>() {
            @Override
            public void call(final SingleSubscriber<? super DataSnapshot> subscriber) {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        subscriber.onSuccess(snapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        subscriber.onError(error.toException());
                    }
                });
            }
        });
    }

    /**
     * Wraps the specified {@link Query} in {@link Completable}
     * with {@link Query#addListenerForSingleValueEvent(ValueEventListener)}.
     *
     * @param query The wrapped query.
     * @return The {@link Completable} that wraps the specified {@link Query}.
     */
    @NonNull
    public static Completable asCompletableForSingleValueEvent(@NonNull final Query query) {
        if (query == null) throw new IllegalArgumentException("'query' must be not null.");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(final CompletableSubscriber subscriber) {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        subscriber.onError(error.toException());
                    }
                });
            }
        });
    }

    private static class ValueEventListenerDelegate implements ValueEventListener {

        private final Subscriber<? super DataSnapshot> subscriber;

        private ValueEventListenerDelegate(Subscriber<? super DataSnapshot> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onDataChange(DataSnapshot snapshot) {
            subscriber.onNext(snapshot);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            subscriber.onError(error.toException());
        }
    }

    private static class QueryForValueEvent {

        private ValueEventListenerDelegate delegate;

        private Query query;

        private QueryForValueEvent(Query query) {
            this.query = query;
        }

        private Observable<DataSnapshot> createObservable() {
            return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
                @Override
                public void call(Subscriber<? super DataSnapshot> subscriber) {
                    delegate = new ValueEventListenerDelegate(subscriber);
                    query.addValueEventListener(delegate);
                }
            });
        }

        private void dispose() {
            query.removeEventListener(delegate);
        }
    }
}
