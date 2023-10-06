package com.example.hangman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    int points = 0;
    String role = "user";
    int vittorie = 0;

    private boolean passwordShowing = false;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    ProgressBar progressBar;
     EditText email ;
     EditText UserName;
     EditText password;
     ImageView passwordIcon;
     AppCompatButton signUpBtn;
     TextView signInBtn;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(Register.this, MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Ottieni il colore dalla risorsa di colore dell'applicazione
            int statusBarColor = ContextCompat.getColor(this, R.color.white);
            window.setStatusBarColor(statusBarColor);

            View decorView = window.getDecorView();
            int systemUiVisibilityFlags = decorView.getSystemUiVisibility();
            systemUiVisibilityFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(systemUiVisibilityFlags);
        }
        mAuth= FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        email = findViewById(R.id.emailET);
        UserName = findViewById(R.id.fullNameET);

        password = findViewById(R.id.passwordET);
        passwordIcon = findViewById(R.id.passwordIcon);


       signUpBtn = findViewById(R.id.signUpBtn);
      signInBtn = findViewById(R.id.signInBtn);

        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // checking if password is showing or not
                if(passwordShowing){
                    passwordShowing = false;

                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.password_show);
                }
                else{
                    passwordShowing = true;

                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.password_hide);
                }

                // move the cursor at last of the text
                password.setSelection(password.length());
            }
        });

        /* signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String email1 = email.getText().toString();
                String user = UserName.getText().toString();
                String pass = password.getText().toString();

                HelperClass HelperClass = new HelperClass(email1, user, pass);

                reference.child(user).setValue(HelperClass);
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);


            }
        });
        */

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String getPassword = password.getText().toString();
                final String getEmailTxt = email.getText().toString();
                final String getNameTxt = UserName.getText().toString();

                if (TextUtils.isEmpty(getEmailTxt)) {
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(getPassword)) {
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(getEmailTxt, getPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    Map<String, Object> utente = new HashMap<>();
                                    utente.put("mail", getEmailTxt);
                                    utente.put("name", getNameTxt);
                                    utente.put("points", points);
                                    utente.put("role", role);
                                    utente.put("vittorie", vittorie);

                                    db.collection("users")
                                            .document(uid)
                                            .set(utente)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(Register.this, "Registrazione nel database avvenuta con successo", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(Register.this, MainActivity.class);
                                                    intent.putExtra("email", getEmailTxt);
                                                    intent.putExtra("username", getNameTxt);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Register.this, "Errore durante la registrazione nel database Firestore", Toast.LENGTH_SHORT).show();
                                                    Log.e("Firestore", "Errore durante la registrazione nel database Firestore", e);
                                                }
                                            });
                                } else {
                                    Toast.makeText(Register.this, "La registrazione ha fallito.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}