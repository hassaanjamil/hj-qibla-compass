package com.hassanjamil.hqibla;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

public class Compass implements SensorEventListener {
    private static final String TAG = Compass.class.getSimpleName();

    public interface CompassListener {
        void onNewAzimuth(float azimuth);
    }

    private CompassListener listener;

    private SensorManager sensorManager;
    private Sensor asensor;
    private Sensor msensor;

    private float[] aData = new float[3];
    private float[] mData = new float[3];
    private float[] Rotation = new float[9];
    private float[] Identity = new float[9];

    private float azimuthFix;

    public Compass(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void start(Context context) {
        sensorManager.registerListener(this, asensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);

        PackageManager manager = context.getPackageManager();
        boolean haveAS = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        boolean haveCS = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);

        if(!haveAS || !haveCS) {
            sensorManager.unregisterListener(this, asensor);
            sensorManager.unregisterListener(this, msensor);
            Log.e(TAG, "Device don't have enough sensors");

            dialogError(context);
        }
    }

    private void dialogError(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.dialog_title));
        builder.setCancelable(false);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(context.getString(R.string.dialog_message_sensor_not_exist));
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (context instanceof Activity)
                    ((Activity) context).finish();
            }
        });
        builder.create().show();
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void setAzimuthFix(float fix) {
        azimuthFix = fix;
    }

    public void resetAzimuthFix() {
        setAzimuthFix(0);
    }

    public void setListener(CompassListener l) {
        listener = l;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;
        //boolean hasAS = false, hasMS = false;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                aData[0] = alpha * aData[0] + (1 - alpha)
                        * event.values[0];
                aData[1] = alpha * aData[1] + (1 - alpha)
                        * event.values[1];
                aData[2] = alpha * aData[2] + (1 - alpha)
                        * event.values[2];
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mData[0] = alpha * mData[0] + (1 - alpha)
                        * event.values[0];
                mData[1] = alpha * mData[1] + (1 - alpha)
                        * event.values[1];
                mData[2] = alpha * mData[2] + (1 - alpha)
                        * event.values[2];
            }

            boolean success = SensorManager.getRotationMatrix(Rotation, Identity, aData, mData);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(Rotation, orientation);
                float azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + azimuthFix + 360) % 360;
                //azimuth = (azimuth + azimuthFix + 360) % 294;

                // Log.d(TAG, "azimuth (deg): " + azimuth);
                if (listener != null) {
                    listener.onNewAzimuth(azimuth);
                }
            }

            /*if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, aData, 0, 3);
                hasAS = true;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, mData, 0, 3);
                hasMS = true;
            }

            if (hasAS && hasMS) {
                float[] Identity = new float[9];
                float[] Rotation = new float[9];
                boolean success = SensorManager.getRotationMatrix(Rotation, Identity, aData, mData);

                if (success) {
                    float[] O = new float[3];
                    SensorManager.getOrientation(Rotation, O);
                    float rotationInRadians = O[0];
                    float rotationInDegrees = (float) Math.toDegrees(rotationInRadians);
                    rotationInDegrees = (rotationInDegrees + 0 + 360) % 360;

                    if (listener != null) {
                        listener.onNewAzimuth(rotationInDegrees);
                        //Log.d(TAG, "azimuth (deg): " + azimuth);
                    }
                    // do something with the rotation in degrees
                }
            }*/
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
