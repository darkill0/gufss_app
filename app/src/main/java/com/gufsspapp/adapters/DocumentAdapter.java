package com.gufsspapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gufsspapp.R;
import com.gufsspapp.models.Document;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(Document document);
    }

    private List<Document> items;
    private OnItemClickListener listener;

    public DocumentAdapter(List<Document> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateData(List<Document> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Document d = items.get(position);
        holder.tvTitle.setText(d.getTitle());
        holder.tvDescription.setText(d.getDescription());
        holder.tvDate.setText(d.getCreatedDate());
        holder.ivDocIcon.setImageResource(R.drawable.ic_pdf);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(d);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivDocIcon;
        TextView tvTitle, tvDescription, tvDate;

        VH(View itemView) {
            super(itemView);
            ivDocIcon = itemView.findViewById(R.id.ivDocIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
