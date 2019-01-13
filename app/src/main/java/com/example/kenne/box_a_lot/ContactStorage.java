package com.example.kenne.box_a_lot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kenne.box_a_lot.adapters.MessageListAdapter;
import com.example.kenne.box_a_lot.models.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactStorage extends AppCompatActivity {


    private static final String TAG = "ContactActivity";

    private static final int LOGIN_REQUEST = 1;
    private static final String MESSAGES_CHILD = "Chats/";
    private static final int REQUEST_IMAGE = 2;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private ProgressBar mProgressBar;
    private LinearLayoutManager mLinearLayoutManager;
    private Button mSendButton;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;

    private String mUsername;
    private String mPhotoUrl = null;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder>  mFirebaseAdapter;

    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

    private RecyclerView mMessageRecyclerView;
    private String storageID;
    String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent data = getIntent();

        setContentView(R.layout.activity_contact);


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.contactRV);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        storageID = data.getStringExtra("storageRoomId");
        Uid = data.getStringExtra("UserId");


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivityForResult(new Intent(this, LoginActivity.class),LOGIN_REQUEST);

            return;
        } else {
            //mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }

            // New child entries
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
                @Override
                public ChatMessage parseSnapshot(DataSnapshot dataSnapshot) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    if (chatMessage != null) {
                        chatMessage.setId(dataSnapshot.getKey());
                    }
                    return chatMessage;
                }
            };



            DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD + storageID + "/" + mFirebaseUser.getUid());
            DatabaseReference checkRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD + storageID + "/");
            checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.hasChild(mFirebaseUser.getUid())) {
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            FirebaseRecyclerOptions<ChatMessage> options =
                    new FirebaseRecyclerOptions.Builder<ChatMessage>()
                            .setQuery(messagesRef, parser)
                            .build();
            mFirebaseAdapter = new MessageListAdapter(options, this);

            ((RecyclerView.Adapter)((Object)mFirebaseAdapter)).registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int chatMessageCount = mFirebaseAdapter.getItemCount();
                    int lastVisiblePosition =
                            mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                    // If the recycler view is initially being loaded or the
                    // user is at the bottom of the list, scroll to the bottom
                    // of the list to show the newly added message.
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (chatMessageCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))) {
                        mMessageRecyclerView.scrollToPosition(positionStart);
                    }
                }
            });

            mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
            mMessageRecyclerView.setAdapter((RecyclerView.Adapter) ((Object)mFirebaseAdapter));

            mMessageEditText = (EditText) findViewById(R.id.messageEditText);

            mMessageEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 0) {
                        mSendButton.setEnabled(true);
                    } else {
                        mSendButton.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            mSendButton = (Button) findViewById(R.id.sendButton);
            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatMessage chatMessage = new
                            ChatMessage(mMessageEditText.getText().toString(),
                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                            mPhotoUrl,
                            null /* no image */,
                            mFirebaseUser.getUid(),
                            System.currentTimeMillis());
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD + storageID + "/" + mFirebaseUser.getUid())
                            .push().setValue(chatMessage);
                    mMessageEditText.setText("");
                }
            });

            mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
            mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_IMAGE);
                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == LOGIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
        else if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());

                    ChatMessage tempMessage = new ChatMessage(null, null, mPhotoUrl,
                            LOADING_IMAGE_URL, mFirebaseUser.getUid(), System.currentTimeMillis());
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD + storageID + "/" + mFirebaseUser.getUid()).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mFirebaseUser.getUid())
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mFirebaseUser != null)
        mFirebaseAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFirebaseUser != null)
        mFirebaseAdapter.startListening();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        UploadTask uploadTask = storageReference.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }}).addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            ChatMessage chatMessage =
                                    new ChatMessage(null, null, mPhotoUrl, downloadUri.toString(), mFirebaseUser.getUid(), System.currentTimeMillis());
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD + storageID + "/"  + mFirebaseUser.getUid()).child(key)
                                    .setValue(chatMessage);
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }
}
