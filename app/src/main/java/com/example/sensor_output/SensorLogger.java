package com.example.sensor_output;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SensorLogger implements SensorEventListener {

    public final int Max_Size = 28;

    public final LinkedHashMap<String, Integer> sensorListMap = new LinkedHashMap<String, Integer>()
    {
        {
            // Motion Sensors
            put("ACCELEROMETER", Sensor.TYPE_ACCELEROMETER);
            put("ACCELEROMETER_UNCALIBRATED", Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
            put("GRAVITY", Sensor.TYPE_GRAVITY);
            put("GYROSCOPE", Sensor.TYPE_GYROSCOPE);
            put("GYROSCOPE_UNCALIBRATED", Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
            put("LINEAR_ACCELERATION",Sensor.TYPE_LINEAR_ACCELERATION);
            put("ROTATION_VECTOR",Sensor.TYPE_ROTATION_VECTOR);
            put("SIGNIFICANT_MOTION",Sensor.TYPE_SIGNIFICANT_MOTION);
            put("STEP_COUNTER",Sensor.TYPE_STEP_COUNTER);
            put("STEP_DETECTOR",Sensor.TYPE_STEP_DETECTOR);

            // Position Sensors
            put("GAME_ROTATION_VECTOR",Sensor.TYPE_GAME_ROTATION_VECTOR);
            put("GEOMAGNETIC_ROTATION_VECTOR",Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
            put("MAGNETIC_FIELD",Sensor.TYPE_MAGNETIC_FIELD);
            put("MAGNETIC_FIELD_UNCALIBRATED",Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
            put("ORIENTATION",Sensor.TYPE_ORIENTATION);
            put("PROXIMITY",Sensor.TYPE_PROXIMITY);

            // Environment Sensors
            put("AMBIENT_TEMPERATURE",Sensor.TYPE_AMBIENT_TEMPERATURE);
            put("LIGHT",Sensor.TYPE_LIGHT);
            put("PRESSURE",Sensor.TYPE_PRESSURE);
            put("RELATIVE_HUMIDITY",Sensor.TYPE_RELATIVE_HUMIDITY);
            put("TEMPERATURE",Sensor.TYPE_TEMPERATURE);

            // Other Sensors
            put("MOTION_DETECT",Sensor.TYPE_MOTION_DETECT);
            put("STATIONARY_DETECT",Sensor.TYPE_STATIONARY_DETECT);
            put("LOW_LATENCY_OFFBODY_DETECT",Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT);
            put("TYPE_POSE_6DOF",Sensor.TYPE_POSE_6DOF);
            put("DEVICE_PRIVATE_BASE",Sensor.TYPE_DEVICE_PRIVATE_BASE);
            put("HEART_BEAT",Sensor.TYPE_HEART_BEAT);
            put("HEART_RATE",Sensor.TYPE_HEART_RATE);
        }
    };

    private SensorLoggerListener listener;

    interface SensorLoggerListener {
        void onSensorValueReceived(final SensorValues values);
    }

    private SensorManager mySensorManager;
    private int[] mySensorSeries = new int[Max_Size];
    private int[] availableSensorList = new int[Max_Size];
    private Context myContext;
    private SensorValues mySensorValues = new SensorValues();
    private Handler myHandler;
    private Runnable myRunnable;
    public boolean isLogging = false;

    public  SensorLogger(Context ctx)
    {
        myContext = ctx;
        listener = (SensorLoggerListener) ctx;
        mySensorManager = (SensorManager) myContext.getSystemService(Context.SENSOR_SERVICE);
        checkSensors();
    }

    private void checkSensors()
    {
        int count = 0;
        for (Integer val: sensorListMap.values())
        {
            Sensor sensor = mySensorManager.getDefaultSensor(val);
            if(sensor !=null)
            {
                availableSensorList[count] = 1;
            }
            else
            {
                availableSensorList[count] = 0;
            }
            count++;
        }
    }

    public int[] getSensorList()
    {
        return availableSensorList;
    }

    public final SensorValues getSensorValues() throws CloneNotSupportedException
    {
        return mySensorValues.clone();
    }

    public void startLog(int[] sensorSeries, final int samplingTime)
    {
        System.arraycopy(sensorSeries, 0, mySensorSeries, 0, Max_Size);
        for(int i=0; i<Max_Size; i++)
        {
            if(availableSensorList[i] == 0)
            {
                mySensorSeries[i] = 0;
            }
        }

        int cKey = 0;
        for(Integer val : sensorListMap.values())
        {
            if(mySensorSeries[cKey]==1)
            {
                Sensor sensor = mySensorManager.getDefaultSensor(val);
                mySensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
            cKey++;
        }

        myHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                if(listener != null)
                {
                    isLogging = true;
                    try {
                        listener.onSensorValueReceived(mySensorValues.clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                myHandler.postDelayed(this, (long) samplingTime);
            }
        };
        myHandler.postDelayed(myRunnable, (long) samplingTime);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        for(Map.Entry<String, Integer> entry : sensorListMap.entrySet()) {
            if(sensorEvent.sensor.getType() == entry.getValue())
            {
                for (int i=0;i<mySensorValues.map.get(entry.getKey()).size();i++)
                {
                    mySensorValues.map.get(entry.getKey()).set(i, sensorEvent.values[i]);
                }
                break;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void stopLog()
    {
        isLogging = false;
        myHandler.removeCallbacks(myRunnable);
        myRunnable = null;
        myHandler = null;
        mySensorManager.unregisterListener(this);
    }
}

class SensorValues implements Cloneable {
    //"ACCELEROMETER"
    //float acc_x, acc_y, acc_z;
    List<Float> accList = new ArrayList<Float>(Arrays.asList(0f,0f,0f));

    //"ACCELEROMETER_UNCALIBRATED"
    //float accU_x, accU_y, accU_z, accUB_x, accUB_y, accUB_z;
    List<Float> accUList = new ArrayList<Float>(Arrays.asList(0f,0f,0f,0f,0f,0f));

    //"GRAVITY"
    //float grv_x, grv_y, grv_z;
    List<Float> grvList = new ArrayList<Float>(Arrays.asList(0f,0f,0f));

    //"GYROSCOPE"
    //float gyr_x, gyr_y, gyr_z;
    List<Float> gyrList = new ArrayList<Float>(Arrays.asList(0f,0f,0f));

    //"GYROSCOPE_UNCALIBRATED"
    //float gyrU_x, gyrU_y, gyrU_z, gyrUD_x, gyrUD_y, gyrUD_z;
    List<Float> gyrUList = new ArrayList<Float>(Arrays.asList(0f,0f,0f,0f,0f,0f));

    //"LINEAR_ACCELERATION"
    //float accL_x, accL_y, accL_z;
    List<Float> accLList = new ArrayList<Float>(Arrays.asList(0f,0f,0f));

    //"ROTATION_VECTOR"
    //float vec_x, vec_y, vec_z, vec_scl;
    List<Float> vecList = new ArrayList<Float>(Arrays.asList(0f,0f,0f,0f));

    //"SIGNIFICANT_MOTION"
    // No Values
    List<Float> motList = new ArrayList<Float>(0);

    //"STEP_COUNTER"
    //float count_step;
    List<Float> cstepList = new ArrayList<Float>(Arrays.asList(0f));

    //"STEP_DETECTOR"
    // No Values
    List<Float> dstepList = new ArrayList<Float>(0);

    //"GAME_ROTATION_VECTOR"
    //float vecRGame_x, vecRGame_y, vecRGame_z;
    List<Float> vecRGameList = new ArrayList<Float>(Arrays.asList(0f,0f,0f));

    //"GEOMAGNETIC_ROTATION_VECTOR"
    //float vecRGeo_x, vecRGeo_y, vecRGeo_z;
    List<Float> vecRGeoList = new ArrayList<Float>(Arrays.asList(0f,0f,0f));

    //"MAGNETIC_FIELD"
    //float mag_x, mag_y, mag_z;
    List<Float> magList = new ArrayList<Float>(Arrays.asList(0f,0f,0f));

    //"MAGNETIC_FIELD_UNCALIBRATED"
    //float magU_x, magU_y, magU_z, magUB_x, magUB_y, magUB_z;
    List<Float> magUList = new ArrayList<Float>(Arrays.asList(0f,0f,0f,0f,0f,0f));

    //"ORIENTATION"
    //float ori_z, ori_x, ori_y;
    List<Float> oriList = new ArrayList<Float>(Arrays.asList(0f,0f,0f));

    //"PROXIMITY"
    //float prx;
    List<Float> prxList = new ArrayList<Float>(Arrays.asList(0f));

    //"AMBIENT_TEMPERATURE"
    //float amb_temp;
    List<Float> atempList = new ArrayList<Float>(Arrays.asList(0f));

    //"LIGHT"
    //float light;
    List<Float> lightList = new ArrayList<Float>(Arrays.asList(0f));

    //"PRESSURE"
    //float press;
    List<Float> pressList = new ArrayList<Float>(Arrays.asList(0f));

    //"RELATIVE_HUMIDITY"
    //float hum;
    List<Float> humList = new ArrayList<Float>(Arrays.asList(0f));

    //"TEMPERATURE"
    //float temp;
    List<Float> tempList = new ArrayList<Float>(Arrays.asList(0f));

    //"MOTION_DETECT"
    //float motion_dtc;
    List<Float> dmotList = new ArrayList<Float>(Arrays.asList(0f));

    //"STATIONARY_DETECT"
    //float station_dtc;
    List<Float> dstaList = new ArrayList<Float>(Arrays.asList(0f));

    //"LOW_LATENCY_OFFBODY_DETECT"
    //float offbody_dtc;
    List<Float> doffList = new ArrayList<Float>(Arrays.asList(0f));

    //"TYPE_POSE_6DOF"
    //float pose_x, pose_y, pose_z;
    //float pose_trsx, pose_trsy, pose_trsz;
    //float pose_quaterx, pose_quatery, pose_quaterz;
    //float pose_dlttrsx, pose_dlttrsy, pose_dlttrsz;
    //float num_seq;
    List<Float> poseList = new ArrayList<Float>(Arrays.asList(0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f));

    //"DEVICE_PRIVATE_BASE"
    // No Values
    List<Float> baseList = new ArrayList<Float>(0);

    //"HEART_BEAT"
    //float heart_B;
    List<Float> heartBList = new ArrayList<Float>(Arrays.asList(0f));

    //"HEART_RATE"
    //float heart_R;
    List<Float> heartRList = new ArrayList<Float>(Arrays.asList(0f));

    public LinkedHashMap<String, List<Float>> map = new LinkedHashMap<String, List<Float>>()
    {
        {
            // Motion Sensors
            put("ACCELEROMETER", accList);
            put("ACCELEROMETER_UNCALIBRATED", accUList);
            put("GRAVITY", grvList);
            put("GYROSCOPE", gyrList);
            put("GYROSCOPE_UNCALIBRATED", gyrUList);
            put("LINEAR_ACCELERATION",accLList);
            put("ROTATION_VECTOR",vecList);
            put("SIGNIFICANT_MOTION",motList);
            put("STEP_COUNTER",cstepList);
            put("STEP_DETECTOR",dstepList);

            // Position Sensors
            put("GAME_ROTATION_VECTOR",vecRGameList);
            put("GEOMAGNETIC_ROTATION_VECTOR",vecRGeoList);
            put("MAGNETIC_FIELD",magList);
            put("MAGNETIC_FIELD_UNCALIBRATED",magUList);
            put("ORIENTATION",oriList);
            put("PROXIMITY",prxList);

            // Environment Sensors
            put("AMBIENT_TEMPERATURE",atempList);
            put("LIGHT",lightList);
            put("PRESSURE",pressList);
            put("RELATIVE_HUMIDITY",humList);
            put("TEMPERATURE",tempList);

            // Other Sensors
            put("MOTION_DETECT",dmotList);
            put("STATIONARY_DETECT",dstaList);
            put("LOW_LATENCY_OFFBODY_DETECT",doffList);
            put("TYPE_POSE_6DOF",poseList);
            put("DEVICE_PRIVATE_BASE",baseList);
            put("HEART_BEAT",heartBList);
            put("HEART_RATE",heartRList);
        }
    };

    @Override
    public SensorValues clone() throws CloneNotSupportedException {
        SensorValues clone = (SensorValues) super.clone();
        return clone;
    }
}

class SensorValues2CSV {
    public static String publishHeader(SensorValues mSensorValues, int[] mSensorSeries){
        String header="";

        header+="time";
        int cKey = 0;
        for(Map.Entry<String, List<Float>> entry : mSensorValues.map.entrySet())
        {
            if(mSensorSeries[cKey]==1)
            {
                header+=",";
                if(entry.getValue().size()==0)
                {
                    header+=entry.getKey()+"_0";
                }
                else
                {
                    for (int i = 0; i < entry.getValue().size(); i++)
                    {
                        header+=entry.getKey()+"_"+i;
                        if(i+1!=entry.getValue().size())
                        {
                            header+=",";
                        }
                    }
                }
            }
            cKey++;
        }
        return header;
    }

    public static String publishValuesLine(SensorValues mSensorValues, int[] mSensorSeries){
        String contents="";

        contents+=System.currentTimeMillis();
        int cKey = 0;
        for(Map.Entry<String, List<Float>> entry : mSensorValues.map.entrySet())
        {
            if(mSensorSeries[cKey]==1)
            {
                contents+=",";
                if(entry.getValue().size()==0)
                {
                    contents+="None";
                }
                else
                {
                    for (int i = 0; i < entry.getValue().size(); i++)
                    {
                        contents+=entry.getValue().get(i);
                        if(i+1!=entry.getValue().size())
                        {
                            contents+=",";
                        }
                    }
                }
            }
            cKey++;
        }
        return contents;
    }
}
