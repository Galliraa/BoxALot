package com.example.kenne.box_a_lot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kenne.box_a_lot.models.ChatMessage;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ContactStorage extends AppCompatActivity {


    private RecyclerView mChatRV;
    private FirestoreRecyclerAdapter<ChatMessage, ChatHolder> mChatRVAdapter;
    private static final int SIGN_IN_REQUEST_CODE = 1;
    private  String stId;
    private Boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_storage);

        mChatRV = findViewById(R.id.list_of_messages);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);

        stId = getIntent().getStringExtra("storageRoomId");

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
            // Load chat room contents
            loggedIn = true;
            displayChatMessages();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setTimestampsInSnapshotsEnabled(true)
                        .build();
                db.setFirestoreSettings(settings);
                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database

                // Add a new document with a generated ID
                db
                        .collection("chats")
                        .document(stId)
                        .collection(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .add(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName()));

                // Clear the input
                input.setText("");
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                loggedIn = true;
                displayChatMessages();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(loggedIn)
            mChatRVAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(loggedIn)
            mChatRVAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ContactStorage.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();
                            loggedIn = false;

                            // Close activity
                            finish();
                        }
                    });
        }
        return true;
    }

    private void displayChatMessages() {

        Query query = FirebaseFirestore.getInstance()
                .collection("chats")
                .document(getIntent().getStringExtra("storageRoomId"))
                .collection(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("messageTime");

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();


            mChatRV.setLayoutManager(new LinearLayoutManager(this));


            mChatRVAdapter = new FirestoreRecyclerAdapter<ChatMessage, ChatHolder>(options) {

                private ArrayList<ChatMessage> mDataset;
            @Override
            protected void onBindViewHolder(ChatHolder holder, int position, ChatMessage model) {
                holder.messageText.setText(model.getMessageText());
                holder.messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
                holder.messageUser.setText(model.getMessageUser());
            }

            @Override
            public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message, parent, false);

                return new ChatHolder(view);
            }
        };
        mChatRV.setAdapter(mChatRVAdapter);
    }

    public class ChatHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView messageText;
        public TextView messageUser;
        public TextView messageTime;

        public ChatHolder(View v) {
            super(v);
            messageText = v.findViewById(R.id.message_text);
            messageUser = v.findViewById(R.id.message_user);
            messageTime = v.findViewById(R.id.message_time);
        }
    }
}
