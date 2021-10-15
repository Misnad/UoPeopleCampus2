package com.misnadqasim.uopeoplecampus2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import java.io.IOException;
import java.io.InputStream;


public class AppView extends LinearLayout {


    @SuppressLint("UseCompatLoadingForDrawables")
    public AppView(Context context, AttributeSet attrs) {
        this(context, attrs, "Title", "Description", 123);
    }

    public AppView(Context context,
                   AttributeSet attrs,
                   String title,
                   String description,
                   int icon) {
        super(context, attrs);

        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setMinimumHeight(toDp(200));
        android.view.ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(100, 100);
        this.setLayoutParams(layoutParams1);



        ImageView vIcon = new ImageView(context);
        android.view.ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = 100;
        layoutParams.height = 100;
        vIcon.setLayoutParams(layoutParams);
        try {
            InputStream ims = getResources().getAssets().open("001.png");
            Drawable d = Drawable.createFromStream(ims, null);
            vIcon.setImageDrawable(d);
            ims.close();
        }
        catch(IOException ex) {
            return;
        }


        this.addView(vIcon);



        TextView vTitle = new TextView(context);
        vTitle.setText("title");

        this.addView(vTitle);

        LinearLayout sub = new LinearLayout(context);

        this.setPadding(toDp(10),toDp(10),toDp(10),toDp(10));
    }


    private int toDp(int px) {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        return (int) ( px / (metrics.densityDpi / 160f) );
    }
}
