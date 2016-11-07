package com.lakeel.altla.rx.realm;

import io.realm.Realm;
import rx.functions.Action1;

final class DisposeRealm implements Action1<Realm> {

    static final DisposeRealm INSTANCE = new DisposeRealm();

    private DisposeRealm() {
    }

    @Override
    public void call(Realm realm) {
        realm.close();
    }
}
