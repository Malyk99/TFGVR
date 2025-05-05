package com.example.appfirebaselittledemons.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.models.Rooms;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private final Context context;
    private final List<Rooms> roomList;
    private final OnRoomSelectedListener onRoomSelectedListener;

    public RoomAdapter(Context context, List<Rooms> roomList, OnRoomSelectedListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.onRoomSelectedListener = listener;
    }

    public RoomAdapter(Context context, List<Rooms> roomList) {
        this.context = context;
        this.roomList = roomList;
        this.onRoomSelectedListener = null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rooms room = roomList.get(position);
        holder.textRoomId.setText("Room ID: " + room.getId());
        holder.textPlayerCount.setText("Players: " + room.getPlayerCount());

        holder.itemView.setOnClickListener(v -> {
            if (onRoomSelectedListener != null) {
                onRoomSelectedListener.onRoomSelected(String.valueOf(room.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textRoomId, textPlayerCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textRoomId = itemView.findViewById(R.id.textRoomId);
            textPlayerCount = itemView.findViewById(R.id.textPlayerCount);
        }
    }

    public void updatePlayerCounts() {
        for (Rooms room : roomList) {
            String roomId = String.valueOf(room.getId());
            FirebaseDatabase.getInstance()
                    .getReference("rooms")
                    .child(roomId)
                    .child("players")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int playerCount = (int) snapshot.getChildrenCount();
                            room.setPlayerCount(playerCount);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("RoomAdapter", "Failed to load player count for room " + roomId);
                        }
                    });
        }

    }

    public interface OnRoomSelectedListener {
        void onRoomSelected(String roomCode);
    }
}
