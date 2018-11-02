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

import java.util.ArrayList;
import java.util.Locale;
//TODO General: Make sure all buttons on all activities/fragments are hooked up
//TODO General: Make sure metric to lbs conversion and textviews are updated everywhere
//TODO General: Async task to load the app for the first time
//TODO 1: Make Bmi details activity
//TODO 2: Use the bmi activity as a template to make the about avtivity
//TODO 3: Give the calc object more functionality in terms of converting units
//TODO 4: Create basic unit converter activity
//TODO 5: Add HomeActivity to side nav menu on all activities
//TODO 6: Link all pages in side nav menu on all activities
//TODO 7: Create some form of sharing mechanism (Interface or different action bar)

public class HomeActivity extends AppCompatActivity {

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

        //Access the header layout
        View headerView = navigationView.getHeaderView(0);
        TextView headerName = headerView.findViewById(R.id.tvHeaderName);
        if (PreferenceManager.get_name() == ""){headerName.setText(R.string.nav_header_error);}
        else {
            String text = this.getText(R.string.nav_welcome).toString();
            headerName.setText(String.format(Locale.ENGLISH, text, PreferenceManager.get_name()));
        }
        //TODO: Set some cool text here, stats etc

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
                            case R.id.nav_converter:
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
        //only if same date
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
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
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();

                return super.onOptionsItemSelected(item);

        }
    }

    public static ArrayList<Weight> get_weights(){
        return _weights;
    }


}
