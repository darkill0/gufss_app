package com.gufsspapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gufsspapp.R;
import com.gufsspapp.models.Employee;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.VH> {

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Employee employee);
    }

    private List<Employee> items;
    private OnFavoriteClickListener favoriteListener;

    public EmployeeAdapter(List<Employee> items, OnFavoriteClickListener favoriteListener) {
        this.items = items;
        this.favoriteListener = favoriteListener;
    }

    public void updateData(List<Employee> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_employee, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Employee e = items.get(position);
        holder.tvName.setText(e.getName());
        holder.tvPosition.setText(e.getPosition());
        holder.tvPhone.setText(e.getPhone());
        holder.tvInitials.setText(e.getInitials());

        holder.btnStar.setImageResource(e.isFavorite() ?
                R.drawable.ic_star_filled : R.drawable.ic_star);

        holder.btnStar.setOnClickListener(v -> {
            if (favoriteListener != null) favoriteListener.onFavoriteClick(e);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvInitials, tvName, tvPosition, tvPhone;
        ImageButton btnStar;

        VH(View itemView) {
            super(itemView);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvName = itemView.findViewById(R.id.tvName);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnStar = itemView.findViewById(R.id.btnStar);
        }
    }
}
