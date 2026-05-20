package com.gufsspapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gufsspapp.R;
import com.gufsspapp.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(Notification notification);
    }

    private List<Notification> items;
    private OnItemClickListener listener;

    public NotificationAdapter(List<Notification> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateData(List<Notification> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Notification n = items.get(position);
        holder.tvTitle.setText(n.getTitle());
        holder.tvDescription.setText(n.getDescription());
        holder.tvTime.setText(n.getTime());

        // Read dot
        if (n.isRead()) {
            holder.vDot.setVisibility(View.INVISIBLE);
        } else {
            holder.vDot.setVisibility(View.VISIBLE);
        }

        // Important
        if (n.isImportant()) {
            holder.tvTitle.setTextColor(Color.parseColor("#E3902B"));
        } else {
            holder.tvTitle.setTextColor(Color.parseColor("#E6EDF3"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(n);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        View vDot;
        TextView tvTitle, tvDescription, tvTime;

        VH(View itemView) {
            super(itemView);
            vDot = itemView.findViewById(R.id.vDot);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
