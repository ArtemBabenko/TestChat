package test_chat_project.testchat.Dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import test_chat_project.testchat.Main_Chat_Activity;
import test_chat_project.testchat.R;

import static test_chat_project.testchat.Main_Chat_Activity.sPref;


public class User_Change_Name_Dialog extends DialogFragment implements View.OnClickListener {

    final String LOG_TAG = "myLogs";
    private final String USER_NAME = "user_name";
    private EditText mEditTextName;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        setCancelable(false);
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
        if (v.getId() == R.id.add_change_name_yes) {
            if (mEditTextName.getText().toString().equals("")) {
                mEditTextName.setText("");
                mEditTextName.setHint("Incorrect name");
                mEditTextName.setHintTextColor(getResources().getColor(R.color.colorHintError));
            } else {
                saveUserName();
                dismiss();
            }
        } else if (v.getId() == R.id.add_change_name_cancel) {
            dismiss();
        }
    }

    void saveUserName() {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(USER_NAME, mEditTextName.getText().toString());
        ed.commit();
        Main_Chat_Activity.userName = mEditTextName.getText().toString();
        Toast.makeText(getActivity(), "Name changed", Toast.LENGTH_SHORT).show();
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