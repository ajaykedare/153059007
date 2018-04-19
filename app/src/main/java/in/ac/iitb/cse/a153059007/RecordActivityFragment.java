package in.ac.iitb.cse.a153059007;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ajay on 16/2/18.
 */

public class RecordActivityFragment extends Fragment implements OnItemSelectedListener{

    ArrayList<TextView> timestampTextViewList = new ArrayList<>();

    private SensorManager sensorManager;
    private boolean started = false;
    private RecordActivityFragment thisObj = this;




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_record, container, false);


        //Set on-off switch listner
        Switch onOffSwitch = (Switch)  view.findViewById(R.id.on_off_switch);

        //Persist State
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.PREF_FILE),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("label","stationary");
        editor.commit();

        boolean isOn = sharedPreferences.getBoolean("swithStatus",false);


        // Spinner element
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> labelItems = new ArrayList<String>();
        labelItems.add("stationary");
        labelItems.add("walking");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, labelItems);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);



        onOffSwitch.setChecked(isOn);

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy:hh:mm:ss");
                Date date = new Date();
                String datestr = dateFormat.format(date);
                System.out.println(dateFormat.format(date));

                if(isChecked){
                    started = true;
                    Toast.makeText(getContext(),"Sensor activated", Toast.LENGTH_SHORT).show();

                    int cnt = timestampTextViewList.size();
                    cnt++;

                    String fileName = "File"+cnt+".csv";

                    //Persist State
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.PREF_FILE),Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("swithStatus",true);
                    editor.putString("currFileName",fileName);
                    editor.commit();


                    Log.v("Switch ispressed1=", ""+isChecked);
                    Intent serviceIntent = new Intent(getActivity(),AccelerometerService.class);
//                    serviceIntent.putExtra("label",label.getText().toString());

//                    getActivity().startService(serviceIntent);
                    startService();
                    Log.v("Switch ispressed2=", ""+isChecked);



                } else {
                    //Persist State
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.PREF_FILE),Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("swithStatus",false);
                    editor.commit();

                    started = false;
//                    sensorManager.unregisterListener(thisObj);
//                    // Remove the listener you previously added
//                    locationManager.removeUpdates(locationListener);


                    LinearLayout ll = (LinearLayout) view.findViewById(R.id.records_linearlayout);

                    //Create a temporary instance which will be added to the list
                    final TextView sampleTextView = new TextView(view.getContext());
                    int cnt = timestampTextViewList.size();
                    cnt++;
                    String ts = "File"+cnt+" : " + datestr+".csv";
                    sampleTextView.setText(ts);
                    sampleTextView.setTextSize(20);


                    if(cnt>=6){
                        ll.removeView(timestampTextViewList.get(cnt-6));

                    }
                    timestampTextViewList.add(sampleTextView);
                    ll.addView(sampleTextView);
                    Toast.makeText(getContext(),"New File Saved!", Toast.LENGTH_SHORT).show();

                    //getActivity().stopService(new Intent(getActivity(),AccelerometerService.class));
                    stopService();
                }

            }

        });
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
//        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        SensorOptions.label = item;

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    public void startService(){
        Intent intent = new Intent(getActivity(), AccelerometerService.class);
        //Start Service
        getActivity().startService(intent);
    }

    public void stopService(){
        Intent intent = new Intent(getActivity(), AccelerometerService.class);
        //Stop Service
        getActivity().stopService(intent);
    }
}
