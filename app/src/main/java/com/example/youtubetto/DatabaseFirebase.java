package com.example.youtubetto;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DatabaseFirebase {

    FirebaseFirestore db;

    public DatabaseFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    public void insertUserToDatabase(User user) {
        Map<String, Object> userInsert = new HashMap<>();

        userInsert.put("googleId", user.getGoogleId());
        userInsert.put("googleEmail", user.getEmail());
        userInsert.put("name", user.getName());

        db.collection("Users").document(user.getGoogleId())
                .set(userInsert)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("", "Document successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d("", "Error updating document!", e);

                    }
                });
    }

    public Task getUser(String googleId){
        return db.collection("Users")
                .whereEqualTo("googleId", googleId)
                .get();
    }
}
