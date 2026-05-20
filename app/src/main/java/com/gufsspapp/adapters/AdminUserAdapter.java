package com.gufsspapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gufsspapp.R;
import com.gufsspapp.models.User;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    private List<User> items;
    private OnItemClickListener listener;

    public AdminUserAdapter(List<User> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateData(List<User> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        User u = items.get(position);
        holder.tvInitials.setText(u.getInitials());
        holder.tvName.setText(u.getName());
        holder.tvPosition.setText(u.getPosition());

        String roleDisplay;
        switch (u.getRole()) {
            case "admin": roleDisplay = "Администратор"; break;
            case "head": roleDisplay = "Руководитель"; break;
            default: roleDisplay = "Сотрудник";
        }
        holder.tvRole.setText(roleDisplay);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(u);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvInitials, tvName, tvPosition, tvRole;

        VH(View itemView) {
            super(itemView);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvName = itemView.findViewById(R.id.tvName);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvRole = itemView.findViewById(R.id.tvRole);
        }
    }
}
