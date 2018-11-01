package com.lafferty.jai.trackie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

public class UserPrefsFragment extends Fragment {

    private TextView tvUPHeight;
    private EditText etUPName;
    private EditText etUPAge;
    private EditText etUPHeight;
    private Spinner genderSpinner;
    private Button btUPSave;
    private String selectedGender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_prefs, container, false);
    }

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        tvUPHeight = view.findViewById(R.id.tvUPHeight);
        tvUPHeight.setText(String.format(Locale.ENGLISH,getActivity().getText(R.string.height).toString(), PreferenceManager.get_heightUnit()));
        genderSpinner = view.findViewById(R.id.GenderSpinner);
        etUPName = view.findViewById(R.id.etUPName);
        etUPAge = view.findViewById(R.id.etUPAge);
        etUPHeight = view.findViewById(R.id.etUPHeight);
        btUPSave = view.findViewById(R.id.btUPSave);

        btUPSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                SaveData();
                Intent i = new Intent(getActivity(), HomeActivity.class);
                startActivity(i);
            }
        });
        InitSpinner();
        LoadData();

    }

    public void InitSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.genders,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        //kgs for is metric, imperial for is NOT metric
        int selection = PreferenceManager.is_metric() ? 0 : 1;
        genderSpinner.setSelection(selection);

        genderSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position){

                            //No selection
                            case 0:
                                selectedGender = "None";
                                break;
                            //Male
                            case 1:
                                selectedGender = "Male";
                                break;
                            //Female
                            case 2:
                                selectedGender = "Female";
                                break;
                            //Other
                            case 3:
                                selectedGender = "Other";
                                break;
                        }
                        PreferenceManager.commitChanges();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
    }

    public void LoadData(){
        etUPName.setText(PreferenceManager.get_name());
        etUPAge.setText(String.valueOf(PreferenceManager.get_age()));

        int height = PreferenceManager.get_height();
        if(!PreferenceManager.is_metric()){height = (int)Math.floor(Calc.CMToInch(height));}
        etUPHeight.setText(String.valueOf(height));

        switch(PreferenceManager.get_gender()){
            case "Male":
                genderSpinner.setSelection(1);
                break;
            case "Female":
                genderSpinner.setSelection(2);
                break;
            case "Other":
                genderSpinner.setSelection(3);
                break;
            default:
                genderSpinner.setSelection(0);
                break;
        }
    }

    public void SaveData(){
        String strName;
        String strAge;
        String strHeight;

        strName = etUPName.getText().toString();
        strAge = etUPAge.getText().toString();
        strHeight = etUPHeight.getText().toString();

        int height = Integer.parseInt(strHeight);
        if(!PreferenceManager.is_metric()){height = (int)Math.floor(Calc.InchToCM(height));}

        PreferenceManager.set_name(strName);
        PreferenceManager.set_age(Integer.parseInt(strAge));
        //TODO: Height Conversion
        PreferenceManager.set_height(height);
        PreferenceManager.set_gender(selectedGender);
        PreferenceManager.commitChanges();
    }

}
