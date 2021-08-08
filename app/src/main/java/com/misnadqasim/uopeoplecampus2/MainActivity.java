package com.misnadqasim.uopeoplecampus2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    WebView moodle;
    LinearLayout mainToolView, bottomBar;
    Button homeBtn, dashBtn, fullSrnBtn, gradeBtn, msgBtn;

    float dpi, ydpi;
    int width, height;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dpi = getResources().getDisplayMetrics().densityDpi;
        ydpi = getResources().getDisplayMetrics().ydpi;
        height = getResources().getDisplayMetrics().heightPixels;
        width = getResources().getDisplayMetrics().widthPixels;


        moodle = findViewById(R.id.main_web_view);
        mainToolView = findViewById(R.id.mainLinearLayout);
        bottomBar = findViewById(R.id.bottomBar);

        homeBtn = findViewById(R.id.homeBtn);
        dashBtn = findViewById(R.id.dashBtn);
        fullSrnBtn = findViewById(R.id.fullScrnBtn);
        gradeBtn = findViewById(R.id.gradeBtn);
        msgBtn = findViewById(R.id.msgBtn);

        moodle.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height-180*(ydpi/283)) ));


        moodle.setWebViewClient(new MyBrowser());
        moodle.getSettings().setLoadsImagesAutomatically(true);
        moodle.getSettings().setJavaScriptEnabled(true);
        moodle.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        moodle.loadUrl("my.uopeople.edu");


        homeBtn.setOnClickListener(v -> moodle.loadUrl("my.uopeople.edu"));
        dashBtn.setOnClickListener(v -> moodle.loadUrl("my.uopeople.edu/my/"));
        fullSrnBtn.setOnClickListener(v -> enterFullSrn());
        gradeBtn.setOnClickListener(v -> moodle.loadUrl("my.uopeople.edu"));
        msgBtn.setOnClickListener(v -> moodle.loadUrl("my.uopeople.edu/message/index.php"));
    }

    private void enterFullSrn() {
        // TODO make moodle full screen, give a notification to exit full screen
        Snackbar.make(this, moodle, "Not yet available. Feature coming soon!", Snackbar.LENGTH_SHORT).show();
    }

    private static class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}