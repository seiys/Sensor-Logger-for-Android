package com.example.sensor_output;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SensorLogger.SensorLoggerListener{

    private final String[] FROM = {"sensor", "value", "check"};
    private final int[] TO = {R.id.textView, R.id.textView2, R.id.checkBox};

    private class MyAdapter extends SimpleAdapter {

        public Map<Integer,Boolean> checkList = new HashMap<>();
        public MyAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            for(int i=0; i<data.size();i++){
                Map map = (Map)data.get(i);
                checkList.put(i,(Boolean)map.get("check"));
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            CheckBox ch = view.findViewById(R.id.checkBox);

            if(!checkList.get(position)){
                ch.setChecked(false);
            }
            else
            {
                ch.setChecked(true);
            }

            if(availableSensors[position]==0 || myLogger.isLogging){
                ch.setEnabled(false);
            }
            else
            {
                ch.setEnabled(true);
            }

            ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(buttonView.isPressed()) {
                        checkList.put(position, isChecked);
                        if (isChecked) {
                            sensorSeries[position] = 1;
                        } else {
                            sensorSeries[position] = 0;
                        }
                    }
                }
            });
            return view;
        }
    }

    private SensorLogger myLogger;
    private int[] sensorSeries;
    private int[] availableSensors;
    private SensorValues receivedValues = new SensorValues();
    private SensorValues2CSV sensorValues2CSV = new SensorValues2CSV();
    private CSVWriter myCSVWriter;
    private String fileName = "test";
    private int samplingTime = 15;
    // Correction value
    private final int corrTime = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = findViewById(R.id.loggerButton);

        myLogger = new SensorLogger(this);
        myCSVWriter = new CSVWriter(this);
        availableSensors = new int[myLogger.Max_Size];
        sensorSeries = new int[myLogger.Max_Size];

        // Check available sensors
        System.arraycopy(myLogger.getSensorList(), 0, availableSensors, 0, myLogger.Max_Size);
        Arrays.fill(sensorSeries,0);

        // Initialize list view
        initListData();

        // Start/Stop sensor logger
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!myLogger.isLogging) {
                    int sum=0;
                    for(int i=0;i<sensorSeries.length;i++)
                    {
                        sum+=sensorSeries[i];
                    }
                    if(sum>0) {
                        myLogger.startLog(sensorSeries, samplingTime );
                        button.setText("stop");
                    }
                    myCSVWriter.init(fileName,sensorValues2CSV.publishHeader(receivedValues, sensorSeries));
                }
                else
                {
                    myLogger.stopLog();
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setListData();
                    myCSVWriter.close();
                    button.setText("start");
                }
            }
        });

        // Edit text
        final EditText fileNameView = findViewById(R.id.fileNameText);
        fileNameView.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                fileName = fileNameView.getText().toString();
                if(fileName.equals(""))
                {
                    fileName = "test";
                }
            }
        });

        fileNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        final EditText samplingTimeView = findViewById(R.id.samplingTime);
        samplingTimeView.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(samplingTimeView.getText().toString().equals("")) {
                    samplingTime = 1;
                }
                else{
                    samplingTime = Integer.valueOf(samplingTimeView.getText().toString()) - corrTime;
                }

                if(samplingTime<=0){
                    samplingTime =1;
                }
            }
        });

        samplingTimeView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        final ListView lv = findViewById(R.id.listView);
    }

    public void initListData(){
        ListView lv = findViewById(R.id.listView);
        List<Map<String,Object>> list = new ArrayList<>();

        int counter = 0;
        for(Map.Entry<String, List<Float>> entry : receivedValues.map.entrySet()) {
            Map<String,Object> map = new HashMap<>();
            map.put("sensor",entry.getKey());

            if(entry.getValue().size()==0)
            {
                map.put("value","None");
            }
            else
            {
                String output = "";
                for (int i = 0; i < entry.getValue().size(); i++) {
                    output+=String.format("%.3f",receivedValues.map.get(entry.getKey()).get(i));
                    if(i+1!=entry.getValue().size())
                    {
                        output+=", ";
                    }
                }
                map.put("value",output);
            }

            if(sensorSeries[counter]==0)
            {
                map.put("check", false);
            }
            else
            {
                map.put("check", true);
            }

            list.add(map);
            counter++;
        }

        MyAdapter adapter = new MyAdapter(MainActivity.this,
                list,R.layout.list,FROM,TO);
        lv.setAdapter(adapter);
    }

    public void setListData(){
        ListView lv = findViewById(R.id.listView);
        MyAdapter adapter = (MyAdapter) lv.getAdapter();

        int counter = 0;
        int listCounter = 0;
        for(Map.Entry<String, List<Float>> entry : receivedValues.map.entrySet()) {
            Map<String,Object> map = (Map<String, Object>) adapter.getItem(listCounter);

            if(entry.getValue().size()!=0)
            {
                String output = "";
                for (int i = 0; i < entry.getValue().size(); i++) {
                    output+=String.format("%.3f",receivedValues.map.get(entry.getKey()).get(i));
                    if(i+1!=entry.getValue().size())
                    {
                        output+=", ";
                    }
                }
                map.remove("value");
                map.put("value",output);
            }

            map.remove("check");
            if(sensorSeries[counter]==0)
            {
                map.put("check", false);
            }
            else
            {
                map.put("check", true);
            }

            listCounter++;
            counter++;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSensorValueReceived(SensorValues values) {
        receivedValues = values;
        setListData();
        myCSVWriter.writeLine(sensorValues2CSV.publishValuesLine(receivedValues, sensorSeries));
    }

}
