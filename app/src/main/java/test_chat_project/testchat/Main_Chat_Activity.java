package test_chat_project.testchat;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import de.hdodenhof.circleimageview.CircleImageView;
import test_chat_project.testchat.Adapter.Room_List_Adapter;
import test_chat_project.testchat.Dialogs.Add_Room_Dialog;
import test_chat_project.testchat.Dialogs.User_Change_Name_Dialog;
import test_chat_project.testchat.Dialogs.User_Name_Dialog;
import test_chat_project.testchat.Item.Room_List_Element;
import test_chat_project.testchat.notification.FirebaseNotificationService;

public class Main_Chat_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "myLogs";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String APP_USER_INFO = "APP_USER_NAME";
    private static final String USER_NAME = "user_name";
    private final String USER_PROFILE_KEY = "user_profile_key";
    private static final String USER_IMG_URL = "user_url";

    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    private Toolbar toolbar;


    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //For save userName in file
    public static SharedPreferences sPref;
    private User_Name_Dialog userNameDialog;
    private User_Change_Name_Dialog userChangeNameDialog;

    //For image profile
    public  static CircleImageView userProfileImage;
    private Uri filepath;
    private StorageReference storageRefrence;
    private String profileImageURI = "empty";

    //For add room
    Add_Room_Dialog addRoomDialog;
    FragmentManager manager = getFragmentManager();

    private RecyclerView recycler;
    public static Room_List_Adapter roomListAdapter;
    private ArrayList<Room_List_Element> list_of_rooms = new ArrayList<>();

    public static String userName;
    public static String userProfileKey;
    public static String userEmail;
    public static String userIconUrl = "empty";
    private String kay_for_image = "";
    private String creator = "";
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chat Rooms");
    private DatabaseReference room_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(R.layout.main_chat_activity);
        startService(new Intent(this, FirebaseNotificationService.class));
        initToolbar();
        initToggle();
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

                    //Check key
                    room_root = FirebaseDatabase.getInstance().getReference().child("Chat Rooms").child(string).child("key");
                    room_root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            kay_for_image = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    //Check creator, add data in ListElement
                    room_root = FirebaseDatabase.getInstance().getReference().child("Chat Rooms").child(string).child("creator");
                    room_root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (timer[0] == 1) {
                                list_of_rooms.clear();
                            }
                            creator = dataSnapshot.getValue(String.class);
                            list_of_rooms.add(new Room_List_Element(string, kay_for_image, creator));
                            Log.d(TAG, string + " : " + kay_for_image + " : " + creator);
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

    //Start (For Toolbar)
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }

    private void initToggle() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_change_name) {
            changeUserName();
        }
        if (id == R.id.nav_logout) {
            auth.signOut();
        }

        return true;
    }
    // End

    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    //Check UserName in start
    private void checkUserName() {
        loadUserName();
        if (userName.equals("")) {
            userNameDialog = new User_Name_Dialog();
            userNameDialog.show(getFragmentManager(), "User Name");
        }
    }

    //Change UserName in NavigationDrawer
    private void changeUserName() {
        userChangeNameDialog = new User_Change_Name_Dialog();
        userChangeNameDialog.show(getFragmentManager(), "User Change Name");
        loadUserName();
    }

    //Change UserProfileIcon in NavigationDrawer
    private void changeUserIcon(String url) {
        sPref = getSharedPreferences(APP_USER_INFO, Context.MODE_PRIVATE);
        userProfileKey = sPref.getString(USER_PROFILE_KEY, "");

        root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference message_root = root.child("profile").child(userProfileKey);
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("User Profile Images", url);
        message_root.updateChildren(map2);

        saveUserIconUrlInFile(url);
    }

    //UserName and Email check
    private void loadUserName() {
        sPref = getSharedPreferences(APP_USER_INFO, Context.MODE_PRIVATE);
        userName = sPref.getString(USER_NAME, "");
        user = auth.getCurrentUser();
        userEmail = user.getEmail();

        loadHeaderInfo(userName, userEmail);
    }

    //Load User Icon Url
    private void loadUserIconUrl() {
        sPref = getSharedPreferences(APP_USER_INFO, Context.MODE_PRIVATE);
        userIconUrl = sPref.getString(USER_IMG_URL, "empty");
        if (!(userIconUrl.equals("empty"))) {
            Picasso.with(getApplicationContext()).load(userIconUrl).fit().centerCrop().into(userProfileImage);
        }
    }

    //Save User Icon Url in file
    private void saveUserIconUrlInFile(String url) {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(USER_IMG_URL, url);
        ed.commit();
    }

    //Start (Choose image for profile and load in base)
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

                storageRefrence = FirebaseStorage.getInstance().getReference();
                StorageReference riversRef = storageRefrence.child("ProfileImages").child(filepath.getLastPathSegment());

                riversRef.putFile(filepath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progresDialog.dismiss();
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                profileImageURI = String.valueOf(downloadUri);
                                changeUserIcon(profileImageURI);
                                Picasso.with(getApplicationContext()).load(profileImageURI).fit().centerCrop().into(userProfileImage);
                                Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_LONG).show();
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
    //End

    //Add user info in NavigationHeader
    private void loadHeaderInfo(String userName, String userEmail) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView userNameHeader = (TextView) header.findViewById(R.id.userNameHeader);
        userNameHeader.setText(userName);
        TextView userEmailHeader = (TextView) header.findViewById(R.id.userEmailHeader);
        userEmailHeader.setText(userEmail);
        userProfileImage = (CircleImageView) header.findViewById(R.id.profile_image);
        loadUserIconUrl();
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showfileChoosen();
            }
        });
    }

}