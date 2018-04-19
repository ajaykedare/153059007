package in.ac.iitb.cse.a153059007;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AccelerometerService extends Service implements SensorEventListener {

    public static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 99;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ArrayList sensorData;

    private LocationListener listener;
    private LocationManager locationManager;
    String longitude="-",latitude="-",accx="-",accy="-",accz="-",label="stationary",userInfo="-";

    File path;
    String currentDateTimeString;
    File file;
    FileOutputStream stream = null;
    OutputStreamWriter myOutWriter = null;


    public AccelerometerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
//        Toast.makeText(this, "Accelerometer Service Created", Toast.LENGTH_SHORT).show();
        Log.d(AccelerometerService.class.getSimpleName(), "Accelerometer Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, " Accelerometer Service Started", Toast.LENGTH_LONG).show();
        userInfo = getUserInfo();
        label = SensorOptions.label;

        if(SensorOptions.isAccel &&  !SensorOptions.isGPS) {
            registerAccelerometer();
        } else if(!SensorOptions.isAccel &&  SensorOptions.isGPS){
            registerGps();
        } else if(SensorOptions.isAccel &&  SensorOptions.isGPS){
            registerAccelerometer();
            registerGps();
        } else {
            Log.d(AccelerometerService.class.getName(), "No Sensor checked, Please select any");
            Toast.makeText(this, "No Sensor checked, Please select any Seonsor First !",Toast.LENGTH_SHORT).show();
        }


        path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy:hh:mm:ss");
        Date date = new Date();
        currentDateTimeString = dateFormat.format(date);

        file = new File(path, currentDateTimeString+".csv");
        try{
            stream = new FileOutputStream(file,true);
            myOutWriter = new OutputStreamWriter(stream);
        }catch(Exception e){
            Log.d("line no 76:",e.toString());
        }
        try{
            myOutWriter.append(userInfo);
        }catch(Exception e){
            Log.d("line no 81:",e.toString());
        }
        return START_STICKY;

    }

    private void stopService() {
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.v("inside sensor service","onSensorChanged working");
        //Log.d("sensorv",String.valueOf(sensorEvent.values[0])+"  "+String.valueOf(sensorEvent.values[1])+" "+String.valueOf(sensorEvent.values[2]));
        accx = String.valueOf(event.values[0]);
        accy = String.valueOf(event.values[1]);
        accz = String.valueOf(event.values[2]);
        writeCSVFile();


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void registerAccelerometer(){

        Log.v(AccelerometerService.class.getSimpleName(),"Inside registerAccelerometer: Registering Acc service");
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void registerGps(){
        Log.d(AccelerometerService.class.getName(), "Registering GPS");



        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Intent i = new Intent("location_update");
                Log.d("coordinates",location.getLongitude()+" "+location.getLatitude());
                Log.d(AccelerometerService.class.getName(), "OnLocationChanged happened");
                longitude = String.valueOf(location.getLongitude());
                latitude = String.valueOf(location.getLatitude());
                writeCSVFile();
                //sendBroadcast(i);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                //Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(i);
            }
        };
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        //noinspection MissingPermission
        //permission end
        try {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("TRY BLOCK :", "location request permissions GRANTED");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 0, listener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 0, listener);
            }

        } catch (SecurityException e){

            Log.d("CATCH BLOCK :", "location request permissions not found");
            e.printStackTrace();
        }
    }



    private void writeCSVFile(){

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy:hh:mm:ss");
        Date date = new Date();
        currentDateTimeString = dateFormat.format(date);

        try {
            String temp =  currentDateTimeString+","+latitude+","+longitude+","+accx+","+accy+","+accz+","+SensorOptions.label+"\n";
            myOutWriter.append(temp);
        } catch(Exception e){
            Log.d("line no 149: ",e.toString());
        }

    }

    private String getUserInfo(){
        return User.getFirstname()+","+User.getLastname()+","+User.getMobile()+","
                +User.getEmail()+","+User.getGender()+","+User.getAge()+"\n";
    }

    @Override
    public void onDestroy() {
        Log.v("inside sensor service","onDestroy method");
        if(sensorManager != null)
            sensorManager.unregisterListener(this, accelerometer);
        //GPS START
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
        //GPS END
        try{
            myOutWriter.close();
            stream.close();
        }catch (Exception e){

        }
    }


}
