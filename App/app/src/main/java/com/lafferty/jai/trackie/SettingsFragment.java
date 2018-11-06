package com.lafferty.jai.trackie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SettingsFragment extends Fragment {

    private Button btUserDetails;
    private Button btWipe;
    private Button btSave;
    private Button btLoadData;
    private Spinner spinner;


    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        spinner = view.findViewById(R.id.UnitSpinner);
        btUserDetails = view.findViewById(R.id.btSetUserPrefs);
        btWipe = view.findViewById(R.id.btWipeData);
        btSave = view.findViewById(R.id.btSaveSettings);
        btLoadData = view.findViewById(R.id.btLoadSampleData);

        btLoadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                SampleData();
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                SaveSettings();
                Intent i = new Intent(getActivity(), HomeActivity.class);
                startActivity(i);
            }
        });

        btUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ((SettingsActivity)getActivity()).createUserPrefs();
            }
        });

        btWipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                wipeAllData();
            }
        });
        InitSpinner();
    }

    public void InitSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.preferred_units,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //kgs for is metric, imperial for is NOT metric
        int selection = PreferenceManager.is_metric() ? 0 : 1;
        spinner.setSelection(selection);

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
    }

    public void SaveSettings(){
        int position = spinner.getSelectedItemPosition();
        switch (position){

            //kgs
            case 0:
                PreferenceManager.set_metric(true);
                HomeActivity.convertWeightsToKgs();
                break;
            //lbs
            case 1:
                PreferenceManager.set_metric(false);
                HomeActivity.convertWeightsToLbs();
                break;
        }
        PreferenceManager.commitChanges();

    }

    public void SampleData(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(this.getText(R.string.confirm_title_sample_data));
        builder.setMessage(this.getText(R.string.confirm_desc_sample_data));

        //USER CLICK YES
        builder.setPositiveButton(this.getText(R.string.yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                PreferenceManager.SampleData();
                WeightFileHandler.SampleData(getContext());
                dialog.dismiss();
                //https://stackoverflow.com/questions/15564614/how-to-restart-an-android-application-programmatically
                Intent i = getContext().getPackageManager().getLaunchIntentForPackage( getContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().finish();
                startActivity(i);
            }
        });
        //USER CLICK NO
        builder.setNegativeButton(this.getText(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void wipeAllData(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(this.getText(R.string.confirm_title));
        builder.setMessage(this.getText(R.string.confirm_desc));

        //USER CLICK YES
        builder.setPositiveButton(this.getText(R.string.yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Wipe();
                dialog.dismiss();
                Intent i = getContext().getPackageManager().getLaunchIntentForPackage( getContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().finish();
                startActivity(i);
            }
        });

        //USER CLICK NO
        builder.setNegativeButton(this.getText(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void Wipe(){
        PreferenceManager.Wipe();
        WeightFileHandler.Wipe();
    }
}
