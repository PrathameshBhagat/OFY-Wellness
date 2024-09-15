package com.ofywellness.fragments;

import static com.ofywellness.fragments.TrackDietTab.lineChartMode.CARBOHYDRATES;
import static com.ofywellness.fragments.TrackDietTab.lineChartMode.ENERGY;
import static com.ofywellness.fragments.TrackDietTab.lineChartMode.FATS;
import static com.ofywellness.fragments.TrackDietTab.lineChartMode.PROTEINS;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.ofywellness.R;
import com.ofywellness.db.ofyDatabase;
import com.ofywellness.modals.Meal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment for DietTrack tab in Home page
 */
public class TrackDietTab extends Fragment {

    /* TODO : Remove the TextView assignment and context to database operation method */

    public lineChartMode nutrientLineChartMode;
    private TextView energyValueLabel, proteinsValueLabel, fatsValueLabel, carbohydratesValueLabel, warningLabel;;
    private ProgressBar energyProgressBar, proteinsProgressBar, fatsProgressBar, carbohydratesProgressBar;
    private BarChart barChart;
    private LineChart lineChart, curvedLineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the view for this fragment and inflate it
        View view = inflater.inflate(R.layout.fragment_track_diet_tab, container, false);

        // Assign the text views so that tracking data can be set
        energyValueLabel = view.findViewById(R.id.track_energy_display_label);
        proteinsValueLabel = view.findViewById(R.id.track_protein_display_label);
        fatsValueLabel = view.findViewById(R.id.track_fats_display_label);
        carbohydratesValueLabel = view.findViewById(R.id.track_carbohydrates_display_label);

        // Assign the progress bars so that the progress can be shown
        energyProgressBar = view.findViewById(R.id.track_energy_progress_bar);
        proteinsProgressBar = view.findViewById(R.id.track_protein_progress_bar);
        fatsProgressBar = view.findViewById(R.id.track_fats_progress_bar);
        carbohydratesProgressBar = view.findViewById(R.id.track_carbohydrates_progress_bar);


        // Assign the Line Graph
        lineChart = view.findViewById(R.id.track_line_chart);

        // Set the default Mode for line chart
        nutrientLineChartMode = ENERGY;

        // Assign the curved line chart
        curvedLineChart = view.findViewById(R.id.track_weight_curved_line_chart);

        // Assign the Bar Chart
        barChart = view.findViewById(R.id.track_water_intake_bar_chart);

        // Assign the text views so that warning can be shown to update diet target
        warningLabel = view.findViewById(R.id.track_target_not_found_label);

        // Hide the warning by default. (Warning to update diet target)
        warning(false);

        // Add on tab selected listener to the tab layout
        ((TabLayout) view.findViewById(R.id.track_line_chart_tab_layout)).addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Get the tabs position
                int tabPosition = tab.getPosition();

                // Change the line chart mode according to the tab position
                if (tabPosition == 0)
                    nutrientLineChartMode = ENERGY;
                else if (tabPosition == 1)
                    nutrientLineChartMode = PROTEINS;
                else if (tabPosition == 2)
                    nutrientLineChartMode = FATS;
                else if (tabPosition == 3)
                    nutrientLineChartMode = CARBOHYDRATES;

                // Update the line chart with intake data of appropriate nutrient
                updateChartWithNutrientIntakeData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Now we update all the data once on start as soon as this tab loads
        // Update Diet data
        updateChartWithNutrientIntakeData();
        // Update all other data
        updateChartWithOtherData();
        // Update diet tracking data
        updateDietTrackingData();

