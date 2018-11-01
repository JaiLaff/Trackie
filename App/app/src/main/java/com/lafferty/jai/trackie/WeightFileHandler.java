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

    private static Context _context;
    private static ArrayList<Weight> _weights;
    private static FileInputStream _fis;
    private static FileOutputStream _fos;
    private static String _filename;

    public static void Initialise(String filename, Context context){
        _context = context;
        _filename = filename;
        _weights = new ArrayList<>(ReadWeights());
    }

    public static ArrayList<Weight> ReadWeights () {
        ArrayList<Weight> result = new ArrayList<>();

        try {

            CheckFileStatus();

            _fis = _context.openFileInput(_filename);

            if (_fis != null){
                InputStreamReader inputStreamReader = new InputStreamReader(_fis);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String s = "";
                int i = 0;

                while ((s = bufferedReader.readLine()) != null){
                    if(s == ""){continue;}
                    else {
                        long date = 0;
                        double weightVal = 0;

                        String[] splitResult = s.split(",");
                        date = Long.parseLong(splitResult[0]);
                        weightVal = Double.parseDouble(splitResult[1]);

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

    public static Weight WriteWeight(long date, double weightVal, Boolean append){

        Weight weight = new Weight(date, weightVal);
        _weights.add(weight);
        String result = String.format(Locale.ENGLISH, "%d,%f",date, weightVal);

        try{
            if (append){_fos = _context.openFileOutput(_filename,Context.MODE_APPEND);}
            else {_fos = _context.openFileOutput(_filename, Context.MODE_PRIVATE);}
            if (_fos != null) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(_fos);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                if(ReadWeights().size() != 0){ bufferedWriter.newLine();}
                bufferedWriter.write(result);
                bufferedWriter.close();
                outputStreamWriter.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return weight;
    }

    public static Weight WriteWeight(long date, double weightVal){
        return WriteWeight(date,weightVal,true);
    }

    public static void DeleteLastEntry(){
        ArrayList<Weight> newWeights = ReadWeights();
        newWeights.remove(newWeights.size()-1);
        if(!newWeights.isEmpty()) {
            WriteWeight(newWeights.get(0).get_date(), newWeights.get(0).get_weight(), false);
            for (Weight w : newWeights.subList(1, newWeights.size())) {
                WriteWeight(w.get_date(), w.get_weight());
            }
        }
    }

    public static void OverrideWeight(long date, double weightVal){
        DeleteLastEntry();
        WriteWeight(date, weightVal);
    }

    public static void CheckFileStatus(){
        try {

            File myFile = new File(_context.getFilesDir(),_filename);

            if (myFile.createNewFile()) {
                //CRASHES APP
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Wipe(){
        try{
            _fos = _context.openFileOutput(_filename, Context.MODE_PRIVATE);
            if (_fos != null) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(_fos);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write("");
                bufferedWriter.close();
                outputStreamWriter.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static ArrayList<Weight> get_weights(){
        return _weights;
    }
}
