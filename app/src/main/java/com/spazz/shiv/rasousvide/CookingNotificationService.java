package com.spazz.shiv.rasousvide;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.spazz.shiv.rasousvide.rest.RestClient;
import com.spazz.shiv.rasousvide.rest.model.ShivVideResponse;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;


/**
 * Created by pula on 2/24/15.
 */
public class CookingNotificationService extends Service {

    private static final String TAG = CookingNotificationService.class.getSimpleName();
    private final IBinder binder = new LocalBinder();

    private Observable<ShivVideResponse> pollingObservable;

//    private ServiceListener callback;

    public interface ServiceListener {
        public void onServiceStarted();
        public void onServiceStopped();
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public CookingNotificationService getService() {
            // Return this instance of CookingNotificationService so clients can call public methods
            return CookingNotificationService.this;
        }

    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Starting Service", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCreate of Service");
    }

    /**
     * The system calls this method when another component, such as an activity, requests that the
     * service be started, by calling startService(). Once this method executes, the service is
     * started and can run in the background indefinitely. If you implement this, it is your
     * responsibility to stop the service when its work is done, by calling stopSelf() or
     * topService(). (If you only want to provide binding, you don't need to implement this method.)
     *
     * Three options for priorities:
     * @Int START_NOT_STICKY do not recreate the service; app can simply restart unfinished jobs
     * @Int START_STICKY recreate service and call onStartCommand(); used in media player apps
     * @Int START_REDELIVER_INTENT recreate service and continue with last intent; downloading files
     *
     * @return creates method to have permanent notification; doesn't need to continue since we
     *          repoll every 5 minutes anyways
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "onStartCommand()", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onStartCommand Service run");
        pollingObservable = createPollingObservable();
        return START_NOT_STICKY;
    }

    private Observable<ShivVideResponse> createPollingObservable() {
//        return Observable.interval(3, TimeUnit.SECONDS);
        return Observable.interval(1, TimeUnit.SECONDS)
                .flatMap((num) -> RestClient.getAPI().getCurrentPiParams())
//                .map(
//                        response -> {
//                            Log.d("OBSERVABLE", "Current thread: " + Thread.currentThread());
//                            String tempStr = response.getTemp();
//                            return tempStr;
//                        }
//                )
                .distinct();
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
        return binder;
    }

    /**
     * The system calls this method when the service is no longer used and is being destroyed. Your
     * service should implement this to clean up any resources such as threads, registered
     * listeners, receivers, etc. This is the last call the service receives.
     */
    @Override
    public void onDestroy() {
        Toast.makeText(this, "DESTROY ALL SERVICE", Toast.LENGTH_SHORT).show();
    }

    public Observable<ShivVideResponse> getPollingObservable() {
        return pollingObservable;
    }
}
