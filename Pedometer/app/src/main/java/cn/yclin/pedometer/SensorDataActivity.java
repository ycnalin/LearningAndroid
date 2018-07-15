package cn.yclin.pedometer;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

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
        Sensor sensora = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listenera, sensora, SensorManager.SENSOR_DELAY_GAME);
        Sensor sensorg = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(listenerg, sensorg, SensorManager.SENSOR_DELAY_GAME);
        Sensor sensorm = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(listenerm, sensorm, SensorManager.SENSOR_DELAY_GAME);

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
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(listenera);
            sensorManager.unregisterListener(listenerg);
            sensorManager.unregisterListener(listenerm);
        }
    }

}
