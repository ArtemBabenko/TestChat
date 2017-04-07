package test_chat_project.testchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import test_chat_project.testchat.Adapter.Room_Adapter;
import test_chat_project.testchat.Item.Room_Message;

public class Chat_Room  extends AppCompatActivity{;

    private static final String TAG = "myLogs";
    private static final int PICK_IMAGE_REQUEST = 1;

    private String mId;
    private String mIdImage = "0";
    private String imageURI = "empty";
    private ImageView btn_send_msg, btn_add_image, btn_emotion;

    private EmojiconEditText input_msg;
    private View rootView;
    private EmojIconActions emojIcon;

    private String user_name,room_name, message_time;
    private Uri filepath;
    private StorageReference storageRefrence;
    private DatabaseReference root ;
    private String temp_key;

    private List<Room_Message> list = new ArrayList<>();
    RecyclerView recycler;
    Room_Adapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        mId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
        message_time = sdf.format(date);

        recycler = (RecyclerView) findViewById(R.id.recyclerRoom);
        RecyclerView.LayoutManager layoutMenager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutMenager);
        recycler.setHasFixedSize(true);
        adapter = new Room_Adapter(list,mId,mIdImage,this);
        recycler.setAdapter(adapter);


        storageRefrence = FirebaseStorage.getInstance().getReference();
        btn_add_image = (ImageView) findViewById(R.id.btn_clip);
        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showfileChoosen();
                mIdImage = "3";
            }
        });

        btn_send_msg = (ImageView) findViewById(R.id.btn_send);

        btn_emotion = (ImageView) findViewById(R.id.btn_emotion);
        input_msg = (EmojiconEditText) findViewById(R.id.msg_input);
        rootView = (RelativeLayout) findViewById(R.id.chat_room);
        emojIcon = new EmojIconActions(getApplicationContext(),rootView,btn_emotion,input_msg);
        emojIcon.ShowEmojicon();

        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();

        setTitle(" Room - "+room_name);

        root = FirebaseDatabase.getInstance().getReference().child(room_name);


        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_msg.getText().toString().equals(""))
                {

                }else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);

                    DatabaseReference message_root = root.child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("id", mId);
                    map2.put("id_image", mIdImage);
                    map2.put("img_uri", imageURI);
                    map2.put("name", user_name);
                    map2.put("msg", input_msg.getText().toString());
                    map2.put("time", message_time);
                    message_root.updateChildren(map2);

                    input_msg.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    mIdImage = "0";
                    imageURI = "empty";
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

    private String chat_id, image_id, img_uri="empty", chat_msg, chat_user_name, chat_time;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        ArrayList<Room_Message> messages = new ArrayList<>();
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){

                    chat_id = (String) ((DataSnapshot) i.next()).getValue();
                    image_id = (String) ((DataSnapshot) i.next()).getValue();
                    img_uri = (String) ((DataSnapshot) i.next()).getValue();
                    chat_msg = (String) ((DataSnapshot) i.next()).getValue();
                    chat_user_name = (String) ((DataSnapshot) i.next()).getValue();
                    chat_time = (String) ((DataSnapshot) i.next()).getValue();

                    messages.add(new Room_Message(chat_id, image_id, img_uri, chat_user_name, chat_msg, chat_time));

        }
            list.addAll(messages);
            recycler.scrollToPosition(list.size() - 1);
            adapter.notifyItemInserted(list.size() - 1);
    }

    private void showfileChoosen(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filepath = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
//                Toast.makeText(this,"Image add",Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            if(filepath != null) {

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
//                                Picasso.with(getApplicationContext()).load(downloadUri).fit().centerCrop().into(testImg);
                                Toast.makeText(getApplicationContext(),"File Uploaded",Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progresDialog.dismiss();
                                Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progresDialog.setMessage("% Uploaded...");
                    }
                });
            }else {
                //Error Toast
            }
        }
    }
}

