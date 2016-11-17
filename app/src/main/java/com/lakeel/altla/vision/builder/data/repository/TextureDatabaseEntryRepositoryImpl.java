package com.lakeel.altla.vision.builder.data.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.ArgumentNullException;
import com.lakeel.altla.vision.builder.domain.model.TextureDatabaseEntry;
import com.lakeel.altla.vision.builder.domain.repository.TextureDatabaseEntryRepository;

import rx.Observable;
import rx.Single;

public final class TextureDatabaseEntryRepositoryImpl implements TextureDatabaseEntryRepository {

    private static final Log LOG = LogFactory.getLog(TextureDatabaseEntryRepositoryImpl.class);

    private final DatabaseReference reference;

    public TextureDatabaseEntryRepositoryImpl(DatabaseReference reference) {
        if (reference == null) throw new ArgumentNullException("reference");

        this.reference = reference;
    }

    @Override
    public Single<TextureDatabaseEntry> save(TextureDatabaseEntry entry) {
        LOG.d("Saving the texture entry: uuid = %s", entry.uuid);

        return Single.create(subscriber -> {
            reference.child(entry.uuid).setValue(entry.metadata)
                     .addOnSuccessListener(aVoid -> {
                         LOG.d("Saved the texture entry.");
                         subscriber.onSuccess(entry);
                     })
                     .addOnFailureListener(e -> {
                         LOG.e("Failed to save the texture entry.");
                         subscriber.onError(e);
                     });
        });
    }

    @Override
    public Observable<TextureDatabaseEntry> findByFilename(String filename) {
        LOG.d("Finding the texture entry: filename = %s", filename);

        return Observable.create(subscriber -> {
            reference.orderByChild("filename").equalTo(filename)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                             if (0 < dataSnapshot.getChildrenCount()) {
                                 DataSnapshot child = dataSnapshot.getChildren().iterator().next();

                                 // child structure:
                                 //
                                 // "<uuid>" : {
                                 //   "metadata" : {
                                 //     ...
                                 //   }
                                 // }
                                 //

                                 TextureDatabaseEntry entry = new TextureDatabaseEntry();
                                 entry.uuid = child.getKey();
                                 entry.metadata = child.getValue(TextureDatabaseEntry.Metadata.class);

                                 LOG.d("Found the texture entry: entry = %s", entry);

                                 subscriber.onNext(entry);
                             } else {
                                 LOG.d("Found no texture entry.");
                             }

                             subscriber.onCompleted();
                         }

                         @Override
                         public void onCancelled(DatabaseError databaseError) {
                             LOG.e("Cancelled to find the texture entry.");
                             subscriber.onError(new DatabaseErrorException(databaseError));
                         }
                     });
        });
    }
}
