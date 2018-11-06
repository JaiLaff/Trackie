package com.lafferty.jai.trackie;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private ArrayList<Weight> _weights;
    private ArrayList<String> _dates;
    private ArrayList<String> _weightValues;
    private ArrayList<Double> _weightChanges;
    private Context _context;

    public HistoryListAdapter(Context context, ArrayList<Weight> weights) {
        _weights = weights;
        _dates = new ArrayList<>();
        _weightValues = new ArrayList<>();
        _weightChanges = new ArrayList<>();
        _context = context;

        populateLists();
    }

    public void populateLists(){
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        for (int i = 0; i < _weights.size(); i++){
            Weight w = _weights.get(i);
            long DateVal = w.get_date();
            double WeightVal = w.get_weight();
            _dates.add(sdf.format(new Date(DateVal)));
            _weightValues.add(String.format(Locale.ENGLISH, "%.1f", WeightVal));

            if (i!=0){
                double change = w.get_weight() - _weights.get(i-1).get_weight();
                _weightChanges.add(change);
            } else {
                _weightChanges.add(Double.valueOf(0));
            }
        }
    }

    public void formatChange(ViewHolder viewHolder, int i){
        double val = _weightChanges.get(i);
        String result;
        boolean positive;
        if (val >= 0){
            result = String.format(Locale.ENGLISH,"+%.1f", val);
            positive = true;
        } else{
            result = String.format(Locale.ENGLISH,"%.1f", val);
            positive = false;
        }
        viewHolder.tvChange.setText(result);
        if (positive){
            viewHolder.tvChange.setTextColor(ContextCompat.getColor(_context,R.color.gain));
        } else {
            viewHolder.tvChange.setTextColor(ContextCompat.getColor(_context,R.color.loss));

        }
    }



    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_list_item,viewGroup,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int i) {
        viewHolder.tvWeight.setText(String.format(Locale.ENGLISH, "%s%s",_weightValues.get(i),PreferenceManager.get_weightUnit()));
        viewHolder.tvDate.setText(_dates.get(i));
        formatChange(viewHolder,i);
    }

    @Override
    public int getItemCount() {
        return _weights.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvWeight;
        TextView tvDate;
        TextView tvChange;

        public ViewHolder( View itemView){
            super(itemView);

            tvWeight = itemView.findViewById(R.id.tvListWeight);
            tvDate = itemView.findViewById(R.id.tvListDate);
            tvChange = itemView.findViewById(R.id.tvListChange);
        }
    }
}
