package com.example.hangman;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LikesFragments extends Fragment {
    private RecyclerView recyclerView;
    private RankingAdapter adapter;
    private List<Player> playerList;
    public TextView topOneName, topOneScore, topTwoName, topTwoScore, topThreeName, topThreeScore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Accedi all'attività ospitante utilizzando getActivity()
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Ottieni il colore dalla risorsa di colore dell'applicazione
            int statusBarColor = ContextCompat.getColor(requireContext(), R.color.red);
            window.setStatusBarColor(statusBarColor);

            View decorView = window.getDecorView();
            int systemUiVisibilityFlags = decorView.getSystemUiVisibility();
            systemUiVisibilityFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(systemUiVisibilityFlags);
        }

        topOneName = view.findViewById(R.id.primoClassifica);
        topOneScore = view.findViewById(R.id.puntiprimoClassifica);
        topTwoName = view.findViewById(R.id.secondoClassifica);
        topTwoScore = view.findViewById(R.id.puntisecondoClassifica);
        topThreeName = view.findViewById(R.id.terzoClassifica);
        topThreeScore = view.findViewById(R.id.puntiterzoClassifica);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playerList = new ArrayList<>();
        adapter = new RankingAdapter(requireContext(), playerList);
        recyclerView.setAdapter(adapter);

        loadPlayerData();

        return view;
    }

    private void loadPlayerData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .orderBy("points", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        playerList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            long points = document.getLong("points");
                            playerList.add(new Player(name, points));
                        }

                        if (!playerList.isEmpty()) {
                            topOneName.setText(playerList.get(0).getName());
                            topOneScore.setText(String.valueOf(playerList.get(0).getPoints()));
                            topTwoName.setText(playerList.get(1).getName());
                            topTwoScore.setText(String.valueOf(playerList.get(1).getPoints()));
                            topThreeName.setText(playerList.get(2).getName());
                            topThreeScore.setText(String.valueOf(playerList.get(2).getPoints()));
                        }

                        Collections.sort(playerList, (player1, player2) -> {
                            long points1 = player1.getPoints();
                            long points2 = player2.getPoints();
                            return Long.compare(points2, points1);
                        });

                        adapter.notifyDataSetChanged();
                    } else {
                    }
                });
    }
}
