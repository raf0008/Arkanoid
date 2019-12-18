package com.example.arkanoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TheEnd extends AppCompatActivity {

    int nextL;
    TextView textView;
    Button menu;
    Button nextLevel;

    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_the_end);

        textView = (TextView) findViewById(R.id.victory);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Arka_solid.ttf");
        textView.setTypeface(custom_font);

        menu = (Button) findViewById(R.id.mainMenuGameOverButton);
        nextLevel = (Button) findViewById(R.id.nextLevel);

        boolean victory = getIntent().getBooleanExtra("victory", false);

        SharedPreferences pref = getSharedPreferences("ScorePref",0);
        //pref.edit().putFloat("Ball_left",getIntent().getFloatExtra("Ball_left",0)).apply();



        score = (TextView)findViewById(R.id.score);
        nextL = getIntent().getIntExtra("nextLevel",2);
        score.setText("Your Level " + Integer.toString(nextL-1) +" Score: "+getIntent().getIntExtra("score",0));


        int levelCount = getIntent().getIntExtra("levelCount",10);

        if(victory) {
            textView.setText("Victory");
            menu.setVisibility(View.VISIBLE);
            nextLevel.setVisibility(View.VISIBLE);

            if(nextL > levelCount){
                nextLevel.setVisibility(View.INVISIBLE);
            }
            else {
                nextLevel.setVisibility(View.VISIBLE);
            }
        }
        else {
            textView.setText("Game Over");
            menu.setVisibility(View.VISIBLE);
            nextLevel.setVisibility(View.INVISIBLE);
        }

        SharedPreferences continuePref = getSharedPreferences("ContinuePref",0);
        continuePref.edit().clear().apply();

        int scores = getIntent().getIntExtra("score",0);
        Log.d("LEVELS","LEVEL 1 Score : "+Integer.toString(scores));

        if(pref.contains("Level"+Integer.toString(getIntent().getIntExtra("current_level",1)))){
            int scs = pref.getInt("Level"+Integer.toString(getIntent().getIntExtra("current_level",1)),0);
            if(scores > scs){
                pref.edit().putInt("Level"+Integer.toString(getIntent().getIntExtra("current_level",1)),scores).apply();
            }
        }
        else pref.edit().putInt("Level"+Integer.toString(getIntent().getIntExtra("current_level",1)),scores).apply();

        int max_level;
        if(victory){
            max_level = getIntent().getIntExtra("nextLevel",0);
        }
        else {
            max_level = getIntent().getIntExtra("current_level",0);
        }


        if(pref.contains("Max_level")){
            int max = pref.getInt("Max_level",0);
            if(max_level > max){
                pref.edit().putInt("Max_level",max_level).apply();
            }
        }
        else pref.edit().putInt("Max_level",max_level).apply();



        pref.edit().putInt("Level_count",getIntent().getIntExtra("level_count",1)).apply();

        Log.d("LEVELS","Level Count: " + getIntent().getIntExtra("level_count",1));

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void MainMenu(View view){
        // Intent intent = new Intent(this, Level.class);
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    public void nextLevel(View view){
        // Intent intent = new Intent(this, Level.class);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("levelSelected",nextL);
        startActivity(intent);
    }
}
