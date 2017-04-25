package test_chat_project.testchat;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;

import test_chat_project.testchat.Adapter.Room_List_Adapter;
import test_chat_project.testchat.Dialogs.Add_Room_Dialog;
import test_chat_project.testchat.Item.Room_List_Element;

import static android.R.attr.id;

public class Main_Chat_Activity extends AppCompatActivity{

    private static final String TAG = "myLogs";

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    Add_Room_Dialog addRoomDialog;
    FragmentManager manager = getFragmentManager();

    private RecyclerView recycler;
    public static Room_List_Adapter roomListAdapter;
    private ArrayList<Room_List_Element> list_of_rooms = new ArrayList<>();

    public static String name;
    private String kay_for_image="";
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference room_root = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(Main_Chat_Activity.this, Main_Activity.class));
                }
            }
        };

        addRoomDialog = new Add_Room_Dialog();
        FloatingActionButton add_room = (FloatingActionButton) findViewById(R.id.add_room);
        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRoomDialog.show(getFragmentManager(),"Room Name");
            }
        });

        recycler = (RecyclerView) findViewById(R.id.room_list_element);
        RecyclerView.LayoutManager layoutMenager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutMenager);
        recycler.setHasFixedSize(true);
        roomListAdapter = new Room_List_Adapter(list_of_rooms, this, manager);
        recycler.setAdapter(roomListAdapter);

        request_user_name();
         Log.d(TAG,"Activity Run");
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final SortedSet<String> set = new TreeSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                Log.d(TAG,"SizeSet: "+set.size());
                final int[] timer = {1};
                for(final String string : set){
                    room_root = FirebaseDatabase.getInstance().getReference().child(string).child("key");
                    room_root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(timer[0] == 1){
                                list_of_rooms.clear();
                            }
                                kay_for_image = dataSnapshot.getValue(String.class);
                                list_of_rooms.add(new Room_List_Element(string, kay_for_image));
                                System.out.println(list_of_rooms.size());
                                Log.d(TAG, string + " : " + kay_for_image);
                                Log.d(TAG, "SizeList: "+ list_of_rooms.size());
                                roomListAdapter.notifyDataSetChanged();
                            timer[0]++;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }
                list_of_rooms.clear();
                roomListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_log_out:
                auth.getInstance().signOut();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void onStart(){
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }
}