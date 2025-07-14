package com.example.myapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages != null ? chatMessages : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        if (message != null) {
            chatMessages.add(message);
            notifyItemInserted(chatMessages.size() - 1);
        }
    }

    public void updateMessage(int position, ChatMessage message) {
        if (position >= 0 && position < chatMessages.size() && message != null) {
            chatMessages.set(position, message);
            notifyItemChanged(position);
        }
    }

    public void clearMessages() {
        chatMessages.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutUserMessage;
        private LinearLayout layoutAssistantMessage;
        private TextView tvUserMessage;
        private TextView tvAssistantMessage;
        private TextView tvTimestamp;
        private ProgressBar progressBarLoading;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        private void initViews() {
            layoutUserMessage = itemView.findViewById(R.id.layoutUserMessage);
            layoutAssistantMessage = itemView.findViewById(R.id.layoutAssistantMessage);
            tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
            tvAssistantMessage = itemView.findViewById(R.id.tvAssistantMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            progressBarLoading = itemView.findViewById(R.id.progressBarLoading);
        }

        public void bind(ChatMessage message) {
            // Hide all layouts first
            layoutUserMessage.setVisibility(View.GONE);
            layoutAssistantMessage.setVisibility(View.GONE);
            tvTimestamp.setVisibility(View.GONE);
            progressBarLoading.setVisibility(View.GONE);

            if (message == null) {
                android.util.Log.w("ChatAdapter", "Binding null message");
                return;
            }

            android.util.Log.d("ChatAdapter", "Binding message: " + message.toString());

            if (message.isFromUser()) {
                // Show user message
                layoutUserMessage.setVisibility(View.VISIBLE);
                tvUserMessage.setText(message.getContent());
                
                android.util.Log.d("ChatAdapter", "Showing user message: " + message.getContent());
            } else if (message.isFromAssistant()) {
                // Show assistant message
                layoutAssistantMessage.setVisibility(View.VISIBLE);
                
                if (message.isLoading()) {
                    // Show loading state
                    tvAssistantMessage.setText(message.getContent());
                    progressBarLoading.setVisibility(View.VISIBLE);
                    android.util.Log.d("ChatAdapter", "Showing loading message: " + message.getContent());
                } else {
                    // Show normal assistant message
                    tvAssistantMessage.setText(message.getContent());
                    progressBarLoading.setVisibility(View.GONE);
                    android.util.Log.d("ChatAdapter", "Showing assistant message: " + message.getContent());
                }
            }

            // Show timestamp if available (optional)
            if (message.getTimestamp() != null && !message.getTimestamp().isEmpty()) {
                try {
                    long timestamp = Long.parseLong(message.getTimestamp());
                    String formattedTime = timeFormat.format(new Date(timestamp));
                    tvTimestamp.setText(formattedTime);
                    tvTimestamp.setVisibility(View.VISIBLE);
                } catch (NumberFormatException e) {
                    // If timestamp is not a number, try to display as is
                    tvTimestamp.setText(message.getTimestamp());
                    tvTimestamp.setVisibility(View.VISIBLE);
                }
            }
        }
    }
} 