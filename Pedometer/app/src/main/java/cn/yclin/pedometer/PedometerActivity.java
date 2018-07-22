package cn.yclin.pedometer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PedometerActivity extends AppCompatActivity {

    private static final String TAG = "PedometerActivity";
    private DynamicLineChartManager dynamicLineChartManager;

    private SensorManager sensorManager;
    private Sensor sensora;
    private Sensor sensorg;
    private FileIO filewriter;
    private Filter filter;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy_MMdd_HHmmss.'csv'",Locale.getDefault());
    List<Float> list = new ArrayList<>();

    private float[] gravity = new float[]{0,0,0};
    private int gravityCounter;

    // plot fig
    private float[] result;
    private List<String> names = new ArrayList<>();
    private List<Integer> colour = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensora = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorg = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        gravityCounter = 0;
        sensorManager.registerListener(listenerg, sensorg, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listenera, sensora, SensorManager.SENSOR_DELAY_GAME);

        try {
            PackageInfo pkgInfo = getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);
            String appName = pkgInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            filewriter = new FileIO(Environment.getExternalStorageDirectory().toString()+ File.separator+appName);
            String filename = df.format(System.currentTimeMillis());
            filewriter.setFileToWrite(filename);
        }catch (Exception e) {
            Log.d(TAG,Log.getStackTraceString(e));
        }

        filter = new Filter();

        LineChart mLineChart = findViewById(R.id.pedometer_line_chart);
        YAxis right = mLineChart.getAxisRight();
        right.setDrawLabels(false);
        right.setDrawAxisLine(false);
        right.setDrawGridLines(false);
        right.setDrawZeroLine(false);

        names.add("AccX");
        names.add("AccY");
        names.add("AccZ");
        //折线颜色
        colour.add(Color.CYAN);
        colour.add(Color.GREEN);
        colour.add(Color.BLUE);
        //for Accel
        dynamicLineChartManager = new DynamicLineChartManager(mLineChart, names, colour);
        dynamicLineChartManager.setYAxis(20, -20, 10);
    }

    private SensorEventListener listenera = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];
            long now = System.currentTimeMillis();
            //result = filter.update(ax,ay,az);
//            list.add(result[0]);
//            list.add(result[1]);
//            list.add(result[2]);
//            dynamicLineChartManager.addEntry(list);
//            list.clear();
            filewriter.writeFile(now,ax*100,ay*100,az*100);
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener listenerg = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float gx = event.values[0];
            float gy = event.values[1];
            float gz = event.values[2];

            if(gravityCounter > 50) {
                filter.setInitGravity(gx, gy, gz);
                stopLog();
            }
            else
                ++gravityCounter;
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    private void stopLog(){
        sensorManager.unregisterListener(listenerg);
        sensorManager.registerListener(listenera, sensora, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(listenera);
            filewriter.closeFile();
        }
    }
}
