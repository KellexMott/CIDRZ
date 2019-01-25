package com.techart.cidrz.constants;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Has com.techart.cidrz.constants for Fire base variable names
 * Created by Kelvin on 11/09/2017.
 */

public final class FireBaseUtils {
    public static final DatabaseReference mDatabaseFacility = FirebaseDatabase.getInstance().getReference().child(Constants.FACILITY_KEY);
   public static FirebaseAuth mAuth  = FirebaseAuth.getInstance();

    public static StorageReference mStorageReports = FirebaseStorage.getInstance().getReference();


    private FireBaseUtils()  {
    }
}
