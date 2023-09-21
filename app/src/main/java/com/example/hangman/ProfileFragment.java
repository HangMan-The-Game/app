package com.example.hangman;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button logoutActivityButton = view.findViewById(R.id.logOutIcon);
        auth = FirebaseAuth.getInstance();

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

        getUserEmailAndName(
                email -> emailTextView.setText(email),
                name -> nameTextView.setText(name),
                e -> {
                    e.printStackTrace();
                }
        );

        return view;
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

     */
}