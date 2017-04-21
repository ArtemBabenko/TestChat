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
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import test_chat_project.testchat.Adapter.Room_List_Adapter;
import test_chat_project.testchat.R;


public class Add_Room_Dialog extends DialogFragment implements OnClickListener {

    final String LOG_TAG = "myLogs";

    public static EditText mEditTextName;
    private EditText mEditTextKey;

    private DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference().getRoot();

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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
        if(v.getId() == R.id.add_room_yes && !(mEditTextName.getText().toString().equals(""))) {

//            Map<String,Object> map = new HashMap<String, Object>();
//            map.put(mEditTextName.getText().toString(),"");
//            mRoot.updateChildren(map);

            DatabaseReference message_root = mRoot.child(mEditTextName.getText().toString());
            Map<String, Object> map2 = new HashMap<String, Object>();
            if(mEditTextKey.getText().toString().equals("")) {
                map2.put("key", "null");
            }else {
                map2.put("key", mEditTextKey.getText().toString());
            }
            message_root.updateChildren(map2);

            dismiss();
        }else  if(v.getId() == R.id.add_room_cancel){
            dismiss();
        }

    }


    //******Обнуление кнопок, текста, картинок, переменных для блокировки
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mEditTextName.setText("");
        mEditTextKey.setText("");
        Log.d(LOG_TAG, "Dialog 1: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "Dialog 1: onCancel");
    }



}
