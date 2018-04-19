package in.ac.iitb.cse.a153059007;

import umich.cse.yctung.androidlibsvm.LibSVM;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by ajay on 17/4/18.
 */

public class SVMActivityFragment extends Fragment{

    LibSVM svm = new LibSVM();
    public static int PICK_DATAFILE = 2000;
    public static int PICK_TESTFILE = 2001;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Button dataFilePicker;
    Button testFilePicker;
    ProgressDialog progressDialog;

    public static String userinfo;

    TextView datafile_textview;
    TextView testfile_textview;

        @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_svm, container, false);

        dataFilePicker = (Button) view.findViewById(R.id.pickdatafile_button);
        testFilePicker = (Button) view.findViewById(R.id.picktestfile_button);

        datafile_textview = (TextView) view.findViewById(R.id.datafile_textview);
        testfile_textview = (TextView) view.findViewById(R.id.testfile_textview);
        progressDialog = new ProgressDialog(getContext());


        dataFilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(getContext(), FileChooser.class);
                i2.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
                startActivityForResult(i2, PICK_DATAFILE);
            }
        });

        testFilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(getContext(), FileChooser.class);
                i2.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
                startActivityForResult(i2, PICK_TESTFILE);
            }
        });


        Button trainButton = (Button) view.findViewById(R.id.train_button);
        trainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trainButtonClickFunction();
            }
        });

        Button testButton = (Button) view.findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testButtonClickFunction();
            }
        });

        // Check for Storage Permissions
        verifyStoragePermissions(getActivity());


        return view;
    }

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
    }

    public void trainButtonClickFunction(){


        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);

//        File file = new File(path, "data.csv");
//        String systemPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
//        String appFolderPath = systemPath + "libsvm/"; // your datasets folder

        String appFolderPath = path.getAbsolutePath()+"/";



        String dataFile = datafile_textview.getText().toString();

        formatFile(dataFile, "data");

        Log.d("DATA FILE: ", dataFile);
