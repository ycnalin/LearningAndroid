package cn.yclin.pedometer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Locale;

public class SensorDataActivity extends AppCompatActivity {

    private SensorManager sensorManager;

    private TextView mGyroX;
    private TextView mGyroY;
    private TextView mGyroZ;
    private TextView mAccX;
    private TextView mAccY;
    private TextView mAccZ;
    private TextView mMagX;
    private TextView mMagY;
    private TextView mMagZ;
    private Button mCharView;
    private Button mPedometer;
    private Sensor sensora;
    private Sensor sensorg;
    private Sensor sensorm;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        verifyStoragePermissions(SensorDataActivity.this);

        mGyroX = findViewById(R.id.gyrox);
        mGyroY = findViewById(R.id.gyroy);
        mGyroZ = findViewById(R.id.gryoz);
        mAccX = findViewById(R.id.accx);
        mAccY = findViewById(R.id.accy);
        mAccZ = findViewById(R.id.accz);
        mMagX = findViewById(R.id.magx);
        mMagY = findViewById(R.id.magy);
        mMagZ = findViewById(R.id.magz);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensora = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorg = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorm = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mCharView = findViewById(R.id.chart_button);
        mCharView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SensorDataActivity.this, ChartViewActivity.class);
                startActivity(intent);

            }
        });

        mPedometer = findViewById(R.id.pedometer_button);
        mPedometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SensorDataActivity.this, PedometerActivity.class);
                startActivity(intent);
            }
        });
    }

    private SensorEventListener listenera = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float accx = event.values[0];
            float accy = event.values[1];
            float accz = event.values[2];

            mAccX.setText(String.format(Locale.getDefault(),"%+06.2f",accx));
            mAccY.setText(String.format(Locale.getDefault(),"%+06.2f",accy));
            mAccZ.setText(String.format(Locale.getDefault(),"%+06.2f",accz));
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private SensorEventListener listenerg = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float gyrox = event.values[0];
            float gyroy = event.values[1];
            float gyroz = event.values[2];

            mGyroX.setText(String.format(Locale.getDefault(),"%+06.2f",gyrox));
            mGyroY.setText(String.format(Locale.getDefault(),"%+06.2f",gyroy));
            mGyroZ.setText(String.format(Locale.getDefault(),"%+06.2f",gyroz));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener listenerm = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float magx = event.values[0];
            float magy = event.values[1];
            float magz = event.values[2];
            //DecimalFormat decimalFormat=new DecimalFormat("00.00");
            mMagX.setText(String.format(Locale.getDefault(),"%+06.2f",magx));
            mMagY.setText(String.format(Locale.getDefault(),"%+06.2f",magy));
            mMagZ.setText(String.format(Locale.getDefault(),"%+06.2f",magz));

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(listenera, sensora, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listenerg, sensorg, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listenerm, sensorm, SensorManager.SENSOR_DELAY_GAME);
    }

    /*
     ** 息屏时停止工作
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(listenera);
            sensorManager.unregisterListener(listenerg);
            sensorManager.unregisterListener(listenerm);
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
