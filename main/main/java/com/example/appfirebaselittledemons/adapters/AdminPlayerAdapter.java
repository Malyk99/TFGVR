package com.example.appfirebaselittledemons.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.firebase.FirebaseDataManager;
import com.example.appfirebaselittledemons.models.Players;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminPlayerAdapter extends RecyclerView.Adapter<AdminPlayerAdapter.PlayerViewHolder> {

    private final String roomCode;
    private final List<Players> playerList;
    private final Context context;
    private final Runnable refreshCallback;

    public AdminPlayerAdapter(String roomCode, List<Players> playerList, Context context, Runnable refreshCallback) {
        this.roomCode = roomCode;
        this.playerList = playerList;
        this.context = context;
        this.refreshCallback = refreshCallback;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Players player = playerList.get(position);
        holder.textUsername.setText(player.getName());

        holder.buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Remove Player")
                    .setMessage("Are you sure you want to remove " + player.getName() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseDatabase.getInstance()
                                .getReference("rooms")
                                .child(roomCode)
                                .child("players")
                                .child(player.getId())
                                .removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Player removed", Toast.LENGTH_SHORT).show();
                                    if (refreshCallback != null) refreshCallback.run();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Error removing player", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername;
        Button buttonDelete;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.textUsername);
            buttonDelete = itemView.findViewById(R.id.buttonDeletePlayer);
        }
    }
}
