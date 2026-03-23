package com.medalert.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.medalert.R;

import java.util.ArrayList;
import java.util.List;

public class GuidesActivity extends AppCompatActivity {

    private List<Guide> guideList;
    private LinearLayout guidesList;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guides);

        // Initialize UI Components
        guidesList = findViewById(R.id.guides_list);
        searchBar = findViewById(R.id.search_bar);
        ImageButton backButton = findViewById(R.id.btn_back);

        backButton.setOnClickListener(v -> finish());

        // Load and Display Guides
        loadGuides();
        displayGuides(guideList);

        // Set Search Bar Listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterGuides(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    /**
     * Load Static Guide Data
     */
    private void loadGuides() {
        guideList = new ArrayList<>();
        guideList.add(new Guide("Performing CPR",
                "Learn how to perform CPR in emergencies.",
                "https://www.youtube.com/watch?v=dlkgjYvHx-U",
                "https://www.medicalnewstoday.com/articles/324712"));

        guideList.add(new Guide("Helping Someone Who is Choking",
                "How to assist a choking person.",
                "https://www.youtube.com/watch?v=ewmbiHraztk",
                "https://www.mayoclinic.org/first-aid/first-aid-choking/basics/art-20056637#:~:text=Bend%20the%20person%20over%20at,known%20as%20the%20Heimlich%20maneuver."));

        guideList.add(new Guide("Struggling with an Anxiety Attack",
                "Manage anxiety attacks with calming techniques.",
                "https://www.youtube.com/watch?v=HcVAuHxEQfQ",
                "https://www.lancastergeneralhealth.org/healthwise-library/condition-categories/mental-and-behavioral-health?lang=en-us&DocumentId=hw53602"));

        guideList.add(new Guide("Having an Allergic Reaction",
                "What to do during an allergic reaction.",
                "https://www.youtube.com/watch?v=9ZBCIVpFYgM",
                "https://www.betterhealth.vic.gov.au/health/conditionsandtreatments/allergic-reactions-emergency-first-aid"));
    }


    /**
     * Filter Guides Based on Search Input
     */
    private void filterGuides(String searchText) {
        List<Guide> filteredList = new ArrayList<>();
        for (Guide guide : guideList) {
            if (guide.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(guide);
            }
        }
        displayGuides(filteredList);
    }

    /**
     * Display Guides Dynamically
     */
    private void displayGuides(List<Guide> list) {
        guidesList.removeAllViews();  // Clear previous items

        for (Guide guide : list) {
            View guideView = LayoutInflater.from(this).inflate(R.layout.item_guide, guidesList, false);

            TextView title = guideView.findViewById(R.id.title);
            TextView description = guideView.findViewById(R.id.description);

            // Set Guide Data
            title.setText(guide.getTitle());
            description.setText(guide.getDescription());

            // Set Clickable Links
            setLinkClick(guideView.findViewById(R.id.link_video), guide.getVideoUrl());
            setLinkClick(guideView.findViewById(R.id.link_article), guide.getArticleUrl());

            // Add Guide to the Layout
            guidesList.addView(guideView);
        }
    }

    /**
     * Open Links on Click
     */
    private void setLinkClick(TextView textView, String url) {
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }
}
