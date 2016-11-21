package com.lakeel.altla.vision.builder.data.repository.firebase;

import com.google.firebase.database.DatabaseError;

public final class DatabaseErrorException extends RuntimeException {

    private final DatabaseError databaseError;

    public DatabaseErrorException(DatabaseError databaseError) {
        super(databaseError.getMessage());

        this.databaseError = databaseError;
    }

    public DatabaseError getDatabaseError() {
        return databaseError;
    }
}
