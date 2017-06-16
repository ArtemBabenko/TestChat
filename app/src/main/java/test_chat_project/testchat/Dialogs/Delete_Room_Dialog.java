package test_chat_project.testchat.Dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import test_chat_project.testchat.Chat_Room;
import test_chat_project.testchat.R;

import static test_chat_project.testchat.Chat_Room.room_name;

public class Delete_Room_Dialog extends DialogFragment implements View.OnClickListener {

    final String LOG_TAG = "myLogs";
    public static String roomName;
    private DatabaseReference root;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        setCancelable(false);
        root = FirebaseDatabase.getInstance().getReference().child("Chat Rooms").child(roomName);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.delete_room_dialog, null);
        v.findViewById(R.id.delete_room_button_yes).setOnClickListener(this);
        v.findViewById(R.id.delete_room_button_cancel).setOnClickListener(this);
        return v;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.delete_room_button_yes) {
            root.removeValue();
            getActivity().finish();
            dismiss();
        } else if (v.getId() == R.id.delete_room_button_cancel) {
            dismiss();
        }
    }


    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "Dialog 1: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "Dialog 1: onCancel");
    }

}