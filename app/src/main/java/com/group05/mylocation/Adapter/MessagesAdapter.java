package com.group05.mylocation.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group05.mylocation.Modal.Messages;
import com.group05.mylocation.R;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    public static final int MSG_R = 1;
    public static final int MSG_L = 0;
    private Context mContext;
    private List<Messages> mMessages;
    private Uri left_profile_image;
    FirebaseUser fuser;

    public MessagesAdapter(Context context, List<Messages> m, Uri left_profile){
        this.mContext = context;
        this.mMessages = m;
        left_profile_image=left_profile;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_R){
            View v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(v);
        }
        View v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Messages message = mMessages.get(position);
        holder.show_message.setText(message.getMessage());
//        holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        Glide.with(mContext).load(left_profile_image).override(40,40).into(holder.profile_image);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView show_message;
        private ImageView profile_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mMessages.get(position).getSender().equals(fuser.getUid()))
            return MSG_R;
        return MSG_L;
    }
}
