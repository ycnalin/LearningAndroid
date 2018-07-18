package cn.yclin.pedometer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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


public class ChartViewActivity extends AppCompatActivity {

    private static final String TAG = "ChartViewActivity";

    private DynamicLineChartManager dynamicLineChartManager;
    private DynamicLineChartManager dynamicLineChartManager1;
    private DynamicLineChartManager dynamicLineChartManager2;
    private List<Float> list = new ArrayList<>();
    private List<Float> list1 = new ArrayList<>();
    private List<Float> list2 = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private List<String> names1 = new ArrayList<>();
    private List<String> names2 = new ArrayList<>();
    private List<Integer> colour = new ArrayList<>();

    private SensorManager sensorManager;
    private Sensor sensora;
    private Sensor sensorg;
    private Sensor sensorm;

    private FileIO filewriter;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy_MMdd_HHmmss.'csv'",Locale.getDefault());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view);

        LineChart mLineChart = findViewById(R.id.line_chart);
        LineChart mLineChart1 = findViewById(R.id.line_chart2);
        LineChart mLineChart2 = findViewById(R.id.line_chart3);

        YAxis right = mLineChart.getAxisRight();
        right.setDrawLabels(false);
        right.setDrawAxisLine(false);
        right.setDrawGridLines(false);
        right.setDrawZeroLine(false);

        names.add("GyroX");
        names.add("GyroY");
        names.add("GyroZ");
        //折线颜色
        colour.add(Color.CYAN);
        colour.add(Color.GREEN);
        colour.add(Color.BLUE);

        //for Gyro
        dynamicLineChartManager = new DynamicLineChartManager(mLineChart, names, colour);
        dynamicLineChartManager.setYAxis(10, -10, 10);

        names1.add("AccX");
        names1.add("AccY");
        names1.add("AccZ");
        //for Acc
        dynamicLineChartManager1 = new DynamicLineChartManager(mLineChart1, names1, colour);
        dynamicLineChartManager1.setYAxis(20, -20, 10);
        //for Mag
        names2.add("MagX");
        names2.add("MagY");
        names2.add("MagZ");
        dynamicLineChartManager2 = new DynamicLineChartManager(mLineChart2, names2, colour);
        dynamicLineChartManager2.setYAxis(120, -120, 10);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensora = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorg = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorm = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(listenera, sensora, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listenerg, sensorg, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listenerm, sensorm, SensorManager.SENSOR_DELAY_GAME);

        try {
            PackageInfo pkgInfo = getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);
            String appName = pkgInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            filewriter = new FileIO(Environment.getExternalStorageDirectory().toString()+ File.separator+appName);

            String filename = df.format(System.currentTimeMillis());
            //Log.d(TAG,filename);
            filewriter.setFileToWrite(filename);
        }catch (Exception e) {
            Log.d(TAG,Log.getStackTraceString(e));
        }
    }

    private SensorEventListener listenera = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            list.add(event.values[0]);  //gyrox
            list.add(event.values[1]);  //gyroy
            list.add(event.values[2]);  //gyroz

            dynamicLineChartManager1.addEntry(list);
            list.clear();
            filewriter.writeFile(event.timestamp/100,event.values[0]*100,event.values[1]*100,event.values[2]*100);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private SensorEventListener listenerg = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            list.add(event.values[0]);  //gyrox
            list.add(event.values[1]);  //gyroy
            list.add(event.values[2]);  //gyroz
            dynamicLineChartManager.addEntry(list);
            list.clear();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener listenerm = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            list.add(event.values[0]);  //gyrox
            list.add(event.values[1]);  //gyroy
            list.add(event.values[2]);  //gyroz
            dynamicLineChartManager2.addEntry(list);
            list.clear();

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    };


    /*
    ** 息屏时继续工作
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(listenera);
            sensorManager.unregisterListener(listenerg);
            sensorManager.unregisterListener(listenerm);
            filewriter.closeFile();
        }
    }


}
