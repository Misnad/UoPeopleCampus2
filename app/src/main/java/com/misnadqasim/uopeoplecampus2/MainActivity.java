package com.misnadqasim.uopeoplecampus2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    WebView moodle;
    LinearLayout mainToolView, bottomBar;
    Button homeBtn, dashBtn, fullSrnBtn, gradeBtn, msgBtn;
    int Y, a;   // for mainToolView raising mechanism
    int X;
    boolean toolsRaised, fullScreenMode;

    int width, height;

    int bottom_nav_height, moodleHeight;
    int screen_height;

    String TAG = "abc";

    String CHANNEL_ID = "1";
    int FULLSCREEN_NOTIFICATION_ID = 31231;
    NotificationCompat.Builder builder;


    private static class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
            // TODO test below code
            return Uri.parse(url).getHost().equals("uopeople.edu");
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (toolsRaised) {
                        setMoodleHeightToDefault();
                    } else if (moodle.canGoBack()) {
                        moodle.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // getting screen size
        height = getResources().getDisplayMetrics().heightPixels;
        width = getResources().getDisplayMetrics().widthPixels;

        // setting xml views
        moodle = findViewById(R.id.main_web_view);
        mainToolView = findViewById(R.id.mainLinearLayout);
        bottomBar = findViewById(R.id.bottomBar);

        homeBtn = findViewById(R.id.homeBtn);
        dashBtn = findViewById(R.id.dashBtn);
        fullSrnBtn = findViewById(R.id.fullScrnBtn);
        gradeBtn = findViewById(R.id.gradeBtn);
        msgBtn = findViewById(R.id.msgBtn);

        // webView settings
        moodle.setWebViewClient(new MyBrowser());
        moodle.getSettings().setLoadsImagesAutomatically(true);
        moodle.getSettings().setJavaScriptEnabled(true);
        moodle.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        moodle.loadUrl("my.uopeople.edu");

        // bottomBar button listeners
        homeBtn.setOnClickListener(v -> moodle.loadUrl("my.uopeople.edu"));
        dashBtn.setOnClickListener(v -> moodle.loadUrl("my.uopeople.edu/my/"));
        fullSrnBtn.setOnClickListener(v -> enterFullSrn());
        gradeBtn.setOnClickListener(v -> moodle.loadUrl("https://my.uopeople.edu/mod/book/view.php?id=45606"));
        msgBtn.setOnClickListener(v -> moodle.loadUrl("my.uopeople.edu/message/index.php"));

        // creating notification channel for the app.
        createNotificationChannel();

        // set onClick listeners for media links
        setUoPeopleMediaLinks();

        mainToolView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mainToolView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                bottom_nav_height = (int) (moodle.getHeight() - dpToPixel(80));
                screen_height = (int) moodle.getHeight();
                mainToolView.setY(bottom_nav_height);
                moodleHeight = (int) (moodle.getHeight() - dpToPixel(80));
                moodle.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, moodleHeight));
            }
        });

        moodle.setOnTouchListener((v, event) -> {
            if (!fullScreenMode)
                setMoodleHeightToDefault();
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    X = (int) event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    if ((int) event.getX() == X) {
                        admobShowAd();
                    }
                    break;
            }
            return false;
        });

        if (sharedPreferences.getBoolean(SettingsActivity.showAdKey, true)) {
            admobInitialize();
            admobLoadAd();
        }
    }

    private void admobInitialize() {
        MobileAds.initialize(this, initializationStatus -> {
        });
    }

    private InterstitialAd mInterstitialAd;

    private void admobLoadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        admobCallback();
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    private void admobCallback() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.d("TAG", "The ad was dismissed.");
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    Log.d("TAG", "The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                    Log.d("TAG", "The ad was shown.");
                    admobLoadAd();
                }
            });
        }
    }

    private void admobShowAd() {
        if (mInterstitialAd != null && sharedPreferences.getBoolean(SettingsActivity.showAdKey, true)) {
            mInterstitialAd.show(this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Y = (int) event.getY();
                a = (int) mainToolView.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Y > a) {
                    int diff = a - Y;
                    int pos = (int) event.getY() + diff;
                    if (pos < bottom_nav_height && pos > screen_height - mainToolView.getHeight()) {
                        mainToolView.setY(pos);
                        moodle.setBottom(pos);
                        toolsRaised = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (event.getRawY() < bottom_nav_height - 80) {
                    slideY(mainToolView, (int) mainToolView.getY(), screen_height - mainToolView.getHeight());
                    moodle.setBottom(screen_height - mainToolView.getHeight());
                } else {
                    slideY(mainToolView, (int) mainToolView.getY(), bottom_nav_height);
                    moodle.setBottom(moodleHeight);
                }
                break;
        }
        return true;
    }

    private void setMoodleHeightToDefault() {
        slideY(mainToolView, (int) mainToolView.getY(), bottom_nav_height);
        moodle.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, moodleHeight));
        toolsRaised = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void enterFullSrn() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);   //Set full
        hideSystemUI();
        mainToolView.setVisibility(View.INVISIBLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getCurrentWindowMetrics();
        moodle.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, displayMetrics.heightPixels));
        makeNotification();

        Toast.makeText(this, "Click the ongoing notification to exit full screen.", Toast.LENGTH_LONG).show();
        fullScreenMode = true;
    }

    private void exitFullSrn() {
        builder.setOngoing(false);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(FULLSCREEN_NOTIFICATION_ID);


        showSystemUI();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainToolView.setVisibility(View.VISIBLE);
        fullScreenMode = false;
//        slideView(moodle, moodle.getLayoutParams().height, (int) (height - 180 * (ydpi / 283)));
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

    public static void slideY(View view, int currentY, int newY) {
        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentY, newY)
                .setDuration(500);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            view.setY(value);
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


        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
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
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        exitFullSrn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    private float dpToPixel(float dp) {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public void setUoPeopleMediaLinks() {
        ImageView facebook = findViewById(R.id.media_facebook);
        ImageView twitter = findViewById(R.id.media_twitter);
        ImageView linkedin = findViewById(R.id.media_linkedin);
        ImageView youtube = findViewById(R.id.media_youtube);
        ImageView instagram = findViewById(R.id.media_instagram);

        View.OnClickListener mediaLinkListener = v -> {
            if (v == facebook) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/UoPeople")));
            } else if (v == twitter) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/UoPeople")));
            } else if (v == linkedin) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/edu/school?id=42066&trk=tyah&trkInfo=tarId%3A1398693253238%2Ctas%3Auniversity%20of%20the%20people%2Cidx%3A3-2-4")));
            } else if (v == youtube) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/user/UoPeople")));
            } else if (v == instagram) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/uopeople.official")));
            }
        };

        facebook.setOnClickListener(mediaLinkListener);
        twitter.setOnClickListener(mediaLinkListener);
        linkedin.setOnClickListener(mediaLinkListener);
        youtube.setOnClickListener(mediaLinkListener);
        instagram.setOnClickListener(mediaLinkListener);
    }

    public void startContributeActivity(View view) {
        startActivity(new Intent(this, ContributeActivity.class));
    }

    public void license(View view) {
        moodle.loadUrl("https://raw.githubusercontent.com/Misnad/UoPeopleCampus2/master/LICENSE");
        setMoodleHeightToDefault();
    }

    public void shareApp(View view) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "UoPeople Campus 2");
            String shareMessage = "\nTry this open-source UoPeople Moodle app\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose share app"));
        } catch (Exception e) {
            Log.e("", e.toString());
        }
    }

    public void openGithubIssue(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Misnad/UoPeopleCampus2/issues")));
    }

    public void openNotesActivity(View view) {
        Snackbar.make(mainToolView, "Coming Soon", Snackbar.LENGTH_SHORT).show();
    }

    public void openGooglePlay(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
    }

    public void settings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }


}