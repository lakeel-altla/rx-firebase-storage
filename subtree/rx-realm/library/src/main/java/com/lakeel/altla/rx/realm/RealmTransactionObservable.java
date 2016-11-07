package com.lakeel.altla.rx.realm;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.observers.Subscribers;

final class RealmTransactionObservable<T> implements Observable.OnSubscribe<T> {

    private final Realm mRealm;

    private final Func1<? super Realm, ? extends Observable<? extends T>> mFactory;

    public RealmTransactionObservable(Realm realm, Func1<? super Realm, ? extends Observable<? extends T>> factory) {
        mRealm = realm;
        mFactory = factory;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        mRealm.beginTransaction();

        Observable<? extends T> observable;

        try {
            observable = mFactory.call(mRealm);
        } catch (Throwable t) {
            mRealm.cancelTransaction();
            return;
        }

        observable.doOnTerminate(new Action0() {
            @Override
            public void call() {
                mRealm.commitTransaction();
            }
        });

        try {
            observable.unsafeSubscribe(Subscribers.wrap(subscriber));
        } catch (Throwable t) {
            mRealm.cancelTransaction();
        }
    }
}
