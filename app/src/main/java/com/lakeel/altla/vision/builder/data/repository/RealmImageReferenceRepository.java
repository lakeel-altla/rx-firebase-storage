package com.lakeel.altla.vision.builder.data.repository;

import com.lakeel.altla.rx.realm.RxRealmObservable;
import com.lakeel.altla.rx.realm.RxRealmSingle;
import com.lakeel.altla.vision.builder.data.repository.realm.RealmImageReference;
import com.lakeel.altla.vision.builder.data.repository.realm.RealmImageReferenceMapper;
import com.lakeel.altla.vision.builder.domain.model.ImageReference;
import com.lakeel.altla.vision.builder.domain.repository.ImageReferenceRepository;

import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Single;

public final class RealmImageReferenceRepository implements ImageReferenceRepository {

    private final RealmImageReferenceMapper mMapper = new RealmImageReferenceMapper();

    @Override
    public Single<ImageReference> create(ImageReference imageReference) {
        RealmImageReference realmImageReference = mMapper.map(imageReference);
        return RxRealmSingle.transaction(realm -> Single.create(subscriber -> {
            realm.copyToRealm(realmImageReference);
            subscriber.onSuccess(imageReference);
        }));
    }

    @Override
    public Observable<ImageReference> findAll() {
        return RxRealmObservable.using(realm -> Observable.<RealmImageReference>create(subscriber -> {
            RealmResults<RealmImageReference> realmImageReferences = realm
                    .where(RealmImageReference.class)
                    .findAllSorted(RealmImageReference.FIELD_CREATE_DATE, Sort.ASCENDING);

            for (RealmImageReference realmImageReference : realmImageReferences) {
                subscriber.onNext(realmImageReference);
            }

            subscriber.onCompleted();
        }).map(mMapper::map));
    }
}
