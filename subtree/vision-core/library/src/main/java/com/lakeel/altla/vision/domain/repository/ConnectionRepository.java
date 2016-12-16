package com.lakeel.altla.vision.domain.repository;

import rx.Observable;

public interface ConnectionRepository {

    Observable<Boolean> observe();
}
