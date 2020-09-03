package com.example.sensor_output;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SensorLogger.SensorLoggerListener{
    SensorLogger myLogger;
    int[] sensorSeries;
    SensorValues receivedValues = new SensorValues();
    SensorValues2CSV sensorValues2CSV = new SensorValues2CSV();
    CSVWriter myCSVWriter;
    int ccsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myLogger = new SensorLogger(this);
        sensorSeries = new int[myLogger.Max_Size];
        System.arraycopy(myLogger.getSensorList(), 0, sensorSeries, 0, myLogger.Max_Size);
        myLogger.startLog(sensorSeries,20);
        myCSVWriter = new CSVWriter(this);
        myCSVWriter.init("test", sensorValues2CSV.publishHeader(receivedValues, sensorSeries));
        ccsv=0;
    }

    public void textViewWriter(final TextView view, final String text){
        Thread thread = new Thread()
        {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        view.setText(text);
                    }
                });
            }
        };
        thread.start();
    }

    private String make_outputText() {
        String outputText = "";

        int counter = 0;
        for(Map.Entry<String, List<Float>> entry : receivedValues.map.entrySet()) {
            if(sensorSeries[counter]==1)
            {
                outputText+=entry.getKey()+": ";
                if(entry.getValue().size()==0)
                {
                    outputText+="None";
                }
                else
                {
                    for (int i = 0; i < entry.getValue().size(); i++) {
                        outputText+=receivedValues.map.get(entry.getKey()).get(i).toString();
                        if(i+1!=entry.getValue().size())
                        {
                            outputText+=", ";
                        }
                    }
                }
                outputText+="\n";
            }
            counter++;
        }

        return outputText;
    }

    @Override
    public void onSensorValueReceived(SensorValues values) {
        receivedValues = values;
        TextView textView = findViewById(R.id.text);
        textViewWriter(textView, make_outputText());
        if(ccsv<10)
        {
            myCSVWriter.writeLine(sensorValues2CSV.publishValuesLine(receivedValues, sensorSeries));
            ccsv++;
            if(ccsv==10)
            {
                myCSVWriter.close();
            }
        }
    }
}
