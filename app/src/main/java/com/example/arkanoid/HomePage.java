package com.example.arkanoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {

    TextView arkanoid;
    TextView taptocontinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_page);

        arkanoid = (TextView)findViewById(R.id.arkanoid);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Arka_solid.ttf");
        arkanoid.setTypeface(custom_font);

        int fadeInDuration = 1000; // Configure time values here
        int timeBetween = 3000;
        int fadeOutDuration = 1000;

        taptocontinue = (TextView)findViewById(R.id.taptocontinue);

       // taptocontinue.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setStartOffset(fadeInDuration);
        fadeOut.setDuration(fadeOutDuration);


        AnimationSet animation = new AnimationSet(true); // change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);

        animation.setRepeatCount(1);
        taptocontinue.setAnimation(animation);


      /*  SharedPreferences continuePref = getSharedPreferences("ContinuePref",0);
        continuePref.edit().clear().apply();

        SharedPreferences scorePref = getSharedPreferences("ScorePref",0);
        scorePref.edit().clear().apply();*/
    }

    public void MainMenu(View view){
        // Intent intent = new Intent(this, Level.class);
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }


}
