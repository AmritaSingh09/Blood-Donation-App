package com.example.blooddonation.core.Helpers;

import androidx.annotation.NonNull;

import com.example.blooddonation.core.Interfaces.Database.RealtimeDatabase;
import com.example.blooddonation.core.Interfaces.Database.StorageDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DatabaseHelper {

    private RealtimeDatabase database;
    private StorageDatabase storageDb;
    private HashMap<String, Object> data;
    private static DatabaseHelper databaseHelper;

    public DatabaseHelper(){}
    public static DatabaseHelper getInstance() {
        if (databaseHelper == null) databaseHelper = new DatabaseHelper();
        return databaseHelper;
    }


    public DatabaseHelper setData(HashMap<String, Object> data){
        databaseHelper.data = data;
        return databaseHelper;
    }

    public void onCompleteListener(RealtimeDatabase realtimeDatabase){
        this.database = realtimeDatabase;
        setDataOnDB();
    }

    private void setDataOnDB() {
        //todo
        FirebaseDatabase
                .getInstance()
                .getReference()
                .updateChildren(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) database.onSuccess("Success");
                    else database.onFailure(task.getException());
                });
    }


}
