package com.example.myapplication.ui.fragment.customer;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.models.ChatMessage;
import com.example.myapplication.ui.adapter.ChatAdapter;
import com.example.myapplication.ui.viewmodel.customer.ChatViewModel;
import com.example.myapplication.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";

    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;
    private SessionManager sessionManager;

    // UI Components
    private RecyclerView recyclerViewMessages;
    private LinearLayout layoutEmptyChat;
    private ProgressBar progressBarLoading;
    private TextView tvChatStatus;
    private TextInputEditText etMessage;
    private FloatingActionButton fabSend;
    private ImageView ivClearChat;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initComponents();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
        loadChatHistory();
    }

    private void initViews(View view) {
        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        layoutEmptyChat = view.findViewById(R.id.layoutEmptyChat);
        progressBarLoading = view.findViewById(R.id.progressBarLoading);
        tvChatStatus = view.findViewById(R.id.tvChatStatus);
        etMessage = view.findViewById(R.id.etMessage);
        fabSend = view.findViewById(R.id.fabSend);
        ivClearChat = view.findViewById(R.id.ivClearChat);
    }

    private void initComponents() {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        sessionManager = SessionManager.getInstance(requireContext());
        chatAdapter = new ChatAdapter();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // Start from bottom
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        // Send button click
        fabSend.setOnClickListener(v -> sendMessage());

        // Clear chat button
        ivClearChat.setOnClickListener(v -> clearChat());

        // Send message on Enter key press
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                sendMessage();
                return true;
            }
            return false;
        });

        // Auto-scroll to bottom when new message is added
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                scrollToBottom();
            }
        });
    }

    private void observeViewModel() {
        // Observe chat messages
        chatViewModel.getChatMessages().observe(getViewLifecycleOwner(), this::updateChatDisplay);

        // Observe loading state
        chatViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    tvChatStatus.setText("Đang tải...");
                } else {
                    tvChatStatus.setText("Luôn sẵn sàng hỗ trợ bạn");
                }
            }
        });

        // Observe message sending state
        chatViewModel.getIsMessageSending().observe(getViewLifecycleOwner(), isSending -> {
            if (isSending != null) {
                fabSend.setEnabled(!isSending);
                if (isSending) {
                    tvChatStatus.setText("Đang gửi tin nhắn...");
                } else {
                    tvChatStatus.setText("Luôn sẵn sàng hỗ trợ bạn");
                }
            }
        });

        // Observe error messages
        chatViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showError(errorMessage);
                chatViewModel.clearError();
            }
        });
    }

    private void loadChatHistory() {
        String userId = sessionManager.getUserId();
        if (userId != null && !userId.isEmpty()) {
            android.util.Log.d(TAG, "Loading chat history for user: " + userId);
            chatViewModel.loadChatHistory(userId);
        } else {
            android.util.Log.e(TAG, "User ID is null or empty, cannot load chat history");
            showError("Không thể xác định người dùng");
        }
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        
        if (messageText.isEmpty()) {
            showError("Vui lòng nhập tin nhắn");
            return;
        }

        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            showError("Không thể xác định người dùng");
            return;
        }

        android.util.Log.d(TAG, "Sending message: " + messageText);
        
        // Send message through ViewModel
        chatViewModel.sendMessage(userId, messageText);
        
        // Clear input field
        etMessage.setText("");
        
        // Hide soft keyboard
        etMessage.clearFocus();
    }

    private void clearChat() {
        android.util.Log.d(TAG, "Clearing chat");
        chatViewModel.clearMessages();
        showEmptyState();
    }

    private void updateChatDisplay(List<ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            showEmptyState();
        } else {
            showChatContent(messages);
        }
    }

    private void showEmptyState() {
        layoutEmptyChat.setVisibility(View.VISIBLE);
        recyclerViewMessages.setVisibility(View.GONE);
    }

    private void showChatContent(List<ChatMessage> messages) {
        layoutEmptyChat.setVisibility(View.GONE);
        recyclerViewMessages.setVisibility(View.VISIBLE);
        
        chatAdapter.setChatMessages(messages);
        scrollToBottom();
        
        android.util.Log.d(TAG, "Displaying " + messages.size() + " messages");
    }

    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            recyclerViewMessages.post(() -> {
                recyclerViewMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            });
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
        android.util.Log.e(TAG, "Error: " + message);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh chat when fragment becomes visible
        if (chatAdapter.getItemCount() == 0) {
            loadChatHistory();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up if needed
    }
} 