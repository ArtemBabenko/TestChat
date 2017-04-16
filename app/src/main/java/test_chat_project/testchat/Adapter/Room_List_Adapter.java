package test_chat_project.testchat.Adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import test_chat_project.testchat.Chat_Room;
import test_chat_project.testchat.Dialogs.Enter_Password_Dialog;
import test_chat_project.testchat.Item.Room_List_Element;
import test_chat_project.testchat.MainActivity;
import test_chat_project.testchat.R;

public class Room_List_Adapter extends RecyclerView.Adapter<Room_List_Adapter.ViewHolder>{

    private DatabaseReference root;
    private String key = "";
    private Enter_Password_Dialog passwordDialog;
    FragmentManager manager;

    private List<Room_List_Element> mRomm_list_element;

    private Context context;

    public Room_List_Adapter(List<Room_List_Element> mRomm_list_element, Context context,FragmentManager manager) {
        this.mRomm_list_element = mRomm_list_element;
        this.context = context;
        this.manager = manager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.room_list_element, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Room_List_Element message = mRomm_list_element.get(position);
        holder.roomName.setText(message.getmNameRoom());
        holder.cv_room_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                root = FirebaseDatabase.getInstance().getReference().child(holder.roomName.getText().toString()).child("key");
                root.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        key = dataSnapshot.getValue(String.class);
                        passwordCheck(holder, v, key);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }

        });


    }


    @Override
    public int getItemCount() {
        return mRomm_list_element.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        EmojiconTextView roomName;
        ImageView imageLock;
        CardView cv_room_list;

        public ViewHolder(View itemView) {
            super(itemView);
            roomName = (EmojiconTextView) itemView.findViewById(R.id.nameRoom);
            imageLock = (ImageView) itemView.findViewById(R.id.lock_image);
            cv_room_list = (CardView) itemView.findViewById(R.id.cv_room_list);
        }
    }

    private void passwordCheck(ViewHolder holder,View v, String key){
        if(key.equals("null")) {

            Intent intent = new Intent(v.getContext(),Chat_Room.class);
            intent.putExtra("room_name",holder.roomName.getText().toString());
            intent.putExtra("user_name", MainActivity.name);
            v.getContext().startActivity(intent);
            Toast.makeText(context,key,Toast.LENGTH_LONG).show();
        }else {

            passwordDialog = new Enter_Password_Dialog();
            passwordDialog.show(manager, "Enter Password");
            Toast.makeText(context, "Enter Password "+" "+key, Toast.LENGTH_SHORT).show();
        }
    }

}
