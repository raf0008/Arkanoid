package com.example.arkanoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
