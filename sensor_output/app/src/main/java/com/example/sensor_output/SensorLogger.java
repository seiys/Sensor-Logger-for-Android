package com.example.sensor_output;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.os.Handler;
import android.util.Log;

import java.util.LinkedHashMap;

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
    private boolean flagCsv;
    private SensorValues mySensorValues = new SensorValues();
    private Handler myHandler;
    private Runnable myRunnable;

    public  SensorLogger(Context ctx, boolean flag)
    {
        myContext = ctx;
        flagCsv = flag;
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
        }

        myHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                if(listener != null)
                {
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
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == sensorListMap.get("ACCELEROMETER"))
        {
            mySensorValues.acc_x = sensorEvent.values[0];
            mySensorValues.acc_y = sensorEvent.values[1];
            mySensorValues.acc_z = sensorEvent.values[2];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("ACCELEROMETER_UNCALIBRATED"))
        {
            mySensorValues.accU_x = sensorEvent.values[0];
            mySensorValues.accU_y = sensorEvent.values[1];
            mySensorValues.accU_z = sensorEvent.values[2];
            mySensorValues.accUB_x = sensorEvent.values[3];
            mySensorValues.accUB_y = sensorEvent.values[4];
            mySensorValues.accUB_z = sensorEvent.values[5];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("GRAVITY"))
        {
            mySensorValues.grv_x = sensorEvent.values[0];
            mySensorValues.grv_y = sensorEvent.values[1];
            mySensorValues.grv_z = sensorEvent.values[2];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("GYROSCOPE"))
        {
            mySensorValues.gyr_x = sensorEvent.values[0];
            mySensorValues.gyr_y = sensorEvent.values[1];
            mySensorValues.gyr_z = sensorEvent.values[2];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("GYROSCOPE_UNCALIBRATED"))
        {
            mySensorValues.gyrU_x = sensorEvent.values[0];
            mySensorValues.gyrU_y = sensorEvent.values[1];
            mySensorValues.gyrU_z = sensorEvent.values[2];
            mySensorValues.gyrUD_x = sensorEvent.values[3];
            mySensorValues.gyrUD_y = sensorEvent.values[4];
            mySensorValues.gyrUD_z = sensorEvent.values[5];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("LINEAR_ACCELERATION"))
        {
            mySensorValues.accL_x = sensorEvent.values[0];
            mySensorValues.accL_y = sensorEvent.values[1];
            mySensorValues.accL_z = sensorEvent.values[2];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("ROTATION_VECTOR"))
        {
            mySensorValues.vec_x = sensorEvent.values[0];
            mySensorValues.vec_y = sensorEvent.values[1];
            mySensorValues.vec_z = sensorEvent.values[2];
            mySensorValues.vec_scl = sensorEvent.values[4];
        }

//        if(sensorEvent.sensor.getType() == sensorListMap.get("SIGNIFICANT_MOTION"))
//        {}

        if(sensorEvent.sensor.getType() == sensorListMap.get("STEP_COUNTER"))
        {
            mySensorValues.count_step = sensorEvent.values[0];
        }

//        if(sensorEvent.sensor.getType() == sensorListMap.get("STEP_DETECTOR"))
//        {}

        if(sensorEvent.sensor.getType() == sensorListMap.get("GAME_ROTATION_VECTOR"))
        {
            mySensorValues.vecRGame_x = sensorEvent.values[0];
            mySensorValues.vecRGame_y = sensorEvent.values[1];
            mySensorValues.vecRGame_z = sensorEvent.values[2];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("GEOMAGNETIC_ROTATION_VECTOR"))
        {
            mySensorValues.vecRGeo_x = sensorEvent.values[0];
            mySensorValues.vecRGeo_y = sensorEvent.values[1];
            mySensorValues.vecRGeo_z = sensorEvent.values[2];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("MAGNETIC_FIELD"))
        {
            mySensorValues.mag_x = sensorEvent.values[0];
            mySensorValues.mag_y = sensorEvent.values[1];
            mySensorValues.mag_z = sensorEvent.values[2];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("MAGNETIC_FIELD_UNCALIBRATED"))
        {
            mySensorValues.magU_x = sensorEvent.values[0];
            mySensorValues.magU_y = sensorEvent.values[1];
            mySensorValues.magU_z = sensorEvent.values[2];
            mySensorValues.magUB_x = sensorEvent.values[3];
            mySensorValues.magUB_y = sensorEvent.values[4];
            mySensorValues.magUB_z = sensorEvent.values[5];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("ORIENTATION"))
        {
            mySensorValues.ori_z = sensorEvent.values[0];
            mySensorValues.ori_x = sensorEvent.values[1];
            mySensorValues.ori_y = sensorEvent.values[2];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("PROXIMITY"))
        {
            mySensorValues.prx = sensorEvent.values[0];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("AMBIENT_TEMPERATURE"))
        {
            mySensorValues.amb_temp = sensorEvent.values[0];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("LIGHT"))
        {
            mySensorValues.light = sensorEvent.values[0];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("PRESSURE"))
        {
            mySensorValues.press = sensorEvent.values[0];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("RELATIVE_HUMIDITY"))
        {
            mySensorValues.hum = sensorEvent.values[0];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("TEMPERATURE"))
        {
            mySensorValues.temp = sensorEvent.values[0];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("MOTION_DETECT"))
        {
            mySensorValues.motion_dtc = sensorEvent.values[0];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("STATIONARY_DETECT"))
        {
            mySensorValues.station_dtc = sensorEvent.values[0];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("LOW_LATENCY_OFFBODY_DETECT"))
        {
            mySensorValues.offbody_dtc = sensorEvent.values[0];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("TYPE_POSE_6DOF"))
        {
            mySensorValues.pose_x = sensorEvent.values[0];
            mySensorValues.pose_y = sensorEvent.values[1];
            mySensorValues.pose_z = sensorEvent.values[2];
            mySensorValues.pose_trsx = sensorEvent.values[3];
            mySensorValues.pose_trsy = sensorEvent.values[4];
            mySensorValues.pose_trsz = sensorEvent.values[5];
            mySensorValues.pose_quaterx = sensorEvent.values[6];
            mySensorValues.pose_quatery = sensorEvent.values[7];
            mySensorValues.pose_quaterz = sensorEvent.values[8];
            mySensorValues.pose_dlttrsx = sensorEvent.values[9];
            mySensorValues.pose_dlttrsy = sensorEvent.values[10];
            mySensorValues.pose_dlttrsz = sensorEvent.values[11];
            mySensorValues.num_seq = sensorEvent.values[12];
        }

