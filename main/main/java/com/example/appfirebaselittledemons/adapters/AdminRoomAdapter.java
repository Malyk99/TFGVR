package com.example.appfirebaselittledemons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.models.Rooms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminRoomAdapter extends RecyclerView.Adapter<AdminRoomAdapter.ViewHolder> {

    private final Context context;
    private final List<Rooms> roomList;
    private final OnRoomDeleteListener deleteListener;
    private final OnSelectionChangeListener selectionListener;
    private final OnRoomClickListener clickListener;
    private final Set<String> selectedRoomIds = new HashSet<>();

    public AdminRoomAdapter(Context context,
                            List<Rooms> roomList,
                            OnRoomDeleteListener deleteListener,
                            OnSelectionChangeListener selectionListener,
                            OnRoomClickListener clickListener) {
        this.context = context;
        this.roomList = roomList;
        this.deleteListener = deleteListener;
        this.selectionListener = selectionListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rooms room = roomList.get(position);
        String roomId = String.valueOf(room.getId());
        holder.textRoomId.setText("Room ID: " + roomId);
        holder.checkBox.setChecked(selectedRoomIds.contains(roomId));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectedRoomIds.add(roomId);
            else selectedRoomIds.remove(roomId);
            selectionListener.onSelectionChanged(selectedRoomIds);
        });

        // ✅ Handle item click to open player management view
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onRoomClicked(roomId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public List<Rooms> getSelectedRooms() {
        return roomList;
    }

    public void removeRoomById(String roomId) {
        roomList.removeIf(room -> String.valueOf(room.getId()).equals(roomId));
        notifyDataSetChanged();
    }

    public interface OnRoomDeleteListener {
        void onDeleteRoom(String roomId);
    }

    public interface OnSelectionChangeListener {
        void onSelectionChanged(Set<String> selected);
    }

    public interface OnRoomClickListener {
        void onRoomClicked(String roomCode);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textRoomId;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textRoomId = itemView.findViewById(R.id.textRoomId);
            checkBox = itemView.findViewById(R.id.checkBoxSelect);
        }
    }
}
