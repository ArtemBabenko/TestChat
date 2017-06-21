package test_chat_project.testchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.lang.AbstractMethodError;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import test_chat_project.testchat.Adapter.Room_Adapter;
import test_chat_project.testchat.Dialogs.Delete_Room_Dialog;
import test_chat_project.testchat.Dialogs.User_Change_Name_Dialog;
import test_chat_project.testchat.Item.Room_Message;

import static android.R.attr.key;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static test_chat_project.testchat.Main_Chat_Activity.userIconUrl;
import static test_chat_project.testchat.Main_Chat_Activity.userName;

public class Chat_Room extends AppCompatActivity {

    private static final String TAG = "myLogs";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String APP_USER_INFO = "APP_USER_NAME";
    private static final String USER_IMG_URL = "user_url";
    private static final String USER_PROFILE_KEY = "user_profile_key";

    private Toolbar toolbar;

    private Delete_Room_Dialog deleteRoomDialog;

    private String mId;
    private String mIdImage = "0";
    private String imageURI = "empty";
    private ImageView btn_send_msg, btn_add_image, btn_emotion;

    private EmojiconEditText input_msg;
    private View rootView;
    private EmojIconActions emojIcon;

    public static String creator, user_name, room_name, message_time, userIconUrl = "empty";
    public static SharedPreferences sPref;
    private Uri filepath;
    private StorageReference storageRefrence;
    private DatabaseReference root;
    private DatabaseReference root_for_key;
    private String temp_key;

    private FirebaseAuth auth;
    private FirebaseUser user;
    static String userEmail;

    private List<Room_Message> list = new ArrayList<>();
    RecyclerView recycler;
    public static Room_Adapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        loadUserIconUrl();
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
                showfileChoosen();
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
                    map2.put("name", user_name);
                    map2.put("user_icon", userIconUrl);
                    map2.put("msg", input_msg.getText().toString());
                    map2.put("time", message_time);
                    message_root.updateChildren(map2);

                    input_msg.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    mIdImage = "0";
                    imageURI = "empty";
                    btn_add_image.setImageResource(R.mipmap.ic_clippy);
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

    private String chat_id, image_id, img_uri = "empty", chat_msg, chat_user_name, chat_user_icon = "empty", chat_time;

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
            Log.d(TAG,chat_user_icon);
            messages.add(new Room_Message(chat_id, image_id, img_uri, chat_user_name, chat_user_icon, chat_msg, chat_time));

        }
        list.addAll(messages);
        recycler.scrollToPosition(list.size() - 1);
        adapter.notifyItemInserted(list.size() - 1);
    }

    private void showfileChoosen() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            if (filepath != null) {

                final ProgressDialog progresDialog = new ProgressDialog(this);
                progresDialog.setTitle("Uploading");
                progresDialog.show();

                StorageReference riversRef = storageRefrence.child("Images").child(filepath.getLastPathSegment());

                riversRef.putFile(filepath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progresDialog.dismiss();
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                imageURI = String.valueOf(downloadUri);
                                mIdImage = "3";
                                btn_add_image.setImageResource(R.mipmap.ic_clippy_full);
//                                Picasso.with(getApplicationContext()).load(downloadUri).fit().centerCrop().into(testImg);
                                Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progresDialog.dismiss();
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progresDialog.setMessage((int) progress + "% Uploaded...");
                    }
                });
            } else {
                //Error Toast
            }
        }
    }


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

    //Load User Icon Url
    private void loadUserIconUrl() {
        sPref = getSharedPreferences(APP_USER_INFO, Context.MODE_PRIVATE);
        String urlIcon = sPref.getString(USER_IMG_URL, "empty");
        userIconUrl = urlIcon;
//        root_for_key = FirebaseDatabase.getInstance().getReference().child("profile").child(key).child("User Profile Images");
//        root_for_key.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String userProfileIconUrlBase = dataSnapshot.getValue(String.class);
//                userIconUrl = userProfileIconUrlBase;
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void createDeleteRoomDialog() {
        deleteRoomDialog = new Delete_Room_Dialog();
        deleteRoomDialog.show(getFragmentManager(), "Delete Room Dialog");
        deleteRoomDialog.roomName = room_name;
    }

//    private void sendNotification() {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                int SDK_INT = android.os.Build.VERSION.SDK_INT;
//                if (SDK_INT > 8) {
//                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                            .permitAll().build();
//                    StrictMode.setThreadPolicy(policy);
//                    String send_email;
//                    if (Chat_Room.logIn_User_Email.equals("timagrid.mail@gmail.com")) {
//                        send_email = "artembabenko.work@gmail.com";
//                    } else {
//                        send_email = "timagrid.mail@gmail.com";
//                    }
//
//
//                    try {
//                        String jsonResponse;
//                        URL url = new URL("https://onesignal.com/api/v1/notifications");
//                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                        con.setUseCaches(false);
//                        con.setDoOutput(true);
//                        con.setDoInput(true);
//
//                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                        con.setRequestProperty("Authorization", "Basic MTRkZThjMjgtOWU4My00ZTQ5LThjOTEtMTlkOWZmNzllODgw");
//                        con.setRequestMethod("POST");
//
//                        String jsonBody = "{"
//                                + "\"app_id\": \"df037a36-e2e8-4336-9485-1de4455a1f34\","
//                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"
//                                + "\"data\": {\"foo\": \"bar\"},"
//                                + "\"contents\": {\"en\": \"English Message\"}"
//                                + "}";
//
//                        System.out.println("strJsonBody:\n" + jsonBody);
//
//                        byte[] sendBytes = jsonBody.getBytes("UTF-8");
//                        con.setFixedLengthStreamingMode(sendBytes.length);
//
//                        OutputStream outputStream = con.getOutputStream();
//                        outputStream.write(sendBytes);
//
//                        int httpResponse = con.getResponseCode();
//                        System.out.println("httpResponse: "+httpResponse);
//
//                        if(httpResponse >= HttpURLConnection.HTTP_OK
//                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST){
//                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
//                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
//                            scanner.close();
//                        }else {
//                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
//                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
//                            scanner.close();
//                        }
//                        System.out.println("jsonResponse:\n"+jsonResponse);
//
//                    } catch (Throwable t) {
//                        t.printStackTrace();
//                    }
//
//                }
//            }
//        });
//    }

}

