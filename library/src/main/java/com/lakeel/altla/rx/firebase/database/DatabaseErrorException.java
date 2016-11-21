package com.lakeel.altla.rx.firebase.database;

import com.google.firebase.database.DatabaseError;

import android.support.annotation.NonNull;

public final class DatabaseErrorException extends RuntimeException {

    private final DatabaseError databaseError;

    public DatabaseErrorException(@NonNull DatabaseError databaseError) {
        super(databaseError.getMessage());
        this.databaseError = databaseError;
    }

    public DatabaseError getDatabaseError() {
        return databaseError;
    }
}
