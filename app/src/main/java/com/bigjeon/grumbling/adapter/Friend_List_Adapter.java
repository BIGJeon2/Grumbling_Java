package com.bigjeon.grumbling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bigjeon.grumbling.data.Friends_Data;
import com.bigjeon.grumbling.data.Post_Data;
import com.example.grumbling.databinding.FriendsItemBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Friend_List_Adapter extends RecyclerView.Adapter<Friend_List_Adapter.Friend_Holder> {
    private Context mContext;
    private FriendsItemBinding binding;
    private String My_Uid;
    ArrayList<Friends_Data> Friend_List;

    public Friend_List_Adapter(Context mContext, ArrayList<Friends_Data> friend_List) {
        this.mContext = mContext;
        Friend_List = friend_List;
    }

    @NonNull
    @NotNull
    @Override
    public Friend_Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        binding = FriendsItemBinding.inflate(LayoutInflater.from(context), parent, false);
        Friend_Holder holder = new Friend_Holder(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Friend_Holder holder, int position) {

        My_Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore Store = FirebaseFirestore.getInstance();
        Store.collection("Users").whereEqualTo("UID", Friend_List.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        binding.FriendRequestUserName.setText(document.get("Name").toString());
                        Picasso.get().load(document.getString("Img")).into(binding.FriendRequestUserImg);
                    }
                }
            }
        });
        //수락시 친구 상태 ACCEPT으로 변경
        holder.binding.FriendRequestAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat simpledate = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date();
                String Set_Friend_Date = simpledate.format(date);

                String Target_Uid = Friend_List.get(position).getUid();

                Set_Friends(Target_Uid);

                Friends_Data My_request = new Friends_Data("ACCEPT", Target_Uid, Set_Friend_Date);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Receive_Friends_Request").child(Friend_List.get(position).getUid());
                reference.setValue(My_request);
                Friends_Data Other_request = new Friends_Data("ACCEPT", My_Uid, Set_Friend_Date);
                reference = FirebaseDatabase.getInstance().getReference("Users").child(Target_Uid).child("Send_Friends_Request").child(My_Uid);
                reference.setValue(Other_request);

            }
        });
    }

    @Override
    public int getItemCount() {
        return Friend_List.size();
    }

    public class Friend_Holder extends RecyclerView.ViewHolder {
        public FriendsItemBinding binding;
        public Friend_Holder(FriendsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    private void Set_Friends(String UID){

        SimpleDateFormat simpledate = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String Set_Friend_Date = simpledate.format(date);

        DatabaseReference My_Reference = FirebaseDatabase.getInstance().getReference("Users").child(My_Uid).child("Friends").child(UID);
        DatabaseReference Other_Reference = FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Friends").child(My_Uid);
        My_Reference.setValue(Set_Friend_Date);
        Other_Reference.setValue(Set_Friend_Date);
    }
}
