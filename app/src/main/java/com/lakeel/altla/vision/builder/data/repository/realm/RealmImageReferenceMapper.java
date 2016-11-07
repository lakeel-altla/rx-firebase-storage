package com.lakeel.altla.vision.builder.data.repository.realm;

import com.lakeel.altla.vision.builder.domain.model.ImageReference;

public final class RealmImageReferenceMapper {

    public RealmImageReference map(ImageReference imageReference) {
        RealmImageReference realmImageReference = new RealmImageReference();
        map(imageReference, realmImageReference);
        return realmImageReference;
    }

    public ImageReference map(RealmImageReference realmImageReference) {
        return new ImageReference(realmImageReference.uri, realmImageReference.createDate);
    }

    public void map(ImageReference imageReference, RealmImageReference realmImageReference) {
        realmImageReference.uri = imageReference.uri;
        realmImageReference.createDate = imageReference.createDate;
    }
}
