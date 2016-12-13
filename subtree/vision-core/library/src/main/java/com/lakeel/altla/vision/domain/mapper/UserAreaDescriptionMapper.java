package com.lakeel.altla.vision.domain.mapper;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

public final class UserAreaDescriptionMapper {

    private UserAreaDescriptionMapper() {
    }

    public static UserAreaDescription map(TangoAreaDescriptionMetaData metaData) {
        String id = TangoAreaDescriptionMetaDataHelper.getUuid(metaData);
        String name = TangoAreaDescriptionMetaDataHelper.getName(metaData);
        long creationTime = TangoAreaDescriptionMetaDataHelper.getMsSinceEpoch(metaData);

        return new UserAreaDescription(id, name, creationTime);
    }
}
