package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserConnection;
import com.lakeel.altla.vision.domain.repository.UserConnectionRepository;

import java.util.HashMap;
import java.util.Map;

import rx.Single;

public final class UserConnectionRepositoryImpl implements UserConnectionRepository {

    private static final Log LOG = LogFactory.getLog(UserConnectionRepositoryImpl.class);

    private static final String PATH_USER_CONNECTIONS = "userConnections";

    private static final String PATH_ONLINE = "online";

    private static final String PATH_LAST_ONLINE_TIME = "lastOnlineTime";

    private final DatabaseReference rootReference;

    public UserConnectionRepositoryImpl(DatabaseReference rootReference) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");

        this.rootReference = rootReference;
    }

    @Override
    public Single<UserConnection> setOnline(UserConnection userConnection) {
        if (userConnection == null) throw new ArgumentNullException("userConnection");

        DatabaseReference connectionReference = rootReference.child(PATH_USER_CONNECTIONS)
                                                             .child(userConnection.userId)
                                                             .child(userConnection.instanceId);
        DatabaseReference onlineReference = connectionReference.child(PATH_ONLINE);
        DatabaseReference lastOnlineTimeReference = connectionReference.child(PATH_LAST_ONLINE_TIME);

        // when connected.
        onlineReference.setValue(Boolean.TRUE, (error, reference) -> {
            if (error != null) {
                LOG.e(String.format("Failed to setOnline: reference = %s", reference), error.toException());
            }
        });

        // when disconnected.
        onlineReference.onDisconnect().setValue(Boolean.FALSE, (error, reference) -> {
            if (error != null) {
                LOG.e(String.format("Failed to onDisconnect on setOnline: reference = %s", reference),
                      error.toException());
            }
        });
        lastOnlineTimeReference.onDisconnect().setValue(ServerValue.TIMESTAMP, (error, reference) -> {
            if (error != null) {
                LOG.e(String.format("Failed to onDisconnect on setOnline: reference = %s", reference),
                      error.toException());
            }
        });

        return Single.just(userConnection);
    }

    @Override
    public Single<UserConnection> setOffline(UserConnection userConnection) {
        if (userConnection == null) throw new ArgumentNullException("userConnection");

        DatabaseReference connectionReference = rootReference.child(PATH_USER_CONNECTIONS)
                                                             .child(userConnection.userId)
                                                             .child(userConnection.instanceId);

        Map<String, Object> children = new HashMap<>(2);
        children.put(PATH_ONLINE, Boolean.FALSE);
        children.put(PATH_LAST_ONLINE_TIME, ServerValue.TIMESTAMP);

        connectionReference.updateChildren(children, (error, reference) -> {
            if (error != null) {
                LOG.e(String.format("Failed to setOffline: reference = %s", reference), error.toException());
            }
        });

        return Single.just(userConnection);
    }
}
