package com.example.appfirebaselittledemons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.models.Rooms;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private final Context context;
    private final List<Rooms> roomList;
    private final OnRoomSelectedListener onRoomSelectedListener;

    /**  Constructor WITHOUT listener  */
    public RoomAdapter(Context context, List<Rooms> roomList) {
        this.context = context;
        this.roomList = roomList;
        this.onRoomSelectedListener = null; // No listener provided
    }

    /**  NEW Constructor WITH listener */
    public RoomAdapter(Context context, List<Rooms> roomList, OnRoomSelectedListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.onRoomSelectedListener = listener;
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
        TextView textRoomId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textRoomId = itemView.findViewById(R.id.textRoomId);
        }
    }

    /** Interface for Room Selection Callback */
    public interface OnRoomSelectedListener {
        void onRoomSelected(String roomCode);
    }
}
