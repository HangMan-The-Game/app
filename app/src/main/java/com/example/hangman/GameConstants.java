package com.example.hangman;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;

public class GameConstants {
    public static Task<String> loadWordsFromFirestore(int numDoc, String difficulty) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("LoadWordsFromFirestore", "Inizio recupero dati da Firestore");

        return db.collection(difficulty)
                .document(String.valueOf(numDoc))
                .get()
                .continueWith(new Continuation<DocumentSnapshot, String>() {
                    @Override
                    public String then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        Log.d("LoadWordsFromFirestore", "Dopo il recupero dei dati da Firestore");

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String word = document.getString("word");
                                Log.d("LoadWordsFromFirestore", "Parola ottenuta: " + word);
                                return word;
                            }
                        }

                        Log.d("LoadWordsFromFirestore", "Nessuna parola ottenuta da Firestore");
                        return null;
                    }
                });
    }

}