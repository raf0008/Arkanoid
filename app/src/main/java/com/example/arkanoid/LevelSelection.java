package com.example.arkanoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LevelSelection extends AppCompatActivity {

    Button[] levels;
    TextView[] tx;
    int levelCount;
    int screenX, screenY;

    int margin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_level_selection);


        //SharedPreferences LEVEL count ze souboru
        levelCount = 100;
        levels = new Button[levelCount];
        tx = new TextView[levels.length];

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenX = size.x;
        screenY = size.y;

        margin = ((int)(screenX*5/95));

        createLevels();
    }

    private void createLevels(){
      //  LinearLayout linear = (LinearLayout) findViewById(R.id.linear);

        TableRow table1 = (TableRow) findViewById(R.id.level_row1);
        TableRow table2 = (TableRow) findViewById(R.id.level_row2);

        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

        tableRowParams.setMargins(margin, margin, 0, margin);

        TableRow[] table = new TableRow[10];

        table1.setLayoutParams(tableRowParams);
        table2.setLayoutParams(tableRowParams);

        for(int i=0;i<levels.length;i++){
            levels[i] = new Button(this);
            levels[i].setTag(i+1);
            levels[i].setText(Integer.toString(i+1));
            levels[i].setTextSize(30);
            levels[i].setWidth((int)(screenX*0.10));
            levels[i].setHeight((int)(screenX*0.15));
            levels[i].setTextColor(getResources().getColor(R.color.text_button_color_option));
            levels[i].setBackground(getResources().getDrawable(R.drawable.transparent_bg_bordered_button));
            levels[i].setRight(100);
            levels[i].setOnClickListener(btnClicked);
            tx[i] = new TextView(this);

            tx[i].setWidth(margin);

            if(i<5){
                table1.addView(levels[i]);
                table1.addView(tx[i]);
            }
            else if(i < 10){
                table2.addView(levels[i]);
                table2.addView(tx[i]);
            }
        }
    }

    View.OnClickListener btnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("level","Level " + v.getTag() + " selected");
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra("levelSelected", (Integer) v.getTag());
            startActivity(intent);
            //Object tag = v.getTag();
            //Toast.makeText(getApplicationContext(), "clicked button", Toast.LENGTH_SHORT).show();
        }
    };
}
