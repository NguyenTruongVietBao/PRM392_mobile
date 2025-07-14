package com.example.myapplication.utils;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class FirebaseHelper {
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public interface OnUploadCompleteListener {
        void onSuccess(String downloadUrl);
        void onFailure(String error);
    }

    public FirebaseHelper() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public void uploadImage(Uri imageUri, OnUploadCompleteListener listener) {
        String fileName = "PRM392/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        listener.onSuccess(uri.toString());
                    }).addOnFailureListener(e -> {
                        listener.onFailure("Error getting URL: " + e.getMessage());
                    });
                })
                .addOnFailureListener(e -> {
                    listener.onFailure("Upload error: " + e.getMessage());
                });
    }
}
