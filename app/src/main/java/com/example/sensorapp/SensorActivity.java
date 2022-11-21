package com.example.sensorapp;

import static com.example.sensorapp.SensorDetailsActivity.SENSOR_LOG_TAG;
import static com.example.sensorapp.SensorDetailsActivity.SENSOR_TYPE_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class SensorActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private RecyclerView recyclerView;
    private SensorAdapter adapter;
    private final List<Integer> mySensors = Arrays.asList(Sensor.TYPE_LIGHT, Sensor.TYPE_ORIENTATION);
    private boolean subtitleVisible = false;

    public static final int SENSOR_REQUEST = 2;
    public final String SUBTITLE_KEY = "SUBTITLE_VISIBLE";


    @Override
    protected void onResume() {
        super.onResume();
        updateSubtitle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        if(savedInstanceState != null){
            subtitleVisible = savedInstanceState.getBoolean(SUBTITLE_KEY);
        }

        recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        if(adapter == null){
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        }
        else{
            adapter.notifyDataSetChanged();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sensors_menu, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_sensors_count);
        if(subtitleVisible){
            subtitleItem.setTitle(R.string.hide_sensors_count);
        }
        else{
            subtitleItem.setTitle(R.string.show_sensors_count);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.show_sensors_count:
                subtitleVisible = !subtitleVisible;
                if(subtitleVisible){
                    String string = getString(R.string.sensors_count, sensorList.size());
                    getSupportActionBar().setSubtitle(string);
                    item.setTitle(R.string.hide_sensors_count);
                }
                else{
                    getSupportActionBar().setSubtitle("");
                    item.setTitle(R.string.show_sensors_count);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SUBTITLE_KEY, subtitleVisible);
    }

    private void updateSubtitle(){
        if(subtitleVisible){
            String string = getString(R.string.sensors_count, sensorList.size());
            getSupportActionBar().setSubtitle(string);
        }
        else{
            getSupportActionBar().setSubtitle("");
        }
    }

    private class SensorHolder extends RecyclerView.ViewHolder{
        private ImageView sensorIcon;
        private TextView nameView;
        private Sensor sensor;

        public SensorHolder(LayoutInflater inflater, ViewGroup parent){
            super((inflater.inflate(R.layout.sensor_list_item, parent, false)));

            sensorIcon = itemView.findViewById(R.id.sensor_icon);
            nameView = itemView.findViewById(R.id.sensor_name);
        }

        public void bind(Sensor sensor){
            this.sensor = sensor;
            nameView.setText(sensor.getName());
            View item = itemView.findViewById(R.id.sensor_item);

            if(mySensors.contains(sensor.getType())){
                item.setBackgroundColor((getResources().getColor(R.color.sensor_available)));
                item.setOnClickListener(v ->{
                    Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                    intent.putExtra(SENSOR_TYPE_KEY, sensor.getType());
                    startActivityForResult(intent, SENSOR_REQUEST);
                });
            }

            if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                item.setBackgroundColor(getResources().getColor(R.color.magnetic_sensor));
                item.setOnClickListener(v -> {
                    Intent intent = new Intent(SensorActivity.this, LocationActivity.class);
                    startActivityForResult(intent, SENSOR_REQUEST);
                });
            }

        }
    }

    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder>{
        private List<Sensor> sensors;

        public SensorAdapter(List<Sensor> sensors){
            this.sensors = sensors;
        }

        @NonNull
        @Override
        public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            return new SensorHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
            Sensor sensor = sensorList.get(position);
            Log.d(SENSOR_LOG_TAG, "Producent: " + sensor.getVendor() + "Maksymalna zwracana wartość: " + sensor.getMaximumRange());
            holder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return sensors.size();
        }
    }
}