package com.example.hangman;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Random;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;



public class GameManager {

    private String lettersUsed = "";
    private String underscoreWord;
    private String wordToGuess;
    private final int maxTries = 6;
    private int currentTries = 0;
    private int drawable = R.drawable.hangman6;

    private long userPoints = 0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /*public GameState startNewGame() {
        lettersUsed = "";
        currentTries = 0;
        drawable = R.drawable.hangman6;

        Random random = new Random();
        int maxDocumentNumber = 2;

        int randomDocumentNumber = random.nextInt(maxDocumentNumber) + 1;

        GameConstants.loadWordsFromFirestore(randomDocumentNumber, "Facile").addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String word) {
                if (word != null) {
                    wordToGuess = word;
                    generateUnderscores(wordToGuess);
                }
            }
        });
        return getGameState();
    } */

    private void getUserPoints(final OnSuccessListener<Long> successListener, final OnFailureListener failureListener) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Long points = documentSnapshot.getLong("points");
                    if (points != null) {
                        successListener.onSuccess(points);
                    } else {
                        successListener.onSuccess(0L);
                    }
                } else {
                    successListener.onSuccess(0L);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                failureListener.onFailure(e);
            }
        });
    }

    private void updateUserPoints(long newPoints) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference userRef = db.collection("users").document(uid);

        userRef.update("points", newPoints)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("GameManager", "Punti dell'utente aggiornati con successo.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("GameManager", "Errore nell'aggiornamento dei punti dell'utente.", e);
                    }
                });
    }

    public interface OnGameStartListener {
        void onGameStart(GameState gameState);
    }

    public GameState startNewGame(final OnGameStartListener listener, final String difficulty) {
        lettersUsed = "";
        currentTries = 0;
        drawable = R.drawable.hangman6;

        Random random = new Random();
        int maxDocumentNumber = 2;
        int randomDocumentNumber = random.nextInt(maxDocumentNumber) + 1;

        getUserPoints(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long points) {
                userPoints = points;
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("GameManager", "Errore nel recupero dei punti dell'utente.", e);
            }
        });

        GameConstants.loadWordsFromFirestore(randomDocumentNumber, difficulty).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String word) {
                if (word != null) {
                    wordToGuess = word;
                    generateUnderscores(wordToGuess);

                    listener.onGameStart(getGameState());
                } else {
                    wordToGuess = "PIPPO";
                    generateUnderscores(wordToGuess);

                    listener.onGameStart(getGameState());
                }
            }
        });
        return null;
    }


    public void generateUnderscores(String word) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char character = word.charAt(i);
            if (character == '/') {
                sb.append('/');
            } else {
                sb.append("_");
            }
        }
        underscoreWord = sb.toString();
    }

    public GameState play(char letter) {
        if (lettersUsed.contains(Character.toString(letter))) {
            return new GameState.Running(lettersUsed, underscoreWord, drawable);
        }

        lettersUsed += letter;
        List<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < wordToGuess.length(); i++) {
            char character = wordToGuess.charAt(i);
            if (Character.toLowerCase(character) == Character.toLowerCase(letter)) {
                indexes.add(i);
            }
        }

        String finalUnderscoreWord = underscoreWord;
        for (Integer index : indexes) {
            StringBuilder sb = new StringBuilder(finalUnderscoreWord);
            sb.setCharAt(index, letter);
            finalUnderscoreWord = sb.toString();
        }

        if (indexes.isEmpty()) {
            currentTries++;
            Log.d("Tentativi", String.valueOf(currentTries));
        } else {
            userPoints++;
            updateUserPoints(userPoints);
        }

        underscoreWord = finalUnderscoreWord;
        return getGameState();
    }


    private int getHangmanDrawable() {
        switch (currentTries) {
            case 0:
                return R.drawable.hangman6;
            case 1:
                return R.drawable.hangman5;
            case 2:
                return R.drawable.hangman4;
            case 3:
                return R.drawable.hangman3;
            case 4:
                return R.drawable.hangman2;
            case 5:
                return R.drawable.hangman1;
            case 6:
                return R.drawable.hangman0;
            default:
                return R.drawable.hangman0;
        }
    }

    private GameState getGameState() {
        if (underscoreWord.equalsIgnoreCase(wordToGuess)) {
            return new GameState.Won(wordToGuess);
        }

        if (currentTries == maxTries) {
            return new GameState.Lost(wordToGuess);
        }

        drawable = getHangmanDrawable();
        return new GameState.Running(lettersUsed, underscoreWord, drawable);
    }
}
