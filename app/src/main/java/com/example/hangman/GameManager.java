package com.example.hangman;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Random;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private String lettersUsed = "";
    private String underscoreWord;
    private String wordToGuess;
    private final int maxTries = 7;
    private int currentTries = 0;
    private int drawable = R.drawable.hangman6;

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
            case 7:
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
