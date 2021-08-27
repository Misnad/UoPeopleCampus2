package com.misnadqasim.uopeoplecampus2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

public class ContributeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);
    }

    public void shareApp(View view) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "UoPeople Campus 2");
            String shareMessage= "\nTry this open-source UoPeople Moodle app\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose share app"));
        } catch(Exception e) {
            Log.e("", e.toString());
        }
    }

    public void openGithubRepo(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Misnad/UoPeopleCampus2/")));
    }

    public void rateAppOnPlayStore(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
    }

    public void donate(View view) {
        Snackbar.make(findViewById(R.id.contribute), "Thanks for your intention to help. We don't accept donations now. You can donate by improving the app or by sharing the app.", Snackbar.LENGTH_LONG).show();
    }

}