package com.example.hangman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Categorie extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorie);
    }
    public void startGameWithDifficulty(String difficulty) {
        Intent vaialgioco = new Intent(Categorie.this, GameActivity.class);
        vaialgioco.putExtra("difficulty", difficulty);
        startActivity(vaialgioco);
    }

    public void facile(View v) {
        Log.d("DIFFICOLTA: ", "Facile");
        startGameWithDifficulty("Facile");
    }

    public void medio(View v) {
        startGameWithDifficulty("Medio");
    }

    public void difficile(View v) {
        startGameWithDifficulty("Difficile");
    }

}