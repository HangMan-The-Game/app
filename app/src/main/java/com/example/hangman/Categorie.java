package com.example.hangman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Categorie extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorie);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Ottieni il colore dalla risorsa di colore dell'applicazione
            int statusBarColor = ContextCompat.getColor(this, R.color.red);
            window.setStatusBarColor(statusBarColor);

            View decorView = window.getDecorView();
            int systemUiVisibilityFlags = decorView.getSystemUiVisibility();
            systemUiVisibilityFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(systemUiVisibilityFlags);
        }
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