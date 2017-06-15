package test_chat_project.testchat.Dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

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

import test_chat_project.testchat.R;


public class Add_Room_Dialog extends DialogFragment implements OnClickListener {

    private static final String TAG = "myLogs";

    public static EditText mEditTextName;
    private EditText mEditTextKey;

    private DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference().getRoot();

    private FirebaseAuth auth;
    private FirebaseUser user;
    public static String userEmail;
    ArrayList<String> roomNames = new ArrayList<>();
    private int mSensor = 0;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userEmail = user.getEmail();
        downloadNamesRooms();
        setStyle(STYLE_NO_TITLE, 0);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_room_dialog, null);
        v.findViewById(R.id.add_room_yes).setOnClickListener(this);
        v.findViewById(R.id.add_room_cancel).setOnClickListener(this);
        mEditTextName = (EditText) v.findViewById(R.id.input_room_name);
        mEditTextKey = (EditText) v.findViewById(R.id.input_room_key);


        return v;
    }

    public void onClick(View v) {
        Log.d(TAG, "Size RoomName: " + roomNames.size());
        checkForUniqueness();
        if (v.getId() == R.id.add_room_yes && !(mEditTextName.getText().toString().equals("")) && mSensor == 0) {
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put(mEditTextName.getText().toString(), "");
//            mRoot.updateChildren(map);
            DatabaseReference message_root = mRoot.child(mEditTextName.getText().toString());
            Map<String, Object> map2 = new HashMap<String, Object>();
            if (mEditTextKey.getText().toString().equals("")) {
                map2.put("key", "null");
                map2.put("creator", userEmail);
            } else {
                map2.put("key", mEditTextKey.getText().toString());
                map2.put("creator", userEmail);
            }
            message_root.updateChildren(map2);
            dismiss();
        } else if (v.getId() == R.id.add_room_yes && !(mEditTextName.getText().toString().equals("")) && mSensor == 1) {
            mEditTextName.setText("");
            mEditTextName.setHint("The name already exists.");
            mEditTextName.setHintTextColor(getResources().getColor(R.color.colorHintError));
            mSensor = 0;
        } else if (v.getId() == R.id.add_room_cancel) {
            dismiss();
        }

    }

    private void downloadNamesRooms() {
        final int[] counter = {1};
        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final SortedSet<String> set = new TreeSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {
                    set.add(((DataSnapshot) i.next()).getKey());
                }
                if (counter[0] == 1) {
                    roomNames.clear();
                }

                roomNames.addAll(set);
                Log.d(TAG, "Size RoomName: " + roomNames.size());
                counter[0]++;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkForUniqueness() {
        for (String names : roomNames) {
            if (names.equals(mEditTextName.getText().toString())) {
                mSensor = 1;
            }
        }
    }

    //******Обнуление кнопок, текста, картинок, переменных для блокировки
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mEditTextName.setText("");
        mEditTextKey.setText("");
        mSensor = 0;
        Log.d(TAG, "Dialog 1: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(TAG, "Dialog 1: onCancel");
    }


}
