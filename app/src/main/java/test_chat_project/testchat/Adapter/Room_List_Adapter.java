package test_chat_project.testchat.Adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;
import java.lang.String;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import test_chat_project.testchat.Chat_Room;
import test_chat_project.testchat.Dialogs.Enter_Password_Dialog;
import test_chat_project.testchat.Item.Room_List_Element;
import test_chat_project.testchat.Main_Chat_Activity;
import test_chat_project.testchat.R;

import static test_chat_project.testchat.Dialogs.Enter_Password_Dialog.roomName;

public class Room_List_Adapter extends RecyclerView.Adapter<Room_List_Adapter.ViewHolder>{

    private static final String TAG = "myLogs";

    private String key = "null";
    private String creator = "null";
    private String kay_for_image;
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
        Log.d(TAG,"SizeRoomList: "+mRomm_list_element.size());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Room_List_Element message = mRomm_list_element.get(position);
        holder.roomName.setText(message.getmNameRoom());

        if(!("null".equals(message.getmPasswordRoom())) && message.getmPasswordRoom() != null ){
            holder.imageLock.setImageResource(R.mipmap.ic_key);
        } else
            holder.imageLock.setImageDrawable(null);

        //for check password if click
        holder.cv_room_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                key = message.getmPasswordRoom();
                creator = message.getmCreator();
                passwordCheck(holder, v, key, creator);
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

    private void passwordCheck(ViewHolder holder,View v, String key, String creator){
        if(key.equals("null")){
            Intent intent = new Intent(v.getContext(),Chat_Room.class);
            intent.putExtra("room_name",holder.roomName.getText().toString());
            intent.putExtra("creator", creator);
            intent.putExtra("user_name", Main_Chat_Activity.userName);
            v.getContext().startActivity(intent);
        }else {
            passwordDialog = new Enter_Password_Dialog();
            passwordDialog.show(manager, "Enter Password");
            passwordDialog.password = key;
            passwordDialog.creator = creator;
            roomName = holder.roomName.getText().toString();
            Toast.makeText(context, "Enter Password "+" "+key, Toast.LENGTH_SHORT).show();
        }
    }

}
