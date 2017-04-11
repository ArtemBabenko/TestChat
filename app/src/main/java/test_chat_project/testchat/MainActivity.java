package test_chat_project.testchat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import test_chat_project.testchat.Adapter.Room_List_Adapter;
import test_chat_project.testchat.Item.Room_List_Element;

public class MainActivity extends AppCompatActivity {

    private EmojIconActions emojIcon;
    private EmojiconEditText room_name;

    private RecyclerView recycler;
    private Room_List_Adapter roomListAdapter;
    private ArrayList<Room_List_Element> list_of_rooms = new ArrayList<>();
    public static String name;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        add_emotion = (ImageView) findViewById(R.id.btn_room_emotion);
//        room_name = (EmojiconEditText) findViewById(R.id.room_name_edit_text);
//        rootView = (RelativeLayout) findViewById(R.id.activity_main);
//        emojIcon = new EmojIconActions(getApplicationContext(),rootView,add_emotion,room_name);
//        emojIcon.ShowEmojicon();

        FloatingActionButton add_room = (FloatingActionButton) findViewById(R.id.add_room);
        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_room_name();
            }
        });

        recycler = (RecyclerView) findViewById(R.id.room_list_element);
        RecyclerView.LayoutManager layoutMenager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutMenager);
        recycler.setHasFixedSize(true);
        roomListAdapter = new Room_List_Adapter(list_of_rooms);
        recycler.setAdapter(roomListAdapter);

        request_user_name();

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                list_of_rooms.clear();
                for(String name : set){
                    list_of_rooms.add(new Room_List_Element(name));
                }
                roomListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void add_room_name() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter room name:");

        final EmojiconEditText input_field = new EmojiconEditText(this);
        final ImageView add_emoticon = new ImageView(this);
        builder.setView(add_emoticon);

        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Map<String,Object> map = new HashMap<String, Object>();
                map.put(input_field.getText().toString(),"");
                root.updateChildren(map);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

    private void request_user_name() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name:");

        final EditText input_field = new EditText(this);

        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                name = input_field.getText().toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user_name();
            }
        });

        builder.show();
    }


}