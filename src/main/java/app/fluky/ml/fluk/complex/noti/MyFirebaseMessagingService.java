package app.fluky.ml.fluk.complex.noti;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("operation32131", "From: " + remoteMessage.getFrom());
        Log.d("operation32131", "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }
}
