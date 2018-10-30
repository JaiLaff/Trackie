package com.lafferty.jai.trackie;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GraphFragment extends Fragment {

    private ArrayList<Weight> _weights;
    private LineChart graph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graph,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
       _weights = new ArrayList<>(((HomeActivity)getActivity()).get_weights());
       graph = view.findViewById(R.id.graph);
       CreateGraph();
    }

    public void CreateGraph(){
        LineData lineData = new LineData(CreateDataSet());
        if (_weights.isEmpty()){
            graph.clear();
        } else {
            graph.setData(lineData);
        }
        StyleGraph();
        graph.invalidate();
    }

    public ArrayList<Entry> GenerateEntries(){
        ArrayList<Entry> entries = new ArrayList<>();
        for (Weight w : _weights){
            entries.add(new Entry(w.get_date(),w.get_weight()));
        }
        return entries;
    }

    public LineDataSet CreateDataSet(){
        LineDataSet dataSet = new LineDataSet(GenerateEntries(), "Weight");
        dataSet.setColor(ContextCompat.getColor(getActivity(),R.color.colorAccent));
        dataSet.setCircleColor(ContextCompat.getColor(getActivity(),R.color.colorAccent));
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(12);
        dataSet.setValueFormatter(new WeightValueFormatter());
        return dataSet;
    }

    public void StyleGraph(){
        // Visual
        graph.setNoDataText("Start by entering your current weight!");
        graph.setNoDataTextColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark));
        graph.setDrawGridBackground(false);
        graph.setDrawBorders(false);
        Description desc = new Description();
        desc.setText("");
        graph.setDescription(desc);

        Legend legend = graph.getLegend();
        legend.setEnabled(false);

        //Axis
        XAxis myX = graph.getXAxis();
        myX.setValueFormatter(new MyXAxisValueFormatter());
        myX.setTextSize(12);
        myX.setGranularity(2f);
        myX.setLabelCount(3);
        myX.setPosition(XAxis.XAxisPosition.BOTTOM);
        myX.setDrawGridLines(false);

        graph.getAxisRight().setEnabled(false);

        YAxis leftAxis = graph.getAxisLeft();
        leftAxis.setTextSize(14);
        leftAxis.setGranularity(2f);
        leftAxis.setLabelCount(6);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawGridLines(false);


        //Functional
        graph.setTouchEnabled(false);
        graph.setScaleEnabled(false);
        graph.setPinchZoom(false);
        graph.setDoubleTapToZoomEnabled(false);
    }

    public void RefreshGraph(){
        graph.notifyDataSetChanged();
        graph.moveViewToX(0);
        graph.invalidate();
    }

    public void AddValue(Weight weight){
        LineData lineData = graph.getLineData();
        if(lineData == null){
            _weights.add(weight);
            CreateGraph();
        } else {
            lineData.addEntry(new Entry(weight.get_date(), weight.get_weight()), 0);
            RefreshGraph();
        }
    }

    public void OverrideValue(Weight weight){
        LineData lineData = graph.getLineData();
        LineDataSet dataSet = (LineDataSet)lineData.getDataSets().get(0);
        dataSet.removeLast();
        lineData.addEntry(new Entry(weight.get_date(),weight.get_weight()),0);
        dataSet.notifyDataSetChanged();
        RefreshGraph();
    }

    //
    //  Date Value Formatter
    //

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String result = sdf.format(new Date((long)value));

            return result;
        }
    }

    public class WeightValueFormatter implements IValueFormatter{

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            String result = String.format(Locale.ENGLISH, "%.1f", value);
            return result;
        }
    }
}
