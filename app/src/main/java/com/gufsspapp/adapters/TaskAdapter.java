package com.gufsspapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gufsspapp.R;
import com.gufsspapp.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.VH> {

    public interface OnItemMenuListener {
        void onMenuClick(Task task, View anchor);
    }

    private List<Task> items;
    private OnItemMenuListener menuListener;

    public TaskAdapter(List<Task> items, OnItemMenuListener menuListener) {
        this.items = items;
        this.menuListener = menuListener;
    }

    public void updateData(List<Task> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Task t = items.get(position);
        Context ctx = holder.itemView.getContext();

        holder.tvTitle.setText(t.getTitle());
        holder.tvDescription.setText(t.getDescription());
        holder.tvDeadline.setText(t.getDeadline());

        // Status tag
        setStatusTag(holder.tvStatus, t.getStatus(), ctx);

        holder.btnMore.setOnClickListener(v -> {
            if (menuListener != null) menuListener.onMenuClick(t, v);
        });
    }

    private void setStatusTag(TextView tv, String status, Context ctx) {
        switch (status) {
            case "urgent":
                tv.setText(ctx.getString(R.string.urgent));
                tv.setTextColor(Color.parseColor("#DA3633"));
                tv.setBackgroundResource(R.drawable.tag_urgent);
                break;
            case "important":
                tv.setText(ctx.getString(R.string.important_tag));
                tv.setTextColor(Color.parseColor("#E3902B"));
                tv.setBackgroundResource(R.drawable.tag_important);
                break;
            case "in_progress":
                tv.setText(ctx.getString(R.string.in_work));
                tv.setTextColor(Color.parseColor("#388BFD"));
                tv.setBackgroundResource(R.drawable.tag_work);
                break;
            case "done":
                tv.setText(ctx.getString(R.string.done));
                tv.setTextColor(Color.parseColor("#2EA043"));
                tv.setBackgroundResource(R.drawable.tag_done);
                break;
            default:
                tv.setText(status);
                tv.setTextColor(Color.parseColor("#8B949E"));
                tv.setBackground(null);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvStatus, tvDeadline;
        ImageButton btnMore;

        VH(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
