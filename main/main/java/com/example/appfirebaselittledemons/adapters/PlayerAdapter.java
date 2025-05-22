package com.example.appfirebaselittledemons.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.models.Players;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
    private List<Players> playerList;

    public PlayerAdapter(List<Players> playerList) {
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Players player = playerList.get(position);
        holder.playerName.setText(player.getName());
        holder.playerStatus.setText(player.isReady() ? "Ready" : "Not Ready");
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public void updateData(List<Players> newList) {
        this.playerList = newList;
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName, playerStatus;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.textPlayerName);
            playerStatus = itemView.findViewById(R.id.textPlayerStatus);
        }
    }
}
