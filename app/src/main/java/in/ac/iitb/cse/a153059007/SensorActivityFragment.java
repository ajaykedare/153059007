package in.ac.iitb.cse.a153059007;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

/**
 * Created by ajay on 16/2/18.
 */

public class SensorActivityFragment extends Fragment{

    public SensorActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sensors, container, false);

        CheckBox accelCheckbox = (CheckBox) view.findViewById(R.id.accelCheckbox);
        CheckBox gpsCheckbox = (CheckBox) view.findViewById(R.id.gpsCheckbox);


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
            // TODO: Veggie sandwich
        }
    }
}