//        if(sensorEvent.sensor.getType() == sensorListMap.get("DEVICE_PRIVATE_BASE"))
//        {}

        if(sensorEvent.sensor.getType() == sensorListMap.get("HEART_BEAT"))
        {
            mySensorValues.heart_B = sensorEvent.values[0];
        }

        if(sensorEvent.sensor.getType() == sensorListMap.get("HEART_RATE"))
        {
            mySensorValues.heart_R = sensorEvent.values[0];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void stopLog()
    {
        myHandler.removeCallbacks(myRunnable);
        myRunnable = null;
        myHandler = null;
        mySensorManager.unregisterListener(this);
    }
}

class SensorValues implements Cloneable {
    //"ACCELEROMETER"
    float acc_x, acc_y, acc_z;

    //"ACCELEROMETER_UNCALIBRATED"
    float accU_x, accU_y, accU_z, accUB_x, accUB_y, accUB_z;

    //"GRAVITY"
    float grv_x, grv_y, grv_z;

    //"GYROSCOPE"
    float gyr_x, gyr_y, gyr_z;

    //"GYROSCOPE_UNCALIBRATED"
    float gyrU_x, gyrU_y, gyrU_z, gyrUD_x, gyrUD_y, gyrUD_z;

    //"LINEAR_ACCELERATION"
    float accL_x, accL_y, accL_z;

    //"ROTATION_VECTOR"
    float vec_x, vec_y, vec_z, vec_scl;

    //"SIGNIFICANT_MOTION"
    // No Values

    //"STEP_COUNTER"
    float count_step;

    //"STEP_DETECTOR"
    // No Values

    //"GAME_ROTATION_VECTOR"
    float vecRGame_x, vecRGame_y, vecRGame_z;

    //"GEOMAGNETIC_ROTATION_VECTOR"
    float vecRGeo_x, vecRGeo_y, vecRGeo_z;

    //"MAGNETIC_FIELD"
    float mag_x, mag_y, mag_z;

    //"MAGNETIC_FIELD_UNCALIBRATED"
    float magU_x, magU_y, magU_z, magUB_x, magUB_y, magUB_z;

    //"ORIENTATION"
    float ori_z, ori_x, ori_y;

    //"PROXIMITY"
    float prx;

    //"AMBIENT_TEMPERATURE"
    float amb_temp;

    //"LIGHT"
    float light;

    //"PRESSURE"
    float press;

    //"RELATIVE_HUMIDITY"
    float hum;

    //"TEMPERATURE"
    float temp;

    //"MOTION_DETECT"
    float motion_dtc;

    //"STATIONARY_DETECT"
    float station_dtc;

    //"LOW_LATENCY_OFFBODY_DETECT"
    float offbody_dtc;

    //"TYPE_POSE_6DOF"
    float pose_x, pose_y, pose_z;
    float pose_trsx, pose_trsy, pose_trsz;
    float pose_quaterx, pose_quatery, pose_quaterz;
    float pose_dlttrsx, pose_dlttrsy, pose_dlttrsz;
    float num_seq;

    //"DEVICE_PRIVATE_BASE"
    // No Values

    //"HEART_BEAT"
    float heart_B;

    //"HEART_RATE"
    float heart_R;

    @Override
    public SensorValues clone() throws CloneNotSupportedException {
        SensorValues clone = (SensorValues) super.clone();
        return clone;
    }
}
