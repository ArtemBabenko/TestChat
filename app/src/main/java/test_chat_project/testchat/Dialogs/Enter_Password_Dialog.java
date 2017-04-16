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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import test_chat_project.testchat.R;




public class Enter_Password_Dialog extends DialogFragment implements OnClickListener {

    final String LOG_TAG = "myLogs";

    private EditText mEditTextPassword;

    private DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference().getRoot();


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
            Toast.makeText(getActivity(),"Button Yes",Toast.LENGTH_SHORT).show();
            dismiss();
        }else if(v.getId() == R.id.add_password_cancel){

            Toast.makeText(getActivity(), "Button Cancel", Toast.LENGTH_SHORT).show();
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



