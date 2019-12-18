package com.example.arkanoid;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    Button button_start;
    Button button_exit;
    Button button_levels;
    Button button_highscore;
    Button button_continue;
    TextView arkanoid;

    SharedPreferences pref;
    SharedPreferences scorePref;

    int maxLevel;
    int level1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getActionBar().hide();
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_menu);

      //  button_exit = (Button) findViewById(R.id.)

        button_start = (Button) findViewById(R.id.newGame);
        button_levels = (Button) findViewById(R.id.levels);
        button_continue = (Button) findViewById(R.id.continueGame);
        button_highscore = (Button) findViewById(R.id.highScore);
        button_exit = (Button) findViewById(R.id.exit);

        button_levels.setEnabled(false);
        button_highscore.setEnabled(false);
        button_continue.setVisibility(View.INVISIBLE);
        button_continue.setEnabled(false);

        arkanoid = (TextView)findViewById(R.id.arkanoidMenu);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Arka_solid.ttf");
        arkanoid.setTypeface(custom_font);

        pref = getSharedPreferences("ContinuePref",0);
        scorePref = getSharedPreferences("ScorePref",0);

        maxLevel = scorePref.getInt("Max_level",0);

        level1 = scorePref.getInt("Level1",0);

     //   Log.d("SHARED","LEVEL 1 Score : "+Integer.toString(pref.getInt("Level1",0)));

    }

    @Override
    protected void onStart() {
        super.onStart();


        Log.d("SHARED","Ball Left: " + Float.toString(pref.getFloat("Ball_left",0)));
        Log.d("SHARED","Ball Right: " + Float.toString(pref.getFloat("Ball_right",0)));
        Log.d("SHARED","Ball Top: " + Float.toString(pref.getFloat("Ball_top",0)));
        Log.d("SHARED","Ball Bottom: " + Float.toString(pref.getFloat("Ball_bottom",0)));

        Log.d("SHARED","Paddle Left: " + Float.toString(pref.getFloat("Paddle_left",0)));
        Log.d("SHARED","Paddle Right: " + Float.toString(pref.getFloat("Paddle_right",0)));
        Log.d("SHARED","Paddle Top: " + Float.toString(pref.getFloat("Paddle_top",0)));
        Log.d("SHARED","Paddle Bottom: " + Float.toString(pref.getFloat("Paddle_bottom",0)));

        Log.d("SHARED","Ball Velocity X: " + Float.toString(pref.getFloat("Ball_velocity_x",0)));
        Log.d("SHARED","Ball Velovity Y: " + Float.toString(pref.getFloat("Ball_velocity_y",0)));

        Log.d("SHARED","Lives: " + Integer.toString(pref.getInt("Lives",0)));
        Log.d("SHARED","Level: " + Integer.toString(pref.getInt("Current_level",0)));
        Log.d("SHARED","Score: " + Integer.toString(pref.getInt("Score",0)));


        // Pokud má alespoň první level skóre přesahující 0 bodů, pak je umožněno
        if(maxLevel > 0){
            button_levels.setEnabled(true);
        }
        else {
            button_levels.setEnabled(false);
        }

        int numberOfLives = pref.getInt("Lives",3);

        int score = pref.getInt("Score",0);

        if(numberOfLives > 0 && score > 0){
            button_continue.setVisibility(View.VISIBLE);
            button_continue.setEnabled(true);
        }
        else {
            button_continue.setEnabled(false);
            button_continue.setVisibility(View.INVISIBLE);
        }

        if(level1 > 0){
            button_highscore.setEnabled(true);
        }
        else {
            button_highscore.setEnabled(false);
        }
    }

    public void exitApp(View view) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory( Intent.CATEGORY_HOME );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        System.exit(0);
    }

    public void startGame(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("newGame",true);
        startActivity(intent);
    }

    public void selectLevel(View view){
       // Intent intent = new Intent(this, Level.class);
        Intent intent = new Intent(this, LevelSelection.class);
        startActivity(intent);
    }

    public void continueGame(View view){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("newGame",false);
        intent.putExtra("ball_left",pref.getFloat("Ball_left",0));
        intent.putExtra("ball_right",pref.getFloat("Ball_right",0));
        intent.putExtra("ball_top",pref.getFloat("Ball_top",0));
        intent.putExtra("ball_bottom",pref.getFloat("Ball_bottom",0));

        intent.putExtra("paddle_left",pref.getFloat("Paddle_left",0));
        intent.putExtra("paddle_right",pref.getFloat("Paddle_right",0));
        intent.putExtra("paddle_top",pref.getFloat("Paddle_top",0));
        intent.putExtra("paddle_bottom",pref.getFloat("Paddle_bottom",0));

        intent.putExtra("ball_velocity_x",pref.getFloat("Ball_velocity_x",0));
        intent.putExtra("ball_velocity_y",pref.getFloat("Ball_velocity_y",0));

        intent.putExtra("lives",pref.getInt("Lives",3));
        intent.putExtra("current_level",pref.getInt("Current_level",1));
        intent.putExtra("score",pref.getInt("Score",0));
        intent.putExtra("level_map",pref.getString("Level_map",""));
        startActivity(intent);
    }

    public void highScoreView(View view){
        //Intent intent = new Intent(this, HighScore.class);
        Intent intent = new Intent(this, High_score.class);
        intent.putExtra("levelCount",6);
        startActivity(intent);
    }

}
