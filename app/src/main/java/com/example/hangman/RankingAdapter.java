package com.example.hangman;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.CollationElementIterator;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {
    private Context context;
    private List<Player> playerList;

    public RankingAdapter(Context context, List<Player> playerList) {
        this.context = context;
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        Player player = playerList.get(position);

        if (player.getPoints() > 4) {
            holder.positionTextView.setText(String.valueOf(position + 1));
            holder.nameTextView.setText(player.getName() + " - " + String.valueOf(player.getPoints()));
            //holder.scoreTextView.setText(String.valueOf(player.getPoints()));

            Context context = holder.itemView.getContext();

            if (position < 3) {
                int color;
                switch (position) {
                    case 0:
                        color = ContextCompat.getColor(context, R.color.gold);
                        break;
                    case 1:
                        color = ContextCompat.getColor(context, R.color.silver);
                        break;
                    case 2:
                        color = ContextCompat.getColor(context, R.color.bronze);
                        break;
                    default:
                        color = ContextCompat.getColor(context, R.color.default_color);
                        break;
                }
                holder.itemView.setBackgroundColor(color);
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.default_color));
            }
        } else {
            holder.itemView.setVisibility(View.GONE);
        }
    }


    /*public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        Player player = playerList.get(position);

        holder.nameTextView.setText(String.valueOf(position + 1) + ". " + player.getName() + " - ");
        holder.scoreTextView.setText(String.valueOf(player.getPoints()));
    }*/

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public class RankingViewHolder extends RecyclerView.ViewHolder {
        public TextView positionTextView;
        public TextView nameTextView;
        //public TextView scoreTextView;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);

            positionTextView = itemView.findViewById(R.id.textPosition);
            nameTextView = itemView.findViewById(R.id.textName);
            //scoreTextView = itemView.findViewById(R.id.textScore);
        }
    }

}
