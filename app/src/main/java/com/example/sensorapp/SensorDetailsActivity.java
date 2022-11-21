package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView sensorLightTextView;
    private TextView sensorName;
    static final String SENSOR_TYPE_KEY = "SENSOR_TYPE_KEY";
    static final String SENSOR_LOG_TAG = "SENSOR_LOGS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        sensorLightTextView = findViewById(R.id.senor_detail_label);
        sensorName = findViewById(R.id.sensor_detail_name);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        int type = getIntent().getIntExtra(SENSOR_TYPE_KEY, Sensor.TYPE_ACCELEROMETER);
        sensor = sensorManager.getDefaultSensor(type);

        if(sensor == null){
            sensorLightTextView.setText(R.string.missing_sensor);
        }
        else{
            sensorName.setText(sensor.getName());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(SENSOR_LOG_TAG, "On accuracy changed");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(sensor != null){
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        int sensorType = sensorEvent.sensor.getType();
        float currentValue = sensorEvent.values[0];

        switch(sensorType){
            case Sensor.TYPE_LIGHT:
                sensorLightTextView.setText(getResources().getString(R.string.light_sensor_label, currentValue));
                break;
            case Sensor.TYPE_ORIENTATION:
                sensorLightTextView.setText(getResources().getString(R.string.orientation_sensor_label, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
                break;
            default:
                break;
        }
    }
}