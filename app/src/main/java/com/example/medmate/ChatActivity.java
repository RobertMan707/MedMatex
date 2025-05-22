// ChatActivity.java
package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList = new ArrayList<>();
    private BottomNavigationView bottomNav;

    private static final String GEMINI_API_KEY = "AIzaSyAC1AY-8PaorKscv_es8wyQmojrGU2RwIY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        bottomNav = findViewById(R.id.nav_menu);

        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        setupBottomNavigation();

        sendButton.setOnClickListener(v -> {
            String userMessage = messageInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                messageList.add(new ChatMessage(userMessage, true));
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
                messageInput.setText("");

                getGeminiResponse(userMessage, response -> {
                    messageList.add(new ChatMessage(response, false));
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    chatRecyclerView.scrollToPosition(messageList.size() - 1);
                });
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_chat);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(ChatActivity.this, Home.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(ChatActivity.this, AddActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_chest) {
                startActivity(new Intent(ChatActivity.this, ChestActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_chat) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(ChatActivity.this, Profile.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void getGeminiResponse(String message, GeminiCallback callback) {
        GeminiRequest.Part part = new GeminiRequest.Part(message);
        List<GeminiRequest.Part> parts = new ArrayList<>();
        parts.add(part);

        GeminiRequest.Content content = new GeminiRequest.Content("user", parts);
        List<GeminiRequest.Content> contents = new ArrayList<>();
        contents.add(content);

        GeminiRequest request = new GeminiRequest(contents);



        GeminiApiClient.getService()
                .generateContent(
                        "gemini-2.0-flash",
                        GEMINI_API_KEY,
                        request
                )
                .enqueue(new Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String reply = response.body().candidates.get(0).content.parts.get(0).text;
                                callback.onResponse(reply);
                            } catch (Exception e) {
                                callback.onResponse("Error parsing response");
                                Log.e("API_ERROR", "Parsing error: " + e.getMessage());
                            }
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e("API_ERROR", "Error reading error body: " + e.getMessage());
                            }
                            callback.onResponse("API Error: " + response.code() + " " + response.message());
                            Log.e("API_ERROR", "Code: " + response.code() + " Message: " + response.message() + " Error Body: " + errorBody);
                        }
                    }

                    @Override
                    public void onFailure(Call<GeminiResponse> call, Throwable t) {
                        callback.onResponse("Network Error: " + t.getMessage());
                        Log.e("NETWORK_ERROR", t.getMessage(), t);
                    }
                });
    }


    private static class ChatMessage {
        private final String text;
        private final boolean isUser;

        public ChatMessage(String text, boolean isUser) {
            this.text = text;
            this.isUser = isUser;
        }

        public String getText() {
            return text;
        }

        public boolean isUser() {
            return isUser;
        }
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private final List<ChatMessage> messages;

        public ChatAdapter(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMessage message = messages.get(position);
            holder.messageText.setText(message.getText());
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        static class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView messageText;

            ChatViewHolder(View itemView) {
                super(itemView);
                messageText = itemView.findViewById(android.R.id.text1);
            }
        }
    }

    private interface GeminiCallback {
        void onResponse(String response);
    }
}