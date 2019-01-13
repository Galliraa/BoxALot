package com.example.kenne.box_a_lot.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.models.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageListAdapter extends FirebaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<ChatMessage> mMessageList;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MessageListAdapter(FirebaseRecyclerOptions options, Context context) {
        super(options);
        mContext = context;
    }

/*    @Override
    public int getItemCount() {
        return mMessageList.size();
    }*/

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItem(position);

        if (message.getUid().equals(FirebaseAuth.getInstance().getUid())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, ChatMessage model) {
        ChatMessage message = getItem(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            String date = DateUtils.formatDateTime(mContext, message.getTime(), 0);
            timeText.setText(date);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(DateUtils.formatDateTime(mContext, message.getTime(), 0));

            nameText.setText(message.getName());

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);

            if (message.getImageUrl() != null) {
                String imageUrl = message.getImageUrl();
                if (imageUrl.startsWith("gs://")) {
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReferenceFromUrl(imageUrl);
                    storageReference.getDownloadUrl().addOnCompleteListener(
                            new OnCompleteListener<Uri>() {
                                private String TAG = "ContactStorage";

                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        String downloadUrl = task.getResult().toString();
                                        Glide.with(mContext)
                                                .load(downloadUrl)
                                                .into(profileImage);
                                    } else {
                                        Log.w(TAG, "Getting download url was not successful.",
                                                task.getException());
                                    }
                                }
                            });
                } else {
                    Glide.with(mContext)
                            .load(message.getImageUrl())
                            .into(profileImage);
                }
            }
        }
    }
}
