package com.ofywellness;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.tabs.TabLayout;
import com.ofywellness.fragments.AdapterForTabs;
import com.ofywellness.fragments.AddIntakeTab;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Objects to show tabs
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager);
        AdapterForTabs adapterForTabs = new AdapterForTabs(this);

        // Set adapter to tab viewer
        viewPager2.setAdapter(adapterForTabs);

        // Add listeners on tab heads
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // open respective tab fragment for tab header
                viewPager2.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // To change tab header on tab fragment change
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Get tab fragment position and select that tab head
                tabLayout.getTabAt(position).select();
            }
        });


        // Now let the user know what tab has what features and uses and educate him about them
        educateUserAboutAllTabs();


    }

    /**
     * Creates a sequence of events to showcase all the tab features
     */
    void educateUserAboutAllTabs() {

        // Get the tab layout to fetch the tabs
        TabLayout tablayout = findViewById(R.id.tab_layout);

        // Create a sequence of tap targets to show case all the tabs
        new TapTargetSequence(this).targets(

                        // Get all the required tap target objects and add to sequence
                        getTapTarget(tablayout.getTabAt(0).view, R.string.Tab_Hint_1, "You can add all your intakes here"),

                        getTapTarget(tablayout.getTabAt(1).view, R.string.Tab_Hint_2, "You can view all your intakes here"),

                        getTapTarget(tablayout.getTabAt(2).view, R.string.Tab_Hint_3, "You can track all your intakes here"),

                        getTapTarget(tablayout.getTabAt(3).view, R.string.Tab_Hint_4, "You can find all of our services here"),

                        getTapTarget(tablayout.getTabAt(4).view, R.string.Tab_Hint_5, "You can view and edit profile details here")


                ).listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        AddIntakeTab.educateUserAboutButtons();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                })
                // Now start the sequence
                .start();
    }


    /**
     * Creates and returns the tab target object for the views and strings
     */
    TapTarget getTapTarget(View view, int Title, String description) {

        // Create Tap Target object and return it
        return TapTarget.forView(view, getString(Title), description).outerCircleColorInt(Color.parseColor("#350069"));

    }

}