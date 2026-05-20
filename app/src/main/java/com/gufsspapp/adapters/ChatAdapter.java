package com.gufsspapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gufsspapp.R;
import com.gufsspapp.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {

    private final List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ChatMessage msg = messages.get(position);

        if (msg.isUser()) {
            holder.llUser.setVisibility(View.VISIBLE);
            holder.llAI.setVisibility(View.GONE);
            holder.tvUserMsg.setText(msg.getMessage());
            holder.tvUserTime.setText(msg.getTime());
        } else {
            holder.llUser.setVisibility(View.GONE);
            holder.llAI.setVisibility(View.VISIBLE);
            holder.tvAIMsg.setText(msg.getMessage());
            holder.tvAITime.setText(msg.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        LinearLayout llUser, llAI;
        TextView tvUserMsg, tvUserTime, tvAIMsg, tvAITime;

        VH(View itemView) {
            super(itemView);
            llUser = itemView.findViewById(R.id.llUser);
            llAI = itemView.findViewById(R.id.llAI);
            tvUserMsg = itemView.findViewById(R.id.tvUserMsg);
            tvUserTime = itemView.findViewById(R.id.tvUserTime);
            tvAIMsg = itemView.findViewById(R.id.tvAIMsg);
            tvAITime = itemView.findViewById(R.id.tvAITime);
        }
    }
}
