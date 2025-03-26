package com.example.appfirebaselittledemons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appfirebaselittledemons.R;
import com.example.appfirebaselittledemons.models.Rooms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AdminRoomAdapter extends RecyclerView.Adapter<AdminRoomAdapter.ViewHolder> {
    private final List<Rooms> roomList;
    private final Context context;
    private final Consumer<String> deleteRoomCallback;
    private final Consumer<Set<String>> selectionCallback;
    private final Set<String> selectedRooms = new HashSet<>();

    public AdminRoomAdapter(Context context, List<Rooms> roomList, Consumer<String> deleteRoomCallback, Consumer<Set<String>> selectionCallback) {
        this.context = context;
        this.roomList = roomList;
        this.deleteRoomCallback = deleteRoomCallback;
        this.selectionCallback = selectionCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rooms room = roomList.get(position);
        holder.textRoomId.setText("Room ID: " + room.getId());

        // Handle checkbox state
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedRooms.contains(room.getId()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedRooms.add(String.valueOf(room.getId()));
            } else {
                selectedRooms.remove(room.getId());
            }
            selectionCallback.accept(selectedRooms); // Notify AdminViewActivity
        });

        // Single delete button
        holder.buttonDelete.setOnClickListener(view -> deleteRoomCallback.accept(String.valueOf(room.getId())));
    }
    public void removeRoomById(String roomId) {
        Iterator<Rooms> iterator = roomList.iterator();
        while (iterator.hasNext()) {
            Rooms room = iterator.next();
            if (String.valueOf(room.getId()).equals(roomId)) {
                iterator.remove(); //  Remove room from the list
                break;
            }
        }
        notifyDataSetChanged(); //  Refresh RecyclerView
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public List<Rooms> getSelectedRooms() {
        List<Rooms> selectedRoomList = new ArrayList<>();
        for (Rooms room : roomList) {
            if (selectedRooms.contains(room.getId())) {
                selectedRoomList.add(room);
            }
        }
        return selectedRoomList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textRoomId;
        Button buttonDelete;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textRoomId = itemView.findViewById(R.id.textRoomId);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            checkBox = itemView.findViewById(R.id.checkBoxSelect);
        }
    }
}
