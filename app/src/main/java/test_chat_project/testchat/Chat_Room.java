package test_chat_project.testchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import test_chat_project.testchat.Adapter.Room_Adapter;
import test_chat_project.testchat.Dialogs.Attach_File_Dialog;
import test_chat_project.testchat.Dialogs.Delete_Room_Dialog;
import test_chat_project.testchat.Item.Room_Message;
import test_chat_project.testchat.notification.Utilities;

public class Chat_Room extends AppCompatActivity {

    private static final String TAG = "myLogs";
    private static final String APP_USER_INFO = "APP_USER_NAME";
    private static final String USER_IMG_URL = "user_url";
    private static final String USER_PROFILE_KEY = "user_profile_key";

    private Toolbar toolbar;

    private Delete_Room_Dialog deleteRoomDialog;

    private String mId;
    public static String mIdImage = "0";
    public static String imageURI = "empty";
    public static ImageView btn_send_msg, btn_add_image, btn_emotion;

    public static EmojiconEditText input_msg;
    private View rootView;
    private EmojIconActions emojIcon;

    public static String creator, user_name, room_name, message_time, userIconUrl = "empty", userKeyProfile;
    public static SharedPreferences sPref;
    private Uri filepath;
    private StorageReference storageRefrence;
    private DatabaseReference root;
    private DatabaseReference createNotificationProfile;
    private String temp_key;

    private FirebaseAuth auth;
    private FirebaseUser user;
    static String userEmail;

    private List<Room_Message> list = new ArrayList<>();
    private List<String> userNotificationIdList = new ArrayList<String>();
    RecyclerView recycler;
    public static Room_Adapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        loadUserIconUrl();
        loadUserKeyProfile();
        checkUsersNotificationsId();
//        OneSignal.startInit(this).init();

//        auth = FirebaseAuth.getInstance();
//        user = auth.getCurrentUser();
//        logIn_User_Email = user.getEmail();
//        OneSignal.sendTag("UserId", logIn_User_Email);


        mId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
        message_time = sdf.format(date);

