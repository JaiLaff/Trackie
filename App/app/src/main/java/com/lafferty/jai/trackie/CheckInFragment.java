package com.lafferty.jai.trackie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class CheckInFragment extends Fragment {

    TextView tvCheckInUnit;
    EditText etCheckInWeight;
    Button btAdd;

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_check_in,container, false);
    }

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        tvCheckInUnit = view.findViewById(R.id.tvCheckInUnit);
        etCheckInWeight = view.findViewById(R.id.etCheckInWeight);
        btAdd = view.findViewById(R.id.btAdd);

        btAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AddWeight(etCheckInWeight.getText().toString());
                ((HomeActivity)getActivity()).CreateStandardHomeFragment();
            }
        });
    }

    public void AddWeight(String text){
        float weight;
        long date = Stats.getLongDateWithoutTime();
        try{
            weight = Float.parseFloat(text);
            String filename = ((HomeActivity)getActivity()).get_filename();
            ArrayList<Weight> weights = ((HomeActivity)getActivity()).get_weights();
            WeightFileHandler fh = new WeightFileHandler(filename,getContext(),weights);
            ((HomeActivity)getActivity()).add_weight(fh.WriteWeight(date,weight));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
