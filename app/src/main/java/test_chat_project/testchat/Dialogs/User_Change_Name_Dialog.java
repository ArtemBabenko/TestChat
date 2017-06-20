package test_chat_project.testchat.Dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import test_chat_project.testchat.Main_Chat_Activity;
import test_chat_project.testchat.R;

import static test_chat_project.testchat.Dialogs.User_Name_Dialog.ACC_EXIST;
import static test_chat_project.testchat.Dialogs.User_Name_Dialog.NAME_EXIST;
import static test_chat_project.testchat.Dialogs.User_Name_Dialog.NAME_FREE;
import static test_chat_project.testchat.Main_Chat_Activity.sPref;
import static test_chat_project.testchat.Main_Chat_Activity.userName;


public class User_Change_Name_Dialog extends DialogFragment implements View.OnClickListener {

    final String LOG_TAG = "myLogs";
    private final String USER_NAME = "user_name";
    private final String USER_PROFILE_KEY = "user_profile_key";
    public static final int NAME_FREE = 0;
    public static final int NAME_EXIST = 1;
    private EditText mEditTextName;

    private DatabaseReference root;
    private DatabaseReference root_user_name;
    ArrayList<String> userNames = new ArrayList<>();
    private String userProfileKey;
    private int Sensor = NAME_FREE;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        setCancelable(false);
        root = FirebaseDatabase.getInstance().getReference().child("profile");
        downloadNamesUser();
        checkUserProfileKey();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_name_change_dialog, null);
        v.findViewById(R.id.add_change_name_yes).setOnClickListener(this);
        v.findViewById(R.id.add_change_name_cancel).setOnClickListener(this);
        mEditTextName = (EditText) v.findViewById(R.id.enter_change_name_text);

        return v;
    }

    public void onClick(View v) {
        checkForUniqueness();
        if (v.getId() == R.id.add_change_name_yes) {
            if (mEditTextName.getText().toString().equals("")) {
                mEditTextName.setText("");
                mEditTextName.setHint("Incorrect name");
                mEditTextName.setHintTextColor(getResources().getColor(R.color.colorHintError));
            } else if(Sensor == NAME_EXIST) {
                mEditTextName.setText("");
                mEditTextName.setHint("The name already exists.");
                mEditTextName.setHintTextColor(getResources().getColor(R.color.colorHintError));
                Sensor = NAME_FREE;
            } else if(Sensor == NAME_FREE){
                saveUserNameinFile();
                saveUserNameinBase();
                dismiss();
            }
        } else if (v.getId() == R.id.add_change_name_cancel) {
            dismiss();
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
                for (final String string : set) {

                    //Check UserName from Base
                    root_user_name = FirebaseDatabase.getInstance().getReference().child("profile").child(string).child("User Name");
                    root_user_name.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (counter[0] == 1) {
                                userNames.clear();
                            }
                            String userName = "";
                            userName = dataSnapshot.getValue(String.class);
                            userNames.add(userName);
                            counter[0]++;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkForUniqueness() {
        for (String names : userNames) {
            if (names.equals(mEditTextName.getText().toString()) && Sensor != ACC_EXIST) {
                Sensor = NAME_EXIST;
            }
        }
    }

    public void saveUserNameinFile() {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(USER_NAME, mEditTextName.getText().toString());
        ed.commit();
        userName = mEditTextName.getText().toString();
        Toast.makeText(getActivity(), "Name changed", Toast.LENGTH_SHORT).show();
    }
    public void saveUserNameinBase(){
        root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference message_root = root.child("profile").child(userProfileKey);
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("User Name", userName);
        message_root.updateChildren(map2);
    }

    public void checkUserProfileKey(){
        userProfileKey = sPref.getString(USER_PROFILE_KEY, "");
    }
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mEditTextName.setText("");
        Log.d(LOG_TAG, "Dialog 1: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "Dialog 1: onCancel");
    }

}