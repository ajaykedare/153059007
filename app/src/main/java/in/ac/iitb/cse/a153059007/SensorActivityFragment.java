package in.ac.iitb.cse.a153059007;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

/**
 * Created by ajay on 16/2/18.
 */

public class SensorActivityFragment extends Fragment{

    CheckBox gpsCheckbox;
    CheckBox accelCheckbox;

    public SensorActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sensors, container, false);

        accelCheckbox = (CheckBox) view.findViewById(R.id.accelCheckbox);
        gpsCheckbox = (CheckBox) view.findViewById(R.id.gpsCheckbox);

        // Check gps permissions
        if(!runtime_permissions())
            enable_checkbox(gpsCheckbox,true);

        // Verify Storage permissions
        verifyStoragePermissions(getActivity());
        accelCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckboxClicked(view);
            }
        });

        gpsCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckboxClicked(view);
            }
        });



        return view;
    }


    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.accelCheckbox:
                if (checked)
                    SensorOptions.isAccel = true;
                else
                        SensorOptions.isAccel = false;
                break;
            case R.id.gpsCheckbox:
                if (checked)
                SensorOptions.isGPS = true;
            else
                SensorOptions.isGPS =  false;
                break;
        }
    }


    private boolean runtime_permissions() {
        Log.d(SensorActivityFragment.class.getSimpleName(), "runtime_permissions is checking");
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_checkbox(gpsCheckbox,true);
            }else {
                enable_checkbox(gpsCheckbox,false);
            }
        }
    }

    private void enable_checkbox(CheckBox cb,boolean doEnable) {
        if(doEnable)
            cb.setEnabled(true);
        else
            cb.setEnabled(false);

    }



    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        //Check permissions

        if ( ContextCompat.checkSelfPermission( activity, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            Log.d(SensorActivityFragment.class.getSimpleName(), "Location access not found. Requesting one");
            ActivityCompat.requestPermissions( activity, new String[] {  Manifest.permission.ACCESS_COARSE_LOCATION  },
                    AccelerometerService.MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }
    }
}
