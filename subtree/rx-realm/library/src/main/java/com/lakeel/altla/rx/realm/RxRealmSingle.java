package com.lakeel.altla.rx.realm;

import io.realm.Realm;
import rx.Single;
import rx.functions.Func1;

public final class RxRealmSingle {

    private RxRealmSingle() {
    }

    public static <T> Single<T> using(Func1<? super Realm, ? extends Single<? extends T>> factory) {
        return Single.using(RealmFactory.INSTANCE, factory, DisposeRealm.INSTANCE);
    }

    public static <T> Single<T> transaction(Realm realm, Func1<? super Realm, ? extends Single<? extends T>> factory) {
        return Single.create(new RealmTransactionSingle<>(realm, factory));
    }

    public static <T> Single<T> transaction(final Func1<? super Realm, ? extends Single<? extends T>> factory) {
        return using(new Func1<Realm, Single<? extends T>>() {
            @Override
            public Single<? extends T> call(Realm realm) {
                return transaction(realm, factory);
            }
        });
    }
}
