package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DatabaseReference;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserDevice;
import com.lakeel.altla.vision.domain.repository.UserDeviceRepository;

import rx.Single;

public final class UserDeviceRepositoryImpl implements UserDeviceRepository {

    private static final Log LOG = LogFactory.getLog(UserProfileRepositoryImpl.class);

    private static final String PATH_USER_DEVICES = "userDevices";

    private final DatabaseReference rootReference;

    public UserDeviceRepositoryImpl(DatabaseReference rootReference) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");

        this.rootReference = rootReference;
    }

    @Override
    public Single<UserDevice> save(UserDevice userDevice) {
        if (userDevice == null) throw new ArgumentNullException("userDevice");

        UserDeviceValue value = new UserDeviceValue();
        value.creationTime = userDevice.creationTime;
        value.osName = userDevice.osName;
        value.osModel = userDevice.osModel;
        value.osVersion = userDevice.osVersion;

        rootReference.child(PATH_USER_DEVICES)
                     .child(userDevice.userId)
                     .child(userDevice.instanceId)
                     .setValue(value, (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });

        return Single.just(userDevice);
    }

    public final class UserDeviceValue {

        public long creationTime;

        public String osName;

        public String osModel;

        public String osVersion;
    }
}
