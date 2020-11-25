package com.group05.mylocation.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group05.mylocation.R;
import com.group05.mylocation.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;

    public ConnectionAdapter(Context context, List<User> users){
        this.mContext = context;
        this.mUsers = users;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.connection_connectedaccount__listview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getFullName());
        //code add image URL later
        //
        StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/"+ user.getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext).load(uri).override(80,80).into(holder.profile_image);

            }
        });
        holder.btn_ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map tmp = new HashMap<>();
                tmp.put("isRequested", true);
                db.collection("User").document(mUsers.get(position).getUid()).collection("connection").document(FirebaseAuth.getInstance().getUid()).set(tmp, SetOptions.merge());

                Map tmp2 = new HashMap<>();
                tmp.put("requested", true);
                db.collection("User").document(FirebaseAuth.getInstance().getUid()).collection("connection").document(mUsers.get(position).getUid()).set(tmp, SetOptions.merge());

                mUsers.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mUsers.size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView username;
        private ImageView profile_image;
        private Button btn_ask;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.accountname);
            profile_image = itemView.findViewById(R.id.imageView);
            btn_ask = itemView.findViewById(R.id.btnAsk);

        }
    }
}
