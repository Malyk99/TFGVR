package com.example.appfirebaselittledemons.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.activities.WaitingLobbyActivity;
import com.example.appfirebaselittledemons.models.Rooms;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private final Context context;
    private final List<Rooms> roomList;

    public RoomAdapter(Context context, List<Rooms> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Rooms room = roomList.get(position);
        holder.roomName.setText("Room ID: " + room.getId());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WaitingLobbyActivity.class);
            intent.putExtra("roomCode", String.valueOf(room.getId()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomName;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.textRoomName);
        }
    }
}
