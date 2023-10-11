package com.example.hangman;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Altri codici di inizializzazione...

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarColor = ContextCompat.getColor(requireContext(), R.color.white);
            window.setStatusBarColor(statusBarColor);

            View decorView = window.getDecorView();
            int systemUiVisibilityFlags = decorView.getSystemUiVisibility();
            systemUiVisibilityFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(systemUiVisibilityFlags);
        }
    }

    private void getUserEmailAndName(final OnSuccessListener<String> emailListener, final OnSuccessListener<String> nameListener, final OnFailureListener failureListener) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d("getUserEmailAndName", "Metodo chiamato con UID: " + uid);

        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String email = documentSnapshot.getString("mail");
                    String name = documentSnapshot.getString("name");

                    if (email != null && name != null) {
                        emailListener.onSuccess(email);
                        nameListener.onSuccess(name);
                        Log.d("EMAIL", email);
                        Log.d("NAME", name);
                    } else {
                        failureListener.onFailure(new Exception("Email o nome mancante"));
                    }
                } else {
                    failureListener.onFailure(new Exception("Documento non trovato"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                failureListener.onFailure(e);
            }
        });
    }

    private void getUserPointsAndWins(final OnSuccessListener<Double> pointsListener, final OnSuccessListener<Integer> winsListener, final OnFailureListener failureListener) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d("getUserPointsAndWins", "Metodo chiamato con UID: " + uid);

        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Double points = documentSnapshot.getDouble("points");
                    Integer wins = documentSnapshot.getLong("vittorie").intValue();

                    if (points != null && wins != null) {
                        pointsListener.onSuccess(points);
                        winsListener.onSuccess(wins);
                        Log.d("POINTS", String.valueOf(points));
                        Log.d("WINS", String.valueOf(wins));
                    } else {
                        failureListener.onFailure(new Exception("Punti o vittorie mancanti"));
                    }
                } else {
                    failureListener.onFailure(new Exception("Documento non trovato"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                failureListener.onFailure(e);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button logoutActivityButton = view.findViewById(R.id.logOutIcon);
        Button verificamail = view.findViewById(R.id.textSizeIcon);
        auth = FirebaseAuth.getInstance();
        AppCompatButton popupbutton = view.findViewById(R.id.popupbtn);
        popupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopup(view); // Chiama il metodo ShowPopup quando il pulsante viene premuto
            }
        });

        final TextView emailTextView = view.findViewById(R.id.email);
        final TextView nameTextView = view.findViewById(R.id.username);


        logoutActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });

        verificamail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()) {
                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(),
                                                "Email di verifica inviata " + FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                                Toast.LENGTH_SHORT).show();
                                        Log.d("Verification", "Email di verifica inviata " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    } else {
                                        Log.e("Mail", "sendEmailVerification", task.getException());
                                        Toast.makeText(getActivity(),
                                                "Email di verifica non inviata con successo.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        getUserEmailAndName(
                email -> emailTextView.setText(email),
                name -> nameTextView.setText(name),
                e -> {
                    e.printStackTrace();
                }
        );

        return view;
    }


    public void ShowPopup(View view) {
        Dialog myDialog = new Dialog(requireContext());
        myDialog.setContentView(R.layout.popup);
        Button btn = myDialog.findViewById(R.id.chiudipopup);
        final TextView puntiTextView = myDialog.findViewById(R.id.npunti);
        final TextView vittorieTextView = myDialog.findViewById(R.id.nvittorie);

        getUserPointsAndWins(
                points -> {
                    if (puntiTextView != null) {
                        puntiTextView.setText(String.valueOf(points));
                    }
                },
                wins -> {
                    if (vittorieTextView != null) {
                        vittorieTextView.setText(String.valueOf(wins));
                    }
                },
                e -> {
                    e.printStackTrace();
                }
        );

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

}
