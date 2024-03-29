package com.example.arkanoid;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class High_score extends AppCompatActivity{

    Intent intent;
    ListView list;
    TextView highScoreText;

    ScoreAdapter scoreAdapter;

    SharedPreferences scorePref;

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

        scorePref = getSharedPreferences("ScorePref",0);
        int sc = scorePref.getInt("Level1",0);

        Log.d("LEVELS","Level 1 in HIGH SCORE : " + Integer.toString(sc));

        list = (ListView)findViewById(R.id.list);
        ArrayList<Score> scores = new ArrayList<>();

        Random rand = new Random();
        int nmb;

        int levelCount = scorePref.getInt("Level_count",1);

        Log.d("LEVELS","Level Count in HIGH SCORE: " + levelCount);

        for(int i=1;i < levelCount + 1; i++){
            nmb = scorePref.getInt("Level"+Integer.toString(i),0);//rand.nextInt(5000)+300;
            Log.d("LEVELS","LEVEL" + (i) + " SCORE: " + nmb);
            if(nmb > 0){
                scores.add(new Score("Level "+Integer.toString(i),"High Score: "+Integer.toString(nmb),"17. 12. 2019"));
                Log.d("LEVELS","LEVEL" + (i) + " SCORE: " + nmb);
            }
        }

        scoreAdapter = new ScoreAdapter(this,scores);
        list.setAdapter(scoreAdapter);
    }

    public void mainMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    class Score {
        String levelName;
        String score;
        String date;

        Score(String levelName, String score, String date){
            this.levelName = levelName;
            this.date = date;
            this.score = score;
        }
    }

    class ScoreAdapter extends ArrayAdapter<Score>{
        private Context context;
        private List<Score> list = new ArrayList<>();

        public ScoreAdapter(@NonNull Context context, ArrayList<Score> list){
            super(context, 0 , list);
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if(listItem == null)
                listItem = LayoutInflater.from(context).inflate(R.layout.level_item,parent,false);

            Score currentLevelScore = list.get(position);

            TextView level = (TextView) listItem.findViewById(R.id.textView1);
            level.setText(currentLevelScore.levelName);

            TextView highScore = (TextView) listItem.findViewById(R.id.textView2);
            highScore.setText(currentLevelScore.score);

            TextView dateOfHighScore = (TextView) listItem.findViewById(R.id.textView3);
            dateOfHighScore.setText(currentLevelScore.date);

            return listItem;
        }
    }
}