// NOTE the space between option parameters, which is important to
// keep the options consistent to the original LibSVM format
//        svm.scale(appFolderPath + "heart_scale", appFolderPath + "heart_scale_scaled");
        if(dataFile.equals("")){
            Toast.makeText(getContext(),"Please provide DATA file!", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(getContext(),"Training started:"+datafile_textview.getText().toString(), Toast.LENGTH_SHORT).show();
            new AsyncTrainTask().execute(new String[]{"-t 2", appFolderPath + "data", appFolderPath + "model"});
//            svm.train("-t 2 "/* svm kernel */ + appFolderPath + "data " + appFolderPath + "model");
        }

    }

    public void testButtonClickFunction(){
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        String appFolderPath = path.getAbsolutePath()+"/";


        String testFile = testfile_textview.getText().toString();

        Log.v("TEST FILE: ", testFile);

        formatFile(testFile, "test");

        if(testFile.equals("")){
            Toast.makeText(getContext(),"Please provide test file!", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(getContext(),"Prediction started:"+testfile_textview.getText().toString(), Toast.LENGTH_SHORT).show();
            new AsyncTestTask().execute(new String[]{appFolderPath + "test", appFolderPath + "model", appFolderPath + "result"});
//            svm.predict(appFolderPath + "test " + appFolderPath + "model " + appFolderPath + "result");
        }

            try {
                Thread.sleep(3000);
            } catch (Exception e){
            e.printStackTrace();
            }

            generateOutputFile(appFolderPath+"result", testFile,"output");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DATAFILE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                Uri file = data.getData();
//                dataFilePicker.setText(file.getPath());
                datafile_textview.setText(file.getPath());
            }
        }

        if (requestCode == PICK_TESTFILE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                Uri file = data.getData();
//                testFilePicker.setText(file.getPath());
                testfile_textview.setText(file.getPath());
            }
        }
    }


    public void generateOutputFile(String resultFilePath, String testFilePath, String outputFileName){

        FileOutputStream stream = null;
        OutputStreamWriter myOutWriter = null;

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, outputFileName);

        try {
            stream = new FileOutputStream(file,false);
        }catch(FileNotFoundException e){
            Log.d(SVMActivityFragment.class.getSimpleName(),e.toString());

        }
        myOutWriter = new OutputStreamWriter(stream);


        ArrayList<String> linesResult = new ArrayList<String>();
        ArrayList<String> linesTest = new ArrayList<String>();

        // Read Test File
        try {

            BufferedReader reader = new BufferedReader(new FileReader(new File(testFilePath)));
            String line = null;
            //Skip first line
            userinfo = reader.readLine();

            while ((line = reader.readLine()) != null) {
                linesTest.add(line);
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        //Read Result File
        try {

            BufferedReader reader = new BufferedReader(new FileReader(new File(resultFilePath)));
            String line = null;
            linesResult = new ArrayList<String>();
            while ((line = reader.readLine()) != null) {
                linesResult.add(line);
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        // Write to the output file
        try{
            myOutWriter.append(userinfo+"\n");
            for(int i=0;i<linesTest.size(); i++){
                StringBuffer sb = new StringBuffer();
                sb.append(linesTest.get(i)+ " ");
                sb.append(linesResult.get(i)+ "\n");
                myOutWriter.append(sb.toString());
            }



        }catch(Exception e) {
            Log.d(SVMActivityFragment.class.getSimpleName(), e.toString());
        }
        try{
            myOutWriter.close();
            stream.close();
        }catch (Exception e){

        }
    }

    public void formatFile(String fromFilePath, String toFileName){

        FileOutputStream stream = null;
        OutputStreamWriter myOutWriter = null;

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, toFileName);

        try {
            stream = new FileOutputStream(file,false);
        }catch(FileNotFoundException e){
            Log.d(SVMActivityFragment.class.getSimpleName(),e.toString());

        }
        myOutWriter = new OutputStreamWriter(stream);




        try {

            BufferedReader reader = new BufferedReader(new FileReader(new File(fromFilePath)));
            String line = null;
            //Skip first line
            userinfo = reader.readLine();

            while ((line = reader.readLine()) != null) {
                StringBuffer sb = new StringBuffer();
                String []record = line.split(",");

                if(record[6].equals("stationary")){
                    sb.append(0);
                    sb.append(" ");
                } else {
                    sb.append(1);
                    sb.append(" ");
                }

                sb.append("1:"+record[3]+" ");
                sb.append("2:"+record[4]+" ");
                sb.append("3:"+record[5]+"\n");
                try{
                    myOutWriter.append(sb.toString());
                }catch(Exception e) {
                    Log.d(SVMActivityFragment.class.getSimpleName(), e.toString());
                }
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }


        try{
            myOutWriter.close();
            stream.close();
        }catch (Exception e){

        }
    }


    private class AsyncTestTask extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("SVM Predict");
            progressDialog.setMessage("Executing svm-prediction, please wait...");
            progressDialog.show();
            Log.d(SVMActivityFragment.class.getSimpleName(), "==================\nStart of SVM Predict \n==================");
        }

        @Override
        protected Void doInBackground(String... params) {
            LibSVM.getInstance().predict(TextUtils.join(" ", params));
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "SVM Prediction has executed successfully!", Toast.LENGTH_LONG).show();
            Log.d(SVMActivityFragment.class.getSimpleName(), "==================\nEnd of SVM Predict\n==================");
//            Utility.readLogcat(getContext(), "SVM-Train Results");
        }
    }


    private class AsyncTrainTask extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("SVM Train");
            progressDialog.setMessage("Executing svm-training, please wait...");
            progressDialog.show();
            Log.d(SVMActivityFragment.class.getSimpleName(), "==================\nStart of SVM Train \n==================");
        }

        @Override
        protected Void doInBackground(String... params) {
            LibSVM.getInstance().train(TextUtils.join(" ", params));
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "SVM Train has executed successfully!", Toast.LENGTH_LONG).show();
            Log.d(SVMActivityFragment.class.getSimpleName(), "==================\nEnd of SVM Train\n==================");
//            Utility.readLogcat(getContext(), "SVM-Train Results");
        }
    }


}
