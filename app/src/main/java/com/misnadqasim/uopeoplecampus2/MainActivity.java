package com.misnadqasim.uopeoplecampus2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
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


    String CHANNEL_ID = "1";
    int FULLSCREEN_NOTIFICATION_ID = 1;

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

        createNotificationChannel();
    }

    private void enterFullSrn() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);   //Set full
        hideSystemUI();
        slideView(moodle, moodle.getLayoutParams().height, height);
        makeNotification();
    }

    private void exitFullSrn() {
        showSystemUI();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        slideView(moodle, moodle.getLayoutParams().height, (int) (height-180*(ydpi/283)) );

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(FULLSCREEN_NOTIFICATION_ID);
    }

    private static class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public static void slideView(View view, int currentHeight, int newHeight) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(500);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            view.getLayoutParams().height = value;
            view.requestLayout();
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    public void makeNotification() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_fullscreen_24)
                .setContentTitle("You are viewing Moodle in FullScreen mode")
                .setContentText("Tap to exit full screen mode")
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        builder.setOngoing(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(FULLSCREEN_NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.channel_name);
            CharSequence name = "channel_name";
//            String description = getString(R.string.channel_description);
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {
        // Shows the system bars by removing all the flags
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        exitFullSrn();
    }




}