package com.lakeel.altla.rx.realm;

import io.realm.Realm;
import rx.functions.Func0;

final class RealmFactory implements Func0<Realm> {

    static final RealmFactory INSTANCE = new RealmFactory();

    private RealmFactory() {
    }

    @Override
    public Realm call() {
        return Realm.getDefaultInstance();
    }
}
