package com.example.sensor_output;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorLogger.SensorLoggerListener{
    SensorLogger myLogger;
    int[] sensorSeries;
    SensorValues receivedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myLogger = new SensorLogger(this, false);
        sensorSeries = myLogger.getSensorList();
        System.arraycopy(myLogger.getSensorList(), 0, sensorSeries, 0, myLogger.Max_Size);
        myLogger.startLog(sensorSeries,20);
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

    private String make_outputText()
    {
        String outputText = "";

        if(sensorSeries[0]==1)
        {
            outputText+="ACCELEROMETER: "+receivedValues.acc_x+", "+receivedValues.acc_y+", "+receivedValues.acc_z+"\n";
        }

        if(sensorSeries[1]==1)
        {
            outputText+="ACCELEROMETER_UNCALIBRATED: "+receivedValues.accU_x+", "+receivedValues.accU_y+", "+receivedValues.accU_z+", "+
                    receivedValues.accUB_x+", "+receivedValues.accUB_y+", "+receivedValues.accUB_z+"\n";
        }

        if(sensorSeries[2]==1)
        {
            outputText+="GRAVITY: "+receivedValues.grv_x+", "+receivedValues.grv_y+", "+receivedValues.grv_z+"\n";
        }

        if(sensorSeries[3]==1)
        {
            outputText+="GYROSCOPE: "+receivedValues.gyr_x+", "+receivedValues.gyr_y+", "+receivedValues.gyr_z+"\n";
        }

        if(sensorSeries[4]==1)
        {
            outputText+="GYROSCOPE_UNCALIBRATED: "+receivedValues.gyrU_x+", "+receivedValues.gyrU_y+", "+receivedValues.gyrU_z+", "+
                    receivedValues.gyrUD_x+", "+receivedValues.gyrUD_y+", "+receivedValues.gyrUD_z+"\n";
        }

        if(sensorSeries[5]==1)
        {
            outputText+="LINEAR_ACCELERATION: "+receivedValues.accL_x+", "+receivedValues.accL_y+", "+receivedValues.accL_z+"\n";
        }

        if(sensorSeries[6]==1)
        {
            outputText+="ROTATION_VECTOR: "+receivedValues.vec_x+", "+receivedValues.vec_y+", "+receivedValues.vec_z+", "+receivedValues.vec_scl+"\n";
        }

        if(sensorSeries[7]==1)
        {
            outputText+="SIGNIFICANT_MOTION: None\n";
        }

        if(sensorSeries[8]==1)
        {
            outputText+="STEP_COUNTER: "+receivedValues.count_step+"\n";
        }

        if(sensorSeries[9]==1)
        {
            outputText+="STEP_DETECTOR: None\n";
        }

        if(sensorSeries[10]==1)
        {
            outputText+="GAME_ROTATION_VECTOR: "+receivedValues.vecRGame_x+", "+receivedValues.vecRGame_y+", "+receivedValues.vecRGame_z+"\n";
        }

        if(sensorSeries[11]==1)
        {
            outputText+="GEOMAGNETIC_ROTATION_VECTOR: "+receivedValues.vecRGeo_x+", "+receivedValues.vecRGeo_y+", "+receivedValues.vecRGeo_z+"\n";
        }

        if(sensorSeries[12]==1)
        {
            outputText+="MAGNETIC_FIELD: "+receivedValues.mag_x+", "+receivedValues.mag_y+", "+receivedValues.mag_z+"\n";
        }

        if(sensorSeries[13]==1)
        {
            outputText+="MAGNETIC_FIELD_UNCALIBRATED: "+receivedValues.magU_x+", "+receivedValues.magU_y+", "+receivedValues.magU_z+", "+
                    receivedValues.magUB_x+", "+receivedValues.magUB_y+", "+receivedValues.magUB_z+"\n";
        }

        if(sensorSeries[14]==1)
        {
            outputText+="ORIENTATION: "+receivedValues.ori_x+", "+receivedValues.ori_y+", "+receivedValues.ori_z+"\n";
        }

        if(sensorSeries[15]==1)
        {
            outputText+="PROXIMITY: "+receivedValues.prx+"\n";
        }

        if(sensorSeries[16]==1)
        {
            outputText+="AMBIENT_TEMPERATURE: "+receivedValues.amb_temp+"\n";
        }

        if(sensorSeries[17]==1)
        {
            outputText+="LIGHT: "+receivedValues.light+"\n";
        }

        if(sensorSeries[18]==1)
        {
            outputText+="PRESSURE: "+receivedValues.press+"\n";
        }

        if(sensorSeries[19]==1)
        {
            outputText+="RELATIVE_HUMIDITY: "+receivedValues.hum+"\n";
        }

        if(sensorSeries[20]==1)
        {
            outputText+="TEMPERATURE: "+receivedValues.temp+"\n";
        }

        if(sensorSeries[21]==1)
        {
            outputText+="MOTION_DETECT: "+receivedValues.motion_dtc+"\n";
        }

        if(sensorSeries[22]==1)
        {
            outputText+="STATIONARY_DETECT: "+receivedValues.station_dtc+"\n";
        }

        if(sensorSeries[23]==1)
        {
            outputText+="LOW_LATENCY_OFFBODY_DETECT: "+receivedValues.offbody_dtc+"\n";
        }

        if(sensorSeries[24]==1)
        {
            outputText+="MAGNETIC_FIELD_UNCALIBRATED: "+receivedValues.pose_x+", "+receivedValues.pose_y+", "+receivedValues.pose_z+", "+
                    receivedValues.pose_trsx+", "+receivedValues.pose_trsy+", "+receivedValues.pose_trsz+", "+
                    receivedValues.pose_quaterx+", "+receivedValues.pose_quatery+", "+receivedValues.pose_quaterz+", "+
                    receivedValues.pose_dlttrsx+", "+receivedValues.pose_dlttrsy+", "+receivedValues.pose_dlttrsz+", "+
                    receivedValues.num_seq+"\n";;
        }

        if(sensorSeries[25]==1)
        {
            outputText+="DEVICE_PRIVATE_BASE: None\n";
        }

        if(sensorSeries[26]==1)
        {
            outputText+="HEART_BEAT: "+receivedValues.heart_B+"\n";
        }

        if(sensorSeries[27]==1)
        {
            outputText+="HEART_RATE: "+receivedValues.heart_R+"\n";
        }

        return outputText;
    }

    @Override
    public void onSensorValueReceived(SensorValues values) {
        receivedValues = values;
        TextView textView = findViewById(R.id.text);
        textViewWriter(textView, make_outputText());
    }
}
