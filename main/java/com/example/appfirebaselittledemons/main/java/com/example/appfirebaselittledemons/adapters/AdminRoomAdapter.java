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
import com.example.appfirebaselittledemons.firebase.FirebaseAdminManager;
import com.example.appfirebaselittledemons.models.Rooms;
import java.util.List;

public class AdminRoomAdapter extends RecyclerView.Adapter<AdminRoomAdapter.ViewHolder> {
    private final List<Rooms> roomList;
    private final Context context;
    private final FirebaseAdminManager adminManager;
    private final String adminUsername; // Usuario actual del admin

    public AdminRoomAdapter(Context context, List<Rooms> roomList, String adminUsername) {
        this.context = context;
        this.roomList = roomList;
        this.adminManager = new FirebaseAdminManager();
        this.adminUsername = adminUsername;
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

        // Acción del botón para eliminar la sala
        holder.buttonDelete.setOnClickListener(view -> {
            adminManager.deleteRoomIfAdmin(adminUsername, String.valueOf(room.getId()), new FirebaseAdminManager.OnAdminCheckListener() {
                @Override
                public void onSuccess(String message) {
                    roomList.remove(position);
                    notifyItemRemoved(position);
                }

                @Override
                public void onFailure(String error) {
                    // Mostrar error en pantalla si no tiene permisos
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textRoomId;
        Button buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textRoomId = itemView.findViewById(R.id.textRoomId);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
