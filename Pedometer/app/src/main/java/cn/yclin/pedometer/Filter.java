package cn.yclin.pedometer;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Filter {

    private static final String TAG = "Filter";
    // Constants for the low-pass filters
    private float timeConstant = 0.18f;    // alpha = 0.7
    private float alpha = 0.9f;
    private float dt = 0;

    // Timestamps for the low-pass filters
    private float timestamp;
    private float timestampOld = System.nanoTime();

    // Gravity and linear accelerations components for the
    // Wikipedia low-pass filter
    private float[] gravity = new float[]{ 0, 0, 0 };

    private float[] result = new float[]{0,0,0};

    private long count = 1;

    public float[] update(float ax,float ay,float az)
    {

        timestamp = System.nanoTime();
        // Find the sample period (between updates).
        // Convert from nanoseconds to seconds
        dt = 1 / (count / ((timestamp - timestampOld) / 1000000000.0f));

        count++;

        alpha = timeConstant / (timeConstant + dt);
//        if(count%100 == 0)
//            Log.d(TAG,"aplha=:"+alpha);

        gravity[0] = alpha * gravity[0] + (1 - alpha) * ax;
        gravity[1] = alpha * gravity[1] + (1 - alpha) * ay;
        gravity[2] = alpha * gravity[2] + (1 - alpha) * az;

        result[0] = ax - gravity[0];
        result[1] = ay - gravity[1];
        result[2] = az - gravity[2];

        return result;
    }

    public void setInitGravity(float x,float y,float z) {
       gravity[0]=x;
       gravity[1]=y;
       gravity[2]=z;
       timestampOld = System.nanoTime();
    }
}
