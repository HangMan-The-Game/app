package com.example.hangman;

public abstract class GameState {

    public static final class Running extends GameState {
        private final String lettersUsed;
        private final String underscoreWord;
        private final int drawable;

        public Running(String lettersUsed, String underscoreWord, int drawable) {
            this.lettersUsed = lettersUsed;
            this.underscoreWord = underscoreWord;
            this.drawable = drawable;
        }

        public String getLettersUsed() {
            return lettersUsed;
        }

        public String getUnderscoreWord() {
            return underscoreWord;
        }

        public int getDrawable() {
            return drawable;
        }
    }

    public static final class Lost extends GameState {
        private final String wordToGuess;

        public Lost(String wordToGuess) {
            this.wordToGuess = wordToGuess;
        }

        public String getWordToGuess() {
            return wordToGuess;
        }
    }

    public static final class Won extends GameState {
        private final String wordToGuess;

        public Won(String wordToGuess) {
            this.wordToGuess = wordToGuess;
        }

        public String getWordToGuess() {
            return wordToGuess;
        }
    }
}
