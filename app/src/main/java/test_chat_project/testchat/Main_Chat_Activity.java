package test_chat_project.testchat;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
import test_chat_project.testchat.Dialogs.User_Name_Dialog;
import test_chat_project.testchat.Item.Room_List_Element;

public class Main_Chat_Activity extends AppCompatActivity {

    private static final String TAG = "myLogs";
    private final String USER_NAME = "user_name";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;


    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    public static SharedPreferences sPref;
    private User_Name_Dialog userNameDialog;

    Add_Room_Dialog addRoomDialog;
    FragmentManager manager = getFragmentManager();

    private RecyclerView recycler;
    public static Room_List_Adapter roomListAdapter;
    private ArrayList<Room_List_Element> list_of_rooms = new ArrayList<>();

    public static String userName;
    private String kay_for_image = "";
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference room_root = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(R.layout.main_chat_activity);
        initToolbar();
//        initToggle();
        initNavigationView();

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(Main_Chat_Activity.this, Main_Activity.class));
                }
            }
        };

        checkUserName();

        addRoomDialog = new Add_Room_Dialog();
        FloatingActionButton add_room = (FloatingActionButton) findViewById(R.id.add_room);
        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRoomDialog.show(getFragmentManager(), "Room Name");
            }
        });

        recycler = (RecyclerView) findViewById(R.id.room_list_element);
        RecyclerView.LayoutManager layoutMenager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutMenager);
        recycler.setHasFixedSize(true);
        roomListAdapter = new Room_List_Adapter(list_of_rooms, this, manager);
        recycler.setAdapter(roomListAdapter);

//        request_user_name();
        Log.d(TAG, "Activity Run");
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final SortedSet<String> set = new TreeSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {
                    set.add(((DataSnapshot) i.next()).getKey());
                }
                Log.d(TAG, "SizeSet: " + set.size());
                final int[] timer = {1};
                for (final String string : set) {
                    room_root = FirebaseDatabase.getInstance().getReference().child(string).child("key");
                    room_root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (timer[0] == 1) {
                                list_of_rooms.clear();
                            }
                            kay_for_image = dataSnapshot.getValue(String.class);
                            list_of_rooms.add(new Room_List_Element(string, kay_for_image));
                            System.out.println(list_of_rooms.size());
                            Log.d(TAG, string + " : " + kay_for_image);
                            Log.d(TAG, "SizeList: " + list_of_rooms.size());
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

    /**
     * For Toolbar
     **************************************************************/
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
//        toolbar.inflateMenu(R.menu.menu_navigation)
    }

//    private void initToggle() {
//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
//    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.accessibilityActionShowOnScreen) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    /***********************************************************/

//    private void request_user_name() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Enter name:");
//
//        final EditText input_field = new EditText(this);
//
//        builder.setView(input_field);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                name = input_field.getText().toString();
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
//                request_user_name();
//            }
//        });
//
//        builder.show();
//    }

    //User Name Check
    private void loadUserName() {
        sPref = getPreferences(MODE_PRIVATE);
        userName = sPref.getString(USER_NAME, "");
    }

    private void checkUserName() {
        loadUserName();
        if (userName.equals("")) {
            userNameDialog = new User_Name_Dialog();
            userNameDialog.show(getFragmentManager(), "User Name");
        }
    }

    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }
}