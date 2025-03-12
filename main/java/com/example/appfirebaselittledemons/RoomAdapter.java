package com.example.appfirebaselittledemons;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private List<Room> roomList;
    private Context context;

    public RoomAdapter(List<Room> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomId.setText("Room ID: " + room.id);
        holder.playerCount.setText("Players: " + room.getPlayerCount());

        // Click listener to join a room
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, WaitingLobbyActivity.class);
            intent.putExtra("roomCode", String.valueOf(room.id));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomId, playerCount;

        public RoomViewHolder(View itemView) {
            super(itemView);
            roomId = itemView.findViewById(R.id.textRoomId);
            playerCount = itemView.findViewById(R.id.textPlayerCount);
        }
    }
}
