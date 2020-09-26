package in.curies.alpha;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import in.curies.alpha.model.Message;
import in.curies.alpha.model.User;

import static in.curies.alpha.utils.Utilities.getChatId;

public class ChatActivity extends AppCompatActivity {

    EditText sendTo;
    EditText messageComposed;
    Button loadChat;
    Button send;
    RecyclerView recyclerView;
    String currentChatId;
    User user;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("Db User");

        Drawable drawable = null;
        try {
            InputStream ims = getAssets().open("bg.png");
            drawable = Drawable.createFromStream(ims, null);
            drawable.setAlpha(100);
        } catch (IOException ex) {
            Log.d("LoadingImage", "Error reading the image");
        }
        findViewById(R.id.relativeLayout).setBackground(drawable);

        sendTo = findViewById(R.id.send_to);
        messageComposed = findViewById(R.id.compose_message);
        loadChat = findViewById(R.id.loadChat);
        send = findViewById(R.id.send);
        recyclerView = findViewById(R.id.messages);

        send.setClickable(false);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        loadChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendTo.getText().toString().isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Please enter recipient", Toast.LENGTH_SHORT).show();
                } else if (sendTo.getText().toString().length() != 10) {
                    Toast.makeText(ChatActivity.this, "Please enter valid 10 digit number", Toast.LENGTH_SHORT).show();
                } else {
                    checkPermissionToChatAndLoad(sendTo.getText().toString());
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentChatId.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Please Load a chat to start messaging", Toast.LENGTH_SHORT).show();
                }
                Date time = new Date();
                Message message = new Message(messageComposed.getText().toString(), user.getPhoneNumber(), new Date());
                databaseReference.child("chats").child(currentChatId).child("" + time.getTime())
                        .setValue(message);
                messageComposed.setText("");
            }
        });
    }

    private void checkPermissionToChatAndLoad(String number) {
        databaseReference.child("users").child("+91" + number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    Toast.makeText(ChatActivity.this, " User does not exist",Toast.LENGTH_LONG).show();
                } else {
                        currentChatId = getChatId(user.getPhoneNumber(), sendTo.getText().toString());
                        databaseReference.child("chats").child(currentChatId).limitToLast(100)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        GenericTypeIndicator<Map<String, Map<String, Object>>> t
                                                = new GenericTypeIndicator<Map<String, Map<String, Object>>>() {
                                        };
                                        Map<String, Map<String, Object>> messages = dataSnapshot.getValue(t);
                                        TreeMap<String, Map<String, Object>> sortedMessages;
                                        if (messages == null) {
                                            Map<String, Object> innerMap = new HashMap<>();
                                            innerMap.put("from", "dummy from");
                                            innerMap.put("message", "No messages yet, start a conversation");
                                            messages = new HashMap<>();
                                            messages.put("123", innerMap);
                                        }
                                        sortedMessages = new TreeMap<>(messages);
                                        initRecyclerView(sortedMessages);
                                        messageComposed.requestFocus();
                                        send.setClickable(true);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initRecyclerView(final Map<String, Map<String, Object>> messages) {

        if (messages.isEmpty())
            return;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mRecentLayoutManager = new LinearLayoutManager(this);
        mRecentLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mRecentLayoutManager);
        final List<String> keys = new ArrayList<>(messages.keySet());

        final RecyclerView.Adapter<CustomViewHolder> mAdapter = new RecyclerView.Adapter<CustomViewHolder>() {
            @Override
            public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.message_thread, viewGroup, false);
                return new CustomViewHolder(view);
            }

            @Override
            public void onBindViewHolder(CustomViewHolder viewHolder, int i) {
                Map<String, Object> messageMap = messages.get(keys.get(i));
                if (messageMap.get("from").equals(user.getPhoneNumber())) {
                    viewHolder.recycleRelativeLayout.setBackgroundColor(Color.parseColor("#9EF498"));
                    viewHolder.my_text.setText((String) messageMap.get("message"));
                    viewHolder.his_text.setVisibility(View.GONE);
                } else {
                    viewHolder.his_text.setText((String) messageMap.get("message"));
                    viewHolder.my_text.setVisibility(View.GONE);
                }
                viewHolder.time.setText((String) messageMap.get("time"));
            }

            @Override
            public int getItemCount() {
                return messages.size();
            }

        };

        recyclerView.setAdapter(mAdapter);
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView my_text;
        private TextView his_text;
        private TextView time;
        private RelativeLayout recycleRelativeLayout;


        public CustomViewHolder(View itemView) {
            super(itemView);
            recycleRelativeLayout = itemView.findViewById(R.id.recycleRelativeLayout);
            my_text = (TextView) itemView.findViewById(R.id.message_text_mine);
            his_text = (TextView) itemView.findViewById(R.id.message_text);
            time = (TextView) itemView.findViewById(R.id.time);
        }
    }
}
