package test_chat_project.testchat.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import test_chat_project.testchat.Main_Chat_Activity;
import test_chat_project.testchat.R;


public class FirebaseNotificationService extends Service {

    private static final String APP_USER_INFO = "APP_USER_NAME";
    private static final String USER_PROFILE_KEY = "user_profile_key";

    SharedPreferences sharedPreferences;
    String userKeyProfile;
    public FirebaseDatabase mDatabase;
    FirebaseAuth firebaseAuth;
    Context context;
    static String TAG = "FirebaseService";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadUserKeyProfile();

        mDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        setupNotificationListener();
    }


    private boolean alReadyNotified(String key) {
        if (sharedPreferences.getBoolean(key, false)) {
            return true;
        } else {
            return false;
        }
    }


    private void saveNotificationKey(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, true);
        editor.commit();
    }

    private void setupNotificationListener() {

        mDatabase.getReference().child("notifications")
                .child(firebaseAuth.getCurrentUser().getUid())
                .orderByChild("status").equalTo(0)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot != null) {
                            Notification notification = dataSnapshot.getValue(Notification.class);

                            showNotification(context, notification, dataSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(getApplicationContext(), FirebaseNotificationService.class));
    }


    private void showNotification(Context context, Notification notification, String notification_key) {
        flagNotificationAsSent(notification_key);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.emerald_logo)
                .setContentTitle(notification.getDescription())
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText(Html.fromHtml(notification.getMessage()
                ))
                .setAutoCancel(true);

        Intent backIntent = new Intent(context, Main_Chat_Activity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intent = new Intent(context, Main_Chat_Activity.class);

        /*  Use the notification type to switch activity to stack on the main activity*/
        if (notification.getType().equals("chat_view")) {
            intent = new Intent(context, Main_Chat_Activity.class);
        }


        final PendingIntent pendingIntent = PendingIntent.getActivities(context, 900,
                new Intent[]{backIntent}, PendingIntent.FLAG_ONE_SHOT);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(Main_Chat_Activity.class);

        mBuilder.setContentIntent(pendingIntent);


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }

    private void flagNotificationAsSent(String notification_key) {
        mDatabase.getReference().child("notifications")
                .child(firebaseAuth.getCurrentUser().getUid())
                .child(notification_key)
                .child("status")
                .setValue(1);
    }

    //Load User Key Profile
    private void loadUserKeyProfile() {
        sharedPreferences = getSharedPreferences(APP_USER_INFO, Context.MODE_PRIVATE);
        String keyProfile = sharedPreferences.getString(USER_PROFILE_KEY, "empty");
        userKeyProfile = keyProfile;
    }

}
