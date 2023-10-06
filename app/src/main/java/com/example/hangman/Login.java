package com.example.hangman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private boolean passwordShowing = false;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
     EditText Email;
     EditText passwordET;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(Login.this, MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        Email = findViewById(R.id.emailLG);
        passwordET = findViewById(R.id.passwordET);

        final ImageView passwordIcon = findViewById(R.id.passwordIcon);
        final TextView signUpBtn = findViewById(R.id.signUpBtn);
        Button Login = findViewById(R.id.signInBtn);



        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // checking if password is showing or not
                if(passwordShowing){
                    passwordShowing = false;

                    passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.password_show);
                }
                else{
                    passwordShowing = true;

                    passwordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.password_hide);
                }

                // move the cursor at last of the text
                passwordET.setSelection(passwordET.length());
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()) {
                } else {
                    checkUser();
                }
            }
        });




        Login.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 progressBar.setVisibility(View.VISIBLE);
                 final String getPassword = passwordET.getText().toString();
                 final String getEmailTxt = Email.getText().toString();

                 if (TextUtils.isEmpty(getEmailTxt)) {
                     Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
                     return;
                 }

                 if (TextUtils.isEmpty(getPassword)) {
                     Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 mAuth.signInWithEmailAndPassword(getEmailTxt, getPassword)
                         .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                             @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {
                                 progressBar.setVisibility(View.GONE);
                                 if (task.isSuccessful()) {
                                     Toast.makeText(getApplicationContext(), "Login Succeseful", Toast.LENGTH_SHORT).show();
                                     startActivity(new Intent(Login.this, MainActivity.class));
                                 } else {
                                     // If sign in fails, display a message to the user.
                                     Toast.makeText(Login.this, "Auth fallita", Toast.LENGTH_SHORT).show();
                                 }
                             }
                         });



             }
         }
        );


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    public Boolean validateUsername() {
        String val = Email.getText().toString();
        if (val.isEmpty()) {
            Email.setError("Email cannot be empty");
            return false;
        } else {
            Email.setError(null);
            return true;
        }
    }
    public Boolean validatePassword(){
        String val = passwordET.getText().toString();
        if (val.isEmpty()) {
            passwordET.setError("Password cannot be empty");
            return false;
        } else {
            passwordET.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String userUsername = Email.getText().toString().trim();
        String userPassword = passwordET.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Email.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userPassword)) {
                        Email.setError(null);
                        String nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                        String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                        String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("password", passwordFromDB);
                        startActivity(intent);
                    } else {
                        passwordET.setError("Invalid Credentials");
                        passwordET.requestFocus();
                    }
                } else {
                    Email.setError("User does not exist");
                    Email.requestFocus();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}