package cn.yclin.pedometer;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class StepService extends Service {
    public static final String TAG = "StepService";

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private StepDetector mStepDetector;

    //utility: steps, distance, speed, calories
    private StepDisplayer mStepDisplayer;

    // hold really time steps
    private int mSteps;


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class StepBinder extends Binder {
        StepService getService() {
            // Return this instance of StepService so clients can call public methods
            return StepService.this;
        }
    }


    @Override
    public void onCreate() {
        Log.i(TAG, "[SERVICE] onCreate");
        super.onCreate();

        // Start detecting
        mStepDetector = new StepDetector();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerDetector();

        mStepDisplayer = new StepDisplayer();
        mStepDisplayer.setSteps(0);
        mStepDisplayer.addListener(mStepListener);
        mStepDetector.addStepListener(mStepDisplayer);


        Toast.makeText(this, getText(R.string.started), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "[SERVICE] onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "[SERVICE] onDestroy");

        // Unregister our receiver.
        unregisterDetector();


        super.onDestroy();
        // Tell the user we stopped.
        Toast.makeText(this, getText(R.string.stopped), Toast.LENGTH_SHORT).show();
    }

    // Binder given to clients
    private final IBinder mBinder = new StepBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** method for clients */

    public interface ICallback {
        public void stepsChanged(int value);
//        public void paceChanged(int value);
//        public void distanceChanged(float value);
//        public void speedChanged(float value);
//        public void caloriesChanged(float value);
    }

    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }

    /**
     * Forwards pace values from PaceNotifier to the activity.
     */
    private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
        public void stepsChanged(int value) {
            mSteps = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.stepsChanged(mSteps);
            }
        }
    };



    private void registerDetector() {
        Log.i(TAG, "registerDetector");
        mSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER /*|
            Sensor.TYPE_MAGNETIC_FIELD |
            Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepDetector,
                mSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterDetector() {
        Log.i(TAG, "unregisterDetector");
        mSensorManager.unregisterListener(mStepDetector);
    }

}