        recycler = (RecyclerView) findViewById(R.id.recyclerRoom);
        RecyclerView.LayoutManager layoutMenager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutMenager);
        recycler.setHasFixedSize(true);
        adapter = new Room_Adapter(list, mId, mIdImage, this);
        recycler.setAdapter(adapter);


        storageRefrence = FirebaseStorage.getInstance().getReference();
        btn_add_image = (ImageView) findViewById(R.id.btn_clip);
        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChoiceDialog();
            }
        });

        btn_send_msg = (ImageView) findViewById(R.id.btn_send);
        btn_emotion = (ImageView) findViewById(R.id.btn_emotion);
        input_msg = (EmojiconEditText) findViewById(R.id.msg_input);
        rootView = (RelativeLayout) findViewById(R.id.chat_room);

        emojIcon = new EmojIconActions(getApplicationContext(), rootView, btn_emotion, input_msg);
        emojIcon.ShowEmojicon();

        creator = getIntent().getExtras().get("creator").toString();
        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();

        initToolbar();

        root = FirebaseDatabase.getInstance().getReference().child("Chat Rooms").child(room_name);

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_msg.getText().toString().equals("") && imageURI == "empty") {

                } else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);

                    DatabaseReference message_root = root.child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("id", mId);
                    map2.put("id_image", mIdImage);
                    map2.put("img_uri", imageURI);
                    map2.put("user_key_profile", userKeyProfile);
                    map2.put("name", user_name);
                    map2.put("user_icon", userIconUrl);
                    map2.put("msg", input_msg.getText().toString());
                    map2.put("time", message_time);
                    message_root.updateChildren(map2);

                    sendNotificationToUser(user_name, input_msg.getText().toString(), room_name);

                    input_msg.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    mIdImage = "0";
                    imageURI = "empty";
                    btn_add_image.setImageResource(R.mipmap.ic_clippy);
                    input_msg.setHint("Message");
                }
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private String chat_id, image_id, img_uri = "empty", chat_msg, chat_user_name, chat_user_icon = "empty", chat_user_key_profile, chat_time;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        ArrayList<Room_Message> messages = new ArrayList<>();
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {

            chat_id = (String) ((DataSnapshot) i.next()).getValue();
            image_id = (String) ((DataSnapshot) i.next()).getValue();
            img_uri = (String) ((DataSnapshot) i.next()).getValue();
            chat_msg = (String) ((DataSnapshot) i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot) i.next()).getValue();
            chat_time = (String) ((DataSnapshot) i.next()).getValue();
            chat_user_icon = (String) ((DataSnapshot) i.next()).getValue();
            chat_user_key_profile = (String) ((DataSnapshot) i.next()).getValue();
            Log.d(TAG, chat_user_icon);
            messages.add(new Room_Message(chat_id, image_id, img_uri, chat_user_name, chat_user_icon, chat_user_key_profile, chat_msg, chat_time));

        }
        list.addAll(messages);
        recycler.scrollToPosition(list.size() - 1);
        adapter.notifyItemInserted(list.size() - 1);
    }

    //Start (User Notifications)
    private void sendNotificationToUser(String userName, String message, String roomName) {
        auth = FirebaseAuth.getInstance();
        boolean sensor = false;
        String userNotificationsId = auth.getCurrentUser().getUid();
        if (userNotificationIdList.size() == 0) {
            Utilities.sendNotification(this, userNotificationsId, "Welcome to Emerald Chat.", "", "");
        } else {
            for (String usersId : userNotificationIdList) {
                if (!usersId.equals(userNotificationsId)) {
                    Utilities.sendNotification(this, usersId, userName + ": " + message, roomName, "new_notification");
                } else {
                    sensor = true;
                }
            }
            if (sensor == false) {
                Utilities.sendNotification(this, userNotificationsId, "Welcome to Emerald Chat.", "", "");
            }
        }
    }

    public void checkUsersNotificationsId() {
        final int[] counter = {1};
        createNotificationProfile = FirebaseDatabase.getInstance().getReference().child("notifications");
        createNotificationProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final SortedSet<String> set = new TreeSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()) {
                    set.add(((DataSnapshot) i.next()).getKey());
                }
                userNotificationIdList.clear();
                userNotificationIdList.addAll(set);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    //End

    /**
     * For Toolbar, button and menu
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Room - " + room_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_room_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.delete_room:
                checkUserEmail();
                if (userEmail.equals(creator)) {
                    createDeleteRoomDialog();
                } else
                    Toast.makeText(this, "Sorry. But you do not have enough rights.", Toast.LENGTH_SHORT).show();
                return true;
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * *******************************
     */
    private void checkUserEmail() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userEmail = user.getEmail();
    }

    //Load User Icon Url in File
    private void loadUserIconUrl() {
        sPref = getSharedPreferences(APP_USER_INFO, Context.MODE_PRIVATE);
        String urlIcon = sPref.getString(USER_IMG_URL, "empty");
        userIconUrl = urlIcon;
    }

    //Load User Key Profile
    private void loadUserKeyProfile() {
        sPref = getSharedPreferences(APP_USER_INFO, Context.MODE_PRIVATE);
        String keyProfile = sPref.getString(USER_PROFILE_KEY, "empty");
        userKeyProfile = keyProfile;
    }

    private void createDeleteRoomDialog() {
        deleteRoomDialog = new Delete_Room_Dialog();
        deleteRoomDialog.show(getFragmentManager(), "Delete Room Dialog");
        deleteRoomDialog.roomName = room_name;
    }

    //Start (Load and send image)

    public void createChoiceDialog() {
        Attach_File_Dialog attachFiledialog = new Attach_File_Dialog();
        attachFiledialog.show(getFragmentManager(), "Choice file");
    }
}

