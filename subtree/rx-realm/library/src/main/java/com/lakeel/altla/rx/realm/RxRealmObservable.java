package com.lakeel.altla.rx.realm;

import io.realm.Realm;
import rx.Observable;
import rx.functions.Func1;

public final class RxRealmObservable {

    private RxRealmObservable() {
    }

    public static <T> Observable<T> using(Func1<? super Realm, ? extends Observable<? extends T>> factory) {
        return Observable.using(RealmFactory.INSTANCE, factory, DisposeRealm.INSTANCE);
    }

    public static <T> Observable<T> transaction(Realm realm,
                                                Func1<? super Realm, ? extends Observable<? extends T>> factory) {
        return Observable.create(new RealmTransactionObservable<>(realm, factory));
    }

    public static <T> Observable<T> transaction(final Func1<? super Realm, ? extends Observable<? extends T>> factory) {
        return using(new Func1<Realm, Observable<? extends T>>() {
            @Override
            public Observable<? extends T> call(Realm realm) {
                return transaction(realm, factory);
            }
        });
    }
}
