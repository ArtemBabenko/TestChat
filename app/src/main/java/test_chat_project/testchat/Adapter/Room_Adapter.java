package test_chat_project.testchat.Adapter;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import test_chat_project.testchat.Chat_Room;
import test_chat_project.testchat.Item.Room_Message;
import test_chat_project.testchat.R;

import java.util.List;

import static android.R.id.list;
import static android.R.id.message;
import static junit.runner.Version.id;

public class Room_Adapter extends RecyclerView.Adapter<Room_Adapter.ViewHolder> {

    private static final int CHAT_RIGHT = 1;
    private static final int CHAT_LEFT = 2;
    public static final int CHAT_RIGHT_IMAGE = 3;
    public static final int CHAT_LEFT_IMAGE = 4;

    

    private String mId;
    private String mIdItem;
    private Context mContext;
    private String ItemPosicion;

    private List<Room_Message> mRomm_list;

    public Room_Adapter(List<Room_Message> mRomm_list, String mId, String mIdItem, Context mContext) {

        this.mRomm_list = mRomm_list;
        this.mId = mId;
        this.mIdItem = mIdItem;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == CHAT_RIGHT || viewType == CHAT_RIGHT_IMAGE) {
            if(viewType == CHAT_RIGHT_IMAGE) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_room_item_right_image, parent, false);
            }else{
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_room_item_right, parent, false);
            }
        }else if(viewType==CHAT_LEFT_IMAGE){
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_room_item_left_image, parent, false);
        }else{
                view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_room_item_left, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Room_Message message = mRomm_list.get(position);
        String ImageId  = message.getmIdImage();
        if(ImageId.equals("3")) {
            Picasso.with(mContext).load(message.getmMessageUri()).fit().centerCrop().into(holder.userMessageImage);
            holder.userName.setText(message.getmNameUser());
            holder.userMessage.setText(message.getmMessageUser());
            holder.userMessageTime.setText(message.getmMessageTime());
        }else {
            holder.userName.setText(message.getmNameUser());
            holder.userMessage.setText(message.getmMessageUser());
            holder.userMessageTime.setText(message.getmMessageTime());
        }
    }

    @Override
        public int getItemViewType(int position) {
        if (mRomm_list.get(position).getmId().equals(mId) && mRomm_list.get(position).getmIdImage().equals("3")) {
            return CHAT_RIGHT_IMAGE;
        }if(mRomm_list.get(position).getmId().equals(mId)){
            return CHAT_RIGHT;
        }if(mRomm_list.get(position).getmIdImage().equals("3")){
            return CHAT_LEFT_IMAGE;
        }else
            return CHAT_LEFT;
    }

    @Override
    public int getItemCount() {
        return mRomm_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName,userMessageTime;
        EmojiconTextView userMessage;
        ImageView userMessageImage;
        CardView cv;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.nameUser);
            userMessage = (EmojiconTextView) itemView.findViewById(R.id.messageUser);
            userMessageTime = (TextView) itemView.findViewById(R.id.messageTime);
            userMessageImage = (ImageView) itemView.findViewById(R.id.imageUser);
            cv = (CardView) itemView.findViewById(R.id.cv);
        }
    }
}
