package com.lafferty.jai.trackie;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;

public class WeightFileHandler {

    private Context _context;
    private ArrayList<Weight> _weights;
    private FileInputStream _fis;
    private FileOutputStream _fos;
    private String _filename;

    public WeightFileHandler(String filename, Context context){
        _context = context;
        _filename = filename;
        _weights = ReadWeights(_filename);
    }

    public WeightFileHandler(String filename, Context context, ArrayList<Weight> weights){
        _context = context;
        _filename = filename;
        _weights = weights;
    }

    public ArrayList<Weight> ReadWeights (String filename) {
        ArrayList<Weight> result = new ArrayList<>();

        try {

            CheckFileStatus(filename);

            _fis = _context.openFileInput(filename);

            if (_fis != null){
                InputStreamReader inputStreamReader = new InputStreamReader(_fis);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String s = "";
                int i = 0;

                while ((s = bufferedReader.readLine()) != null){
                    if(s == ""){continue;}
                    else {
                        long date = 0;
                        float weightVal = 0;

                        String[] splitResult = s.split(",");
                        date = Long.parseLong(splitResult[0]);
                        weightVal = Float.parseFloat(splitResult[1]);

                        Weight weight = new Weight(date,weightVal);

                        result.add(weight);
                        s = "";
                        i ++;
                    }
                }
                bufferedReader.close();
                inputStreamReader.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }

    public Weight WriteWeight(long date, float weightVal){

        Weight weight = new Weight(date, weightVal);
        _weights.add(weight);
        String result = String.format(Locale.ENGLISH, "%d,%f",date, weightVal);

        try{
            _fos = _context.openFileOutput(_filename,Context.MODE_APPEND);
            if (_fos != null) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(_fos);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.newLine();
                bufferedWriter.write(result);
                bufferedWriter.close();
                outputStreamWriter.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return weight;
    }

    public void CheckFileStatus(String filename){
        try {

            File myFile = new File(_context.getFilesDir(),filename);

            if (myFile.createNewFile()) {
                //CRASHES APP
                WriteWeight(Stats.getLongDateWithoutTime(),0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Weight> get_weights(){
        return _weights;
    }
}