        // Return view to onCreateView method and the method
        return view;
    }

    // Update chart with diet data
    private void updateChartWithNutrientIntakeData() {

        // Simple try catch block to catch any errors and exceptions
        try {

            // Call the method to get the "updated" tracking data and set the text views to the tracking data
            ofyDatabase.getDietDataAndSetNutrientCharts(this);

        } catch (Exception e) {
            // Catch exception and show toast message
            Toast.makeText(requireActivity(), "Error:" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Update chart with "other" data
    private void updateChartWithOtherData() {

        // Simple try catch block to catch any errors and exceptions
        try {

            // Call the method to get the "other" data and set the charts
            ofyDatabase.getOtherDataAndSetCharts(this);

        } catch (Exception e) {
            // Catch exception and show toast message
            Toast.makeText(requireActivity(), "Error:" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Method to set the bar chart with daily water intake data (used by ofyDatabase method)
    public void setBarChartWithWaterIntakeData(HashMap<String, Integer> waterIntakeDataMap) {

        // Hide the description of the bar chart, we don't need it
        barChart.getDescription().setEnabled(false);

        // List for storing days for bar chart labels
        LinkedList<String> days = new LinkedList<>();

        // Entries for Bar Chart
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        // Integer for X-axis values of the chart
        int i = 1;

        // Iterate over and convert intake data to bar chart entries
        for (Map.Entry<String, Integer> waterIntakeEntries : waterIntakeDataMap.entrySet()) {

            // Fill bar chart entries { X : [ 1,2,3,...], Y : [10,1,5,8,7,..] }
            barEntries.add(new BarEntry(i++, waterIntakeEntries.getValue()));

            // Add date labels, which is stored as key for each value 
            days.add(waterIntakeEntries.getKey());
        }

        // Create a DataSet for bar chart
        BarDataSet barDataset = new BarDataSet(barEntries,"");
        // Set colors for the chart
        barDataset.setColors(ColorTemplate.COLORFUL_COLORS);

        // Create a data object for our bar chart
        BarData barData = new BarData(barDataset);

        // Get the right axis and disable it as it's not needed
        barChart.getAxisRight().setEnabled(false);

        // Now we get our bar chart's X-axis
        XAxis barXAxis = barChart.getXAxis();
        // Set granularity to 1 so that the X values don't repeat themselves on zoom
        barXAxis.setGranularity(1f);
        // Set x-axis position to bottom (default - top)
        barXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // Hide the grid lines as they are not required
        barXAxis.setDrawGridLines(false);

        // Now set its labels via value formatter to show relevant data
        barXAxis.setValueFormatter((value, axis) ->
        {
            // Simple try-catch block
            try {

                // Get the day from list of days to be mapped
                String date = days.get((int) value - 1);

                // Now parse the date string to a date object
                Date dateObj = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);

                // Return the date in "Jan 13" like format
                return new SimpleDateFormat("MMM dd", Locale.ENGLISH).format(dateObj);

            } catch (Exception e) {
                // If exception occurs, show a toast
                Toast.makeText(requireActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                // And return a string with error text for each entry
                return "Error";
            }
        });

        // Now set the data and animation for the bar chart
        barChart.setData(barData);
        barChart.animateY(5000);

        // Show/Refresh the bar chart
        barChart.invalidate();

    }

    // Method to set the weight tracker line chart (used by ofyDatabase method)
    public void setCurvedLineChartWithDailyWeightData(HashMap<String, Integer> weightDataMap, Context context) {

        // Hide the description of the chart, we don't need it
        curvedLineChart.getDescription().setEnabled(false);

        // List for storing days for chart labels
        LinkedList<String> days = new LinkedList<>();

        // Entries for the chart
        ArrayList<Entry> curvedLineEntries = new ArrayList<>();

        // Integer for X-axis values of the chart
        int i = 1;

        // Iterate over and convert daily weight data to chart entries
        for (Map.Entry<String, Integer> dailyWeightEntries : weightDataMap.entrySet()) {

            // Fill chart entries { X : [ 1,2,3,...], Y : [10,1,5,8,7,...] }
            curvedLineEntries.add(new Entry(i++, dailyWeightEntries.getValue()));

            // Add date/day labels, which is stored as key for each value 
            days.add(dailyWeightEntries.getKey());
        }


        // Create the dataset for the line chart
        LineDataSet curvedLineDataSet = new LineDataSet(curvedLineEntries, "");

        // Set the line color to black and circle (point) color to blue
        curvedLineDataSet.setColor(Color.BLACK);
        curvedLineDataSet.setCircleColor(Color.BLUE);

        // Make the line chart curved/smooth
        curvedLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Make the chart fill the area below the line/curve and set the drawable to fill it with
        curvedLineDataSet.setDrawFilled(true);
        curvedLineDataSet.setFillDrawable(ContextCompat.getDrawable(context, R.drawable.home_background));


        // Create a data object for our curved line chart from the dataset
        LineData lineData = new LineData(curvedLineDataSet);

        // Hide the right axis as we do not need a right axis for this chart
        curvedLineChart.getAxisRight().setEnabled(false);

        // Get the left axis and make it start with 0th point
        curvedLineChart.getAxisLeft().setAxisMinimum(0f);

        // Get the X-axis of the curved line chart
        XAxis lineXAxis = curvedLineChart.getXAxis();

        // Remove grid lines and set axis minimum to 0.5
        lineXAxis.setDrawGridLines(false);
        lineXAxis.setAxisMinimum(0.5f);

        // Set X-axis position to bottom (default - top)
        lineXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Set granularity to 1 so that on zoom the X-axis values do not get repeated
        lineXAxis.setGranularity(1f);

        // Now set its labels via value formatter to show relevant data
        lineXAxis.setValueFormatter((value, axis) ->
        {
            // Simple try-catch block
            try {

                // Get the exact day from list of days to be mapped
                String date = days.get((int) value - 1);

                // Now parse the date string to a date object
                Date dateObj = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);

                // Return the date in "Jan 13" like format
                return new SimpleDateFormat("MMM dd", Locale.ENGLISH).format(dateObj);

            } catch (Exception e) {
                // If exception occurs, show a toast
                Toast.makeText(requireActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                // And return a string with error text for each entry
                return "Error";
            }
        });

        // Now set the line chart's animation
        curvedLineChart.animateY(5000);

        // Finally set the data
        curvedLineChart.setData(lineData);

    }

    // Method to set the line chart with diet intake data (used by ofyDatabase method)
    public void setLineChartWithDietIntakeData(LinkedHashMap<String, Float> nutrientMap, Context context) {

        // Hide the description of the line chart, we don't need it
        lineChart.getDescription().setEnabled(false);


        // List for storing days for chart labels
        LinkedList<String> days = new LinkedList<>();

        // Entries for the chart
        ArrayList<Entry> lineChartEntries = new ArrayList<>();
        // Integer for X-axis values of the chart
        int i = 1;

        // Iterate over and convert daily weight data to chart entries
        for (Map.Entry<String, Float> nutrientEntries : nutrientMap.entrySet()) {

            // Fill chart entries { X : [1,2,3,...], Y : [10,1,5,8,7,...] }
            lineChartEntries.add(new Entry(i++, nutrientEntries.getValue()));

            // Add date/day labels, which is stored as key for each value 
            days.add(nutrientEntries.getKey());
        }

        // Create the dataset for the line chart
        LineDataSet lineDataset = new LineDataSet(lineChartEntries, "");

        // Set the line color to black and circle (point) color to blue
        lineDataset.setColor(Color.BLACK);
        lineDataset.setCircleColor(Color.BLUE);


        // Make the chart fill the area below the line/curve and set the drawable to fill it with
        lineDataset.setDrawFilled(true);
        lineDataset.setFillDrawable(ContextCompat.getDrawable(context, R.drawable.img_head_background));

        // Make the chart to make the graph line curved
        lineDataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Create a data for our line chart from the dataset
        LineData lineData = new LineData(lineDataset);


        // Hide the right axis as we do not need a right axis for this chart
        lineChart.getAxisRight().setEnabled(false);

        // Get the left axis and make it start with 0th point
        lineChart.getAxisLeft().setAxisMinimum(0f);

        // Get the x axis of line chart
        XAxis lineXAxis = lineChart.getXAxis();

        // Hide the grid lines for this axis and set its minimum to 0.5 
        lineXAxis.setDrawGridLines(false);
        lineXAxis.setAxisMinimum(0.5f);

        // Set the X-axis position to bottom (default-top)
        lineXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        // Set granularity to 1 so that on zoom the X values do not get repeated
        lineXAxis.setGranularity(1f);

        // Now set its labels via value formatter to show relevant data
        lineXAxis.setValueFormatter((value, axis) ->
        {
            // Simple try-catch block
            try {

                // Get the exact day from list of days to be mapped
                String date = days.get((int) value - 1);

                // Now parse the date string to a date object
                Date dateObj = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);

                // Return the date in "Jan 13" like format
                return new SimpleDateFormat("MMM dd", Locale.ENGLISH).format(dateObj);

            } catch (Exception e) {
                // If exception occurs, show a toast
                Toast.makeText(requireActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                // And return a string with error text for each entry
                return "Error";
            }
        });

        // Now set the line chart's animation
        lineChart.animateY(5000);

        // Finally set the data
        lineChart.setData(lineData);

    }

    // Update tracking data each time user clicks update button
    void updateDietTrackingData() {

        // Simple try catch block to catch any errors and exceptions
        try {

            // Call the method to get the "updated" tracking data and set the text views to the tracking data
            ofyDatabase.getTrackDietDataAndSetData(this, energyValueLabel, proteinsValueLabel, fatsValueLabel, carbohydratesValueLabel);

        } catch (Exception e) {
            // Catch exception and show toast message
            Toast.makeText(requireActivity(), "Error:" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Method to show or hide warning to update diet target
    public void warning(boolean show) {

        // If we are to show the warning then make view with the warning visible
        if (show)
            warningLabel.setVisibility(View.VISIBLE);
        // Else remove the view
        else
            warningLabel.setVisibility(View.GONE);

    }

    public void updateProgress() {

        // Simple try catch block to catch any errors and exceptions
        try {
            // Variables required to get current total diet intake
            int currentEnergy, currentProteins , currentFats, currentCarbohydrates;

            // Get the current progress
            currentEnergy = Integer.parseInt(energyValueLabel.getText().toString().replace("Cal", ""));
            currentProteins = Integer.parseInt(proteinsValueLabel.getText().toString().replace("g", ""));
            currentFats = Integer.parseInt(fatsValueLabel.getText().toString().replace("g", ""));
            currentCarbohydrates = Integer.parseInt(carbohydratesValueLabel.getText().toString().replace("g", ""));

            // Store in a meal object
            Meal currentProgress = new Meal(null,null,
                    currentEnergy ,
                    currentProteins ,
                    currentFats ,
                    currentCarbohydrates );

            // Call the method to update the progress
            ofyDatabase.updateDietProgress( this, currentProgress,energyProgressBar, proteinsProgressBar, fatsProgressBar, carbohydratesProgressBar) ;

        } catch (Exception e) {
            // Catch exception and show toast message
            Toast.makeText(requireActivity(), "Error:" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Enums for nutrient line chart Modes
    public enum lineChartMode {ENERGY, PROTEINS, FATS, CARBOHYDRATES}
}