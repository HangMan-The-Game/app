package com.example.hangman;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);

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

                        Collections.sort(playerList, (player1, player2) -> {
                            long points1 = player1.getPoints();
                            long points2 = player2.getPoints();
                            return Long.compare(points2, points1);
                        });

                        adapter.notifyDataSetChanged();
                    } else {
                        // Gestisci eventuali errori
                    }
                });
    }
}
