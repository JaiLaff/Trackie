package com.lafferty.jai.trackie;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements IShareable{

    private DrawerLayout mDrawerLayout;
    private static ArrayList<Weight> _weights;
    private static Boolean _weightsInMetric = null;
    private final String WEIGHT_DATA_FILENAME = "WeightData.txt";
    private final String PREFERENCES_FILENAME = "UserPrefs";
    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        InitHelpers();
        InitUI();
        LoadWeightData();
    }

    @Override
    public void onResume(){
        super.onResume();
        InitUI();
    }

    public void InitHelpers(){
        //give all static classes the necessary info
        PreferenceManager.Initialise(this, PREFERENCES_FILENAME);
        WeightFileHandler.Initialise(WEIGHT_DATA_FILENAME, this);
        Stats.Initialise(this);
    }

    public void InitUI(){
        NavigationSetUp();
        CreateToolbar();
        CreateGraphFragment();
        CreateStandardHomeFragment();
    }

    public void NavigationSetUp(){
        mDrawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        TextView headerName = headerView.findViewById(R.id.tvHeaderName);
        if (PreferenceManager.get_name() == ""){headerName.setText(R.string.nav_header_error);}
        else {
            String text = this.getText(R.string.nav_welcome).toString();
            headerName.setText(String.format(Locale.ENGLISH, text, PreferenceManager.get_name()));
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch (menuItem.getItemId()){
                            case R.id.nav_home:
                                Intent home = new Intent(getBaseContext(),HomeActivity.class);
                                startActivity(home);
                                break;
                            case R.id.nav_converter:
                                Intent body = new Intent(getBaseContext(),BodyCalculatorActivity.class);
                                startActivity(body);
                                break;
                            case R.id.nav_bmi_calc:
                                Intent bmi = new Intent(getBaseContext(),BMIActivity.class);
                                startActivity(bmi);
                                break;
                            case R.id.nav_history:
                                Intent history = new Intent(getBaseContext(),HistoryActivity.class);
                                startActivity(history);
                                break;
                            case R.id.nav_about:
                                Intent about = new Intent(getBaseContext(),AboutActivity.class);
                                startActivity(about);
                                break;
                        }
                        return false;
                    }
                });
    }

    public void CreateToolbar(){
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
    }

    public void CreateGraphFragment(){
        GraphFragment graphFrag = new GraphFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.GraphPlaceholder, graphFrag, "GraphFragment");
        ft.commit();
        fm.executePendingTransactions();
    }

    public void CreateStandardHomeFragment(){
        StandardHomeFragment homeFrag = new StandardHomeFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.BottomFragmentPlaceholder, homeFrag, "HomeFragment");
        ft.commit();
        fm.executePendingTransactions();
    }

    public void CreateCheckInFragment(){
        CheckInFragment checkInFrag = new CheckInFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.BottomFragmentPlaceholder, checkInFrag, "CheckInFragment");
        ft.commit();
        fm.executePendingTransactions();
    }

    public void LoadWeightData(){
        _weights = new ArrayList<>(WeightFileHandler.get_weights());
        if(!PreferenceManager.is_metric()){convertWeightsToLbs();}
    }

    public void AddValueToGraph(Weight weight){
        GraphFragment graphFrag = (GraphFragment)fm.findFragmentByTag("GraphFragment");
        graphFrag.AddValue(weight);
    }

    public void OverrideValueOnGraph(Weight weight){
        GraphFragment graphFrag = (GraphFragment)fm.findFragmentByTag("GraphFragment");
        graphFrag.OverrideValue(weight);
    }

    public void add_weight(Weight weight){
        //if same date as most recent entry
        if (_weights.size() >0 && weight.get_date() == _weights.get(_weights.size()-1).get_date()){
            _weights.remove(_weights.size()-1);
            _weights.add(weight);
            WeightFileHandler.OverrideWeight(weight.get_date(),weight.get_weight());
            OverrideValueOnGraph(weight);
        } else {
            _weights.add(weight);
            WeightFileHandler.WriteWeight(weight.get_date(),weight.get_weight());
            AddValueToGraph(weight);
        }
    }

    public static void convertWeightsToLbs(){
        //Weird way of getting around null pointer exceptions for a null Boolean

        //Solved an issue where app would begin with the userPref set to imperial yet with metric
        //data, when converting back to metric to line things up, metric data went under another
        // "Metric conversion" ruining the integrity of the data

        if(Boolean.FALSE.equals(_weightsInMetric)){return;}

        for (Weight w : _weights){
            w.convertToLbs();
        }
        _weightsInMetric = false;
    }

    public static void convertWeightsToKgs(){
        if(Boolean.TRUE.equals(_weightsInMetric)){return;}

        for (Weight w : _weights){
            w.convertToKgs();
        }
        _weightsInMetric = true;
    }

    public void Share(){
        String result = "My last 14 days:\n";
        String date;
        double weight;
        String strWeight;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");

        for (Weight w : _weights){
            if (w.get_date() > Stats.getLongDateWithoutTime() || w.get_date() < Stats.getLongDateWithoutTime()-1209600000){
                continue;
            }
            date = sdf.format(new Date(w.get_date()));
            weight = w.get_weight();

            if (!PreferenceManager.is_metric()){
                weight = Calc.KgToPound(weight);
            }

            strWeight = String.format(Locale.US, "%.1f", weight);
            result += String.format(Locale.US, "%s | %s%s\n", date, strWeight,PreferenceManager.get_weightUnit());
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, result);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_title)));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        if(this instanceof IShareable) {
            inflater.inflate(R.menu.action_bar, menu);
        } else {
            inflater.inflate(R.menu.action_bar_no_share, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;

            case R.id.action_share:
                Share();
                return true;

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public static ArrayList<Weight> get_weights(){
        return _weights;
    }

    public static Weight get_recent_weight(){
        if (_weights.size() > 0){ return _weights.get(_weights.size()-1); }
        else {return null;}
    }
}
