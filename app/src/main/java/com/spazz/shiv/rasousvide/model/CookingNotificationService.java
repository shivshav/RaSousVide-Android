package com.spazz.shiv.rasousvide.model;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by pula on 2/24/15.
 */
public class CookingNotificationService extends Service {
    @Override
    public void onCreate() {
        Toast.makeText(this, "Starting Service", Toast.LENGTH_SHORT).show();
    }

    /**
     * The system calls this method when another component, such as an activity, requests that the
     * service be started, by calling startService(). Once this method executes, the service is
     * started and can run in the background indefinitely. If you implement this, it is your
     * responsibility to stop the service when its work is done, by calling stopSelf() or
     * topService(). (If you only want to provide binding, you don't need to implement this method.)
     *
     * @return creates method to have permanent notification
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    /**
     * The system calls this method when another component wants to bind with the service (such as
     * to perform RPC), by calling bindService(). In your implementation of this method, you must
     * provide an interface that clients use to communicate with the service, by returning an
     * IBinder. You must always implement this method, but if you don't want to allow binding, then
     * you should return null.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * The system calls this method when the service is no longer used and is being destroyed. Your
     * service should implement this to clean up any resources such as threads, registered
     * listeners, receivers, etc. This is the last call the service receives.
     */
    @Override
    public void onDestroy() {

    }
}
