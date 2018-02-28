package in.ac.iitb.cse.a153059007;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ajay on 16/2/18.
 */

public class RecordActivityFragment extends Fragment implements SensorEventListener {

    ArrayList<TextView> timestampTextViewList = new ArrayList<>();

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ArrayList sensorData;
    private boolean started = false;
    private RecordActivityFragment thisObj = this;

    private LocationManager locationManager;
    private ArrayList gpsData;
    private LocationListener locationListener;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };


    }

    public void makeUseOfNewLocation(Location loc){
        gpsData.add(loc);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_record, container, false);



        //Set on-off switch listner
        Switch onOffSwitch = (Switch)  view.findViewById(R.id.on_off_switch);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a ");
                Date date = new Date();
                String datestr = dateFormat.format(date);
                System.out.println(dateFormat.format(date));

                if(isChecked){
                    started = true;
                    sensorData = new ArrayList();
                    gpsData = new ArrayList();
                    Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    sensorManager.registerListener(thisObj, accel,SensorManager.SENSOR_DELAY_FASTEST);

                    if(locationManager != null) {
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        }
                    }

                    Toast.makeText(getContext(),"Sensor activated", Toast.LENGTH_SHORT).show();

                    if(SensorOptions.isAccel){

                    }

                    if(SensorOptions.isGPS){

                    }

                } else{
                    started = false;
                    sensorManager.unregisterListener(thisObj);
                    // Remove the listener you previously added
                    locationManager.removeUpdates(locationListener);


                    LinearLayout ll = (LinearLayout) view.findViewById(R.id.records_linearlayout);

                    TextView t = (TextView) view.findViewById(R.id.test);
                    t.setText("AccelSwitchStatus:"+SensorOptions.isAccel);



                    //Create a temporary instance which will be added to the list
                    final TextView sampleTextView = new TextView(view.getContext());
                    int cnt = timestampTextViewList.size();
                    cnt++;
                    String ts = "File"+cnt+" :" + datestr;
                    sampleTextView.setText(ts);
                    sampleTextView.setTextSize(20);

                    ///Sample data test
                    TextView user = (TextView) view.findViewById(R.id.user);
                    user.setText("Accel Data:"+sensorData.size()+sensorData.get(0).toString());

                    //Sample GPS data
                    TextView gpsdata = (TextView) view.findViewById(R.id.gpsdata);
                    gpsdata.setText("GPS Data:"+gpsData.size());

                    if(cnt>=6){
                        ll.removeView(timestampTextViewList.get(cnt-6));

                    }
                    timestampTextViewList.add(sampleTextView);
                    ll.addView(sampleTextView);
                    Toast.makeText(getContext(),"New File Saved!", Toast.LENGTH_SHORT).show();
                }

            }

        });
        return view;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (started) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.currentTimeMillis();
            AccelData data = new AccelData(timestamp, x, y, z);
            sensorData.add(data);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
