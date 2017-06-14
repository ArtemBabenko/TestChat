package test_chat_project.testchat.Dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import test_chat_project.testchat.Chat_Room;
import test_chat_project.testchat.Main_Chat_Activity;
import test_chat_project.testchat.R;


public class Enter_Password_Dialog extends DialogFragment implements OnClickListener {

    final String LOG_TAG = "myLogs";
    public static String password;
    public static String creator;
    public static String roomName;
    private EditText mEditTextPassword;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.enter_password_dialog, null);
        v.findViewById(R.id.add_password_yes).setOnClickListener(this);
        v.findViewById(R.id.add_password_cancel).setOnClickListener(this);
        mEditTextPassword = (EditText) v.findViewById(R.id.enter_password_text);

        return v;
    }

    public void onClick(View v) {
        if(v.getId() == R.id.add_password_yes && !(mEditTextPassword.getText().toString().equals(""))) {
            if(mEditTextPassword.getText().toString().equals(password)){
                Intent intent = new Intent(v.getContext(),Chat_Room.class);
                intent.putExtra("room_name", roomName);
                intent.putExtra("creator", creator);
                intent.putExtra("user_name", Main_Chat_Activity.userName);
                v.getContext().startActivity(intent);
                dismiss();
            }else
                mEditTextPassword.setText("");
                mEditTextPassword.setHint("Incorrect password");
                mEditTextPassword.setHintTextColor(getResources().getColor(R.color.colorHintError));
        }else if(v.getId() == R.id.add_password_cancel){
            dismiss();
        }
    }


    //******Обнуление кнопок, текста, картинок, переменных для блокировки
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mEditTextPassword.setText("");
        Log.d(LOG_TAG, "Dialog 1: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "Dialog 1: onCancel");
    }

}



