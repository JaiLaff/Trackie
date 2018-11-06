package com.lafferty.jai.trackie;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

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
        //If file does not exist, create it.
        try {

            File myFile = new File(_context.getFilesDir(),_filename);

            myFile.createNewFile();

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

    public static void SampleData(Context context){
        CheckFileStatus();
        Wipe();
        ReadFromAssets(context);
    }

    public static void ReadFromAssets(Context context){
        try{
            AssetManager am = context.getAssets();
            InputStream inputStream = am.open(_filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String s = "";

                while ((s = bufferedReader.readLine()) != null){
                    if(s == ""){continue;}
                    else {
                        long date = 0;
                        double weightVal = 0;

                        String[] splitResult = s.split(",");
                        date = Long.parseLong(splitResult[0]);
                        weightVal = Double.parseDouble(splitResult[1]);

                        s = "";
                        WriteWeight(date,weightVal);
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public static ArrayList<Weight> get_weights(){
        return _weights;
    }
}