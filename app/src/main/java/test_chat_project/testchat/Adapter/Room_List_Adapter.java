package test_chat_project.testchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import test_chat_project.testchat.Chat_Room;
import test_chat_project.testchat.Item.Room_List_Element;
import test_chat_project.testchat.MainActivity;
import test_chat_project.testchat.R;

public class Room_List_Adapter extends RecyclerView.Adapter<Room_List_Adapter.ViewHolder> {


    private List<Room_List_Element> mRomm_list_element;

    public Room_List_Adapter(List<Room_List_Element> mRomm_list_element) {

        this.mRomm_list_element = mRomm_list_element;
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
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),Chat_Room.class);
                intent.putExtra("room_name",holder.roomName.getText().toString());
                intent.putExtra("user_name", MainActivity.name);
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mRomm_list_element.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EmojiconTextView roomName;
        CardView cv_room_list;

        public ViewHolder(View itemView) {
            super(itemView);
            roomName = (EmojiconTextView) itemView.findViewById(R.id.nameRoom);
            cv_room_list = (CardView) itemView.findViewById(R.id.cv_room_list);
        }
    }
}
