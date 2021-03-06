package test_chat_project.testchat.notification;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class Utilities {

    public static void sendNotification(final Context context,String user_id, String message, String description, String type){
        final String TAG = "myLogs";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notifications").child(user_id);
        String pushKey = databaseReference.push().getKey();

        Notification notification = new Notification();
        notification.setDescription(description);
        notification.setMessage(message);
        notification.setUser_id(user_id);
        notification.setType(type);

        Map<String, Object> forumValues = notification.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(pushKey, forumValues);
        databaseReference.setPriority(ServerValue.TIMESTAMP);
        databaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null){
                    Log.d(TAG,"Notifications send");
                }
            }
        });
    }
}

