package com.example.arkanoid;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class High_score extends AppCompatActivity{

    Intent intent;
    ListView list;
    TextView highScoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_high_score);

        highScoreText = (TextView)findViewById(R.id.highScoreText);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Arka_solid.ttf");
        highScoreText.setTypeface(custom_font);

    }


    public void mainMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
