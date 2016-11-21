package com.lakeel.altla.rx.realm;

import io.realm.Realm;
import io.realm.log.RealmLog;
import rx.Single;
import rx.SingleSubscriber;
import rx.functions.Func1;

final class RealmTransactionSingle<T> implements Single.OnSubscribe<T> {

    private final Realm mRealm;

    private final Func1<? super Realm, ? extends Single<? extends T>> mFactory;

    public RealmTransactionSingle(Realm realm, Func1<? super Realm, ? extends Single<? extends T>> factory) {
        mRealm = realm;
        mFactory = factory;
    }

    @Override
    public void call(final SingleSubscriber<? super T> child) {
        mRealm.beginTransaction();

        Single<? extends T> single;

        try {
            single = mFactory.call(mRealm);
        } catch (Throwable t) {
            mRealm.cancelTransaction();
            return;
        }

        SingleSubscriber<T> parent = new SingleSubscriber<T>() {
            @Override
            public void onSuccess(T value) {
                mRealm.commitTransaction();
                child.onSuccess(value);
            }

            @Override
            public void onError(Throwable error) {
                if (mRealm.isInTransaction()) {
                    mRealm.cancelTransaction();
                } else {
                    RealmLog.warn("Could not cancel transaction, not currently in a transaction.");
                }
                child.onError(error);
            }
        };
        child.add(parent);

        single.subscribe(parent);
    }
}
