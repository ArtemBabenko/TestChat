package test_chat_project.testchat.Dialogs;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import test_chat_project.testchat.Main_Chat_Activity;
import test_chat_project.testchat.R;

import static test_chat_project.testchat.Main_Chat_Activity.sPref;


public class User_Name_Dialog extends DialogFragment implements OnClickListener {

    private static final String TAG = "myLogs";
    private final String USER_NAME = "user_name";
    public static final int NAME_FREE = 0;
    public static final int NAME_EXIST = 1;
    public static final int ACC_EXIST = 2;

    private String userNameInBase = " ";
    private EditText mEditTextName;
    private TextView mHeaderText;

    private DatabaseReference root;
    private DatabaseReference root_user;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userEmail;
    ArrayList<String> userNames = new ArrayList<>();
    private int Sensor = NAME_FREE;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        setCancelable(false);
        root = FirebaseDatabase.getInstance().getReference().child("profile");
        auth = FirebaseAuth.getInstance();
        checkUserEmail();
        downloadNamesUser();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_name_dialog, null);
        v.findViewById(R.id.add_name_yes).setOnClickListener(this);
        mEditTextName = (EditText) v.findViewById(R.id.enter_name_text);
        mHeaderText = (TextView) v.findViewById(R.id.enter_user_name);
        return v;
    }

    public void onClick(View v) {
        checkForUniqueness();
        if (v.getId() == R.id.add_name_yes) {
            if (Sensor == ACC_EXIST) {
                saveUserNameInFile(userNameInBase);
                dismiss();
            } else if (mEditTextName.getText().toString().equals("")) {
                mEditTextName.setText("");
                mEditTextName.setHint("Incorrect name");
                mEditTextName.setHintTextColor(getResources().getColor(R.color.colorHintError));
            } else if (Sensor == NAME_FREE) {
                saveUserNameInFile(mEditTextName.getText().toString());
                saveProfileDataInBase();
                dismiss();
            } else if (Sensor == NAME_EXIST) {
                mEditTextName.setText("");
                mEditTextName.setHint("The name already exists.");
                mEditTextName.setHintTextColor(getResources().getColor(R.color.colorHintError));
                Sensor = NAME_FREE;
            }
        }
    }

    private void downloadNamesUser() {
        final int[] counter = {1};
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final SortedSet<String> set = new TreeSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {
                    set.add(((DataSnapshot) i.next()).getKey());
                }
                if (counter[0] == 1) {
                    userNames.clear();
                }

                userNames.addAll(set);
                Log.d(TAG, "Size UserName: " + userNames.size());
                checkExistProfile();
                counter[0]++;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkExistProfile() {
        for (final String name : userNames) {
            root_user = FirebaseDatabase.getInstance().getReference().child("profile").child(name).child("User Email");
            root_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String userEmailInBase = dataSnapshot.getValue(String.class);
                    if (userEmailInBase.equals(userEmail)) {
                        profileExist(name);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void profileExist(String name) {
        userNameInBase = name;
        mEditTextName.setText(userNameInBase);
        mEditTextName.setCursorVisible(false);
        mEditTextName.setLongClickable(false);
        mEditTextName.setFocusable(false);
        mHeaderText.setText("You profile name:");
        Sensor = ACC_EXIST;
    }

    public void checkForUniqueness() {
        for (String names : userNames) {
            if (names.equals(mEditTextName.getText().toString()) && Sensor != ACC_EXIST) {
                Sensor = NAME_EXIST;
            }
        }
    }

    private void checkUserEmail() {
        user = auth.getCurrentUser();
        userEmail = user.getEmail();
    }

    private void saveProfileDataInBase() {
        Map<String, Object> map = new HashMap<String, Object>();
        root.updateChildren(map);

        DatabaseReference message_root = root.child(mEditTextName.getText().toString());
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("User Email", userEmail);
        map2.put("User Profile Images", "null");
        message_root.updateChildren(map2);
    }

    private void saveUserNameInFile(String name) {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(USER_NAME, name);
        ed.commit();
        Main_Chat_Activity.userName = name;
        Toast.makeText(getActivity(), "Name saved", Toast.LENGTH_SHORT).show();
    }


    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mEditTextName.setText("");
        Sensor = NAME_FREE;
        Log.d(TAG, "Dialog 1: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(TAG, "Dialog 1: onCancel");
    }

}
