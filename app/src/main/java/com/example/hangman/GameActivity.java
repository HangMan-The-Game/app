package com.example.hangman;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class GameActivity extends AppCompatActivity implements GameManager.OnGameStartListener {

    private GameManager gameManager = new GameManager();
    private TextView wordTextView;
    private TextView lettersUsedTextView;
    private ImageView imageView;
    private TextView gameLostTextView;
    private TextView gameWonTextView;
    private AppCompatButton pointsTextView;
    private Button newGameButton;
    private ConstraintLayout lettersLayout;
    String difficulty;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private ListenerRegistration pointsListener;

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
        pointsTextView = findViewById(R.id.appCompatButton);

        difficulty = getIntent().getStringExtra("difficulty");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        userRef = db.collection("users").document(currentUser.getUid());

        pointsListener = userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Long userPoints = snapshot.getLong("points");
                    if (userPoints != null) {
                        pointsTextView.setText("Punti: " + userPoints);
                    }
                }
            }
        });

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
            lettersUsedTextView.setText("Lettere usate: " + runningState.getLettersUsed());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pointsListener != null) {
            pointsListener.remove();
        }
    }
}
