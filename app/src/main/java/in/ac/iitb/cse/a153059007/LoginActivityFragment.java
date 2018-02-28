package in.ac.iitb.cse.a153059007;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment{

    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mAgeView;
    private AutoCompleteTextView mMobileNoView;
    private AutoCompleteTextView mFirstnameView;
    private AutoCompleteTextView mLastnameView;
    private RadioGroup mGenderView;

    public LoginActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.email);
        mFirstnameView = (AutoCompleteTextView) view.findViewById(R.id.firstname);
        mLastnameView= (AutoCompleteTextView) view.findViewById(R.id.lastname);
        mMobileNoView = (AutoCompleteTextView) view.findViewById(R.id.phone);
        mAgeView = (AutoCompleteTextView) view.findViewById(R.id.age);
        mGenderView = (RadioGroup) view.findViewById(R.id.gender_radio_group);
        Button mEmailSignInButton = (Button) view.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInClickFunction();
            }
        });

        return view;
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.male_radio_btn:
                if (checked)
                    User.gender = "male";
                    break;
            case R.id.female_radio_btn:
                if (checked)
                    User.gender = "female";
                    break;
        }
    }

    public void signInClickFunction()
    {

        String email = mEmailView.getText().toString();
        String firstname =  mFirstnameView.getText().toString();
        String lastname=  mLastnameView.getText().toString();
        String mobile=  mMobileNoView.getText().toString();
        String age =  mAgeView.getText().toString();

        mEmailView.setError(null);
        mFirstnameView.setError(null);
        mLastnameView.setError(null);
        mMobileNoView.setError(null);
        mAgeView.setError(null);

        boolean isAllValid = true;
        View focusView = null;

        //Email
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            isAllValid = false;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            isAllValid = false;
        }

        //Firstname
        if (TextUtils.isEmpty(firstname)) {
            mFirstnameView.setError(getString(R.string.error_field_required));
            focusView = mFirstnameView;
            isAllValid = false;
        }

        //LastName
        if (TextUtils.isEmpty(lastname)) {
            mLastnameView.setError(getString(R.string.error_field_required));
            focusView = mLastnameView;
            isAllValid = false;
        }

        //Mobile
        if (TextUtils.isEmpty(mobile)) {
            mMobileNoView.setError(getString(R.string.error_field_required));
            focusView = mMobileNoView;
            isAllValid = false;
        } else if (!isMobileValid(mobile)) {
            mMobileNoView.setError(getString(R.string.error_invalid_mobile));
            focusView = mMobileNoView;
            isAllValid = false;
        }

        //Age
        if (TextUtils.isEmpty(age)) {
            mAgeView.setError(getString(R.string.error_field_required));
            focusView = mAgeView;
            isAllValid = false;
        }


        if (!isAllValid) {
            focusView.requestFocus();
        } else {

            RadioButton rb = (RadioButton) mGenderView.findViewById(mGenderView.getCheckedRadioButtonId());
            String txt = rb.getText().toString();
            Toast.makeText(getContext(),"Success!", Toast.LENGTH_SHORT).show();
            //All fields are valid

            User.firstname = firstname;
            User.lastname = lastname;
            User.email = email;
            User.mobile = mobile;
            User.age = age;
            User.gender = txt;
            User.isComplete = true;
//            Intent intent = new Intent(getApplicationContext(), SensorsActivity.class);
//            startActivity(intent);
        }

    }

    boolean isEmailValid(String e){
        return e.contains("@");
    }

    boolean isMobileValid(String m){
        return m.length()==10;
    }

}
