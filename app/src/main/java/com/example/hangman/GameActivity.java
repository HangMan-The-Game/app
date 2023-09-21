package com.example.hangman;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

public class GameActivity extends AppCompatActivity implements GameManager.OnGameStartListener {

    private GameManager gameManager = new GameManager();

    private TextView wordTextView;
    private TextView lettersUsedTextView;
    private ImageView imageView;
    private TextView gameLostTextView;
    private TextView gameWonTextView;
    private Button newGameButton;
    private ConstraintLayout lettersLayout;
    String difficulty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gioco);

        imageView = findViewById(R.id.imageView5);
        wordTextView = findViewById(R.id.wordTextView);
        lettersUsedTextView = findViewById(R.id.lettersUsedTextView);
        gameLostTextView = findViewById(R.id.gameLostTextView);
        gameWonTextView = findViewById(R.id.gameWonTextView);

        newGameButton = findViewById(R.id.nuovapartita);
        lettersLayout = findViewById(R.id.lettersLayout);

        difficulty = getIntent().getStringExtra("difficulty");

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGame();
            }
        });


        //Log.d("DIFFICOLTA: ", difficulty);

        GameState gameState = gameManager.startNewGame(this, difficulty);
        updateUI(gameState);

        for (int i = 0; i < lettersLayout.getChildCount(); i++) {
            View letterView = lettersLayout.getChildAt(i);
            if (letterView instanceof TextView) {
                final TextView textView = (TextView) letterView;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GameState gameState = gameManager.play(textView.getText().charAt(0));
                        updateUI(gameState);
                        textView.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    private void updateUI(GameState gameState) {
        if (gameState instanceof GameState.Lost) {
            showGameLost(((GameState.Lost) gameState).getWordToGuess());
        } else if (gameState instanceof GameState.Running) {
            GameState.Running runningState = (GameState.Running) gameState;
            wordTextView.setText(runningState.getUnderscoreWord());
            lettersUsedTextView.setText("Letters used: " + runningState.getLettersUsed());
            imageView.setImageDrawable(ContextCompat.getDrawable(this, runningState.getDrawable()));
        } else if (gameState instanceof GameState.Won) {
            showGameWon(((GameState.Won) gameState).getWordToGuess());
        }
    }

    private void showGameLost(String wordToGuess) {
        wordTextView.setText(wordToGuess);
        gameLostTextView.setVisibility(View.VISIBLE);
        imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hangman0));
        lettersLayout.setVisibility(View.GONE);
    }

    private void showGameWon(String wordToGuess) {
        wordTextView.setText(wordToGuess);
        gameWonTextView.setVisibility(View.VISIBLE);
        gameWonTextView.setVisibility(View.VISIBLE);
        lettersLayout.setVisibility(View.GONE);
    }

    private void startNewGame() {
        gameLostTextView.setVisibility(View.GONE);
        gameWonTextView.setVisibility(View.GONE);
        lettersLayout.setVisibility(View.VISIBLE);
        for (int i = 0; i < lettersLayout.getChildCount(); i++) {
            lettersLayout.getChildAt(i).setVisibility(View.VISIBLE);
        }
        gameManager.startNewGame(this, difficulty);
    }

    @Override
    public void onGameStart(GameState gameState) {
        updateUI(gameState);
    }
}
