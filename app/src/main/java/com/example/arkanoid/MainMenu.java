package com.example.arkanoid;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.content.SharedPreferences;

import android.os.Bundle;

public class MainMenu extends AppCompatActivity {

    Button button_start;
    Button button_exit;
    Button button_levels;
    Button button_highscore;
    Button button_continue;

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);


        String maxLevel = pref.getString("maxLevel","0");
        int highestLevel = Integer.parseInt(maxLevel);
        if(highestLevel > 0){
            button_levels.setEnabled(true);
        }

        String lives = pref.getString("life","3");
        int numberOfLives = Integer.parseInt(lives);

        String score = pref.getString("score","0");
        int currentScore = Integer.parseInt(score);
        currentScore = 1;
        if(numberOfLives > 0 && currentScore > 0){
            button_continue.setVisibility(View.VISIBLE);
            button_continue.setEnabled(true);
        }
        else {
            button_continue.setEnabled(false);
            button_continue.setVisibility(View.INVISIBLE);
        }

        String highScore = pref.getString("highscore","0");
        int highestScore = 1;// Integer.parseInt(highScore);
        if(highestScore > 0){
            button_highscore.setEnabled(true);
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void continueGame(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("newGame",false);
        startActivity(intent);
    }

    public void highScoreView(View view){
        //Intent intent = new Intent(this, HighScore.class);
        Intent intent = new Intent(this, High_score.class);
        startActivity(intent);
    }

}
