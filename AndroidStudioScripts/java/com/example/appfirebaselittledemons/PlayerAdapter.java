package com.example.appfirebaselittledemons;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
    private List<Player> playerList;
    private DatabaseReference roomRef; // Added Firebase Reference

    // Updated Constructor to accept DatabaseReference
    public PlayerAdapter(List<Player> playerList) {
        this.playerList = playerList;
        this.roomRef = roomRef;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    /*@Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.textPlayerName.setText(player.name);
        holder.textPlayerStatus.setText(player.ready ? "Ready" : "Not Ready");

        //  Fix setTextColor ambiguity using ContextCompat
        int color = player.ready ? ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark)
                : ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark);
        holder.textPlayerStatus.setTextColor(color);

        holder.buttonReady.setText(player.ready ? "Unready" : "Ready");

        //  Fix "Ready" button not updating Firebase
        holder.buttonReady.setOnClickListener(view -> {
            DatabaseReference playerRef = roomRef.child(player.name);
            boolean newReadyState = !player.ready;

            playerRef.child("ready").setValue(newReadyState)
                    .addOnSuccessListener(aVoid -> {
                        player.ready = newReadyState;
                        notifyDataSetChanged(); //  Update UI after state change
                    })
                    .addOnFailureListener(e -> Log.e("FirebaseError", "Failed to update ready state", e));
        });
    }*/
    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.textPlayerName.setText(player.name);
        holder.textPlayerStatus.setText(player.ready ? "Ready" : "Not Ready");

        int color = player.ready ? ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark)
                : ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark);
        holder.textPlayerStatus.setTextColor(color);
    }




    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView textPlayerName, textPlayerStatus;
        Button buttonReady;

        public PlayerViewHolder(View itemView) {
            super(itemView);
            textPlayerName = itemView.findViewById(R.id.textPlayerName);
            textPlayerStatus = itemView.findViewById(R.id.textPlayerStatus);
            buttonReady = itemView.findViewById(R.id.buttonReady);
        }
    }

}
