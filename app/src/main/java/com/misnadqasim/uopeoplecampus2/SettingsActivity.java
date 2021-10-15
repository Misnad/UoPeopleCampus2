package com.misnadqasim.uopeoplecampus2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences pref;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch adSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        adSwitch = findViewById(R.id.ad_switch);
        adSwitch.setChecked(pref.getBoolean("SHOW_AD", true));
        adSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showAd(true);
                } else {
                    alertDialog();
                }
            }
        });
    }

    private void showAd(boolean val) {
        pref.edit().putBoolean("SHOW_AD", val).apply();
    }

    private void alertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure you want to turn of ads?");
        dialog.setTitle("Confirmation");
        dialog.setPositiveButton("NO", (dialog1, which) ->
                adSwitch.setChecked(true)
        );
        dialog.setNegativeButton("YES", (dialog12, which) ->
                showAd(false)
        );
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}