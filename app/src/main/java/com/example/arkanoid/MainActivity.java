package com.example.arkanoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SoundPool soundPool;
    int bounce;
    int deadend;
    int destruction;
    int realScore = 0;
    int selectedLevel;
    int levelCount;

    int lives = 3;
    int score = 0;

    Paddle paddle;
    Ball ball;

    String level_map;
    String current_level_map = "";
    String loaded_map;

    float angle = 1;

    long startTime;

    StringBuilder sb = new StringBuilder();

    boolean newGame;
     SharedPreferences continuePref; // = getApplicationContext().getSharedPreferences("MyContinuePref", MODE_PRIVATE);

    boolean touched = false;

    private ArkanoidView arkanoidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        arkanoidView = new ArkanoidView(this);
        setContentView(arkanoidView);
        // setContentView(R.layout.activity_main);


        continuePref = getSharedPreferences("ContinuePref",0);


        newGame = getIntent().getBooleanExtra("newGame", true);
        if(newGame){
            continuePref.edit().clear().apply();
        }
        else {
            loaded_map = continuePref.getString("level_map","");
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
/*
    @Override
    protected void onStart() {
        super.onStart();

        boolean newGame = getIntent().getBooleanExtra("newGame", true);

        if(newGame) textView.setText("New Game");
        else textView.setText("Continue");
    }*/

    private class ArkanoidView extends SurfaceView implements Runnable, SensorEventListener {

        SensorManager sensorManager;
        Sensor accelerometer;

        Thread gameThread = null;

        SurfaceHolder surfHolder;

        boolean play;
        boolean paused;
        boolean ballStopped = true;

        Canvas canvas;
        Bitmap background;
        Paint paint;
        int screenX;
        int screenY;

        long fps;
        long fpsTime;




        Brick[] bricks = new Brick[200];
        int brickCount = 0;
        int brickLifeCount = 0;

        int brickWidth;
        int brickHeight;

        public ArkanoidView(Context context) {

            super(context);

            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();

            bounce = soundPool.load(getContext(), R.raw.bounce, 1);
            deadend = soundPool.load(getContext(), R.raw.deadend, 1);
            destruction = soundPool.load(getContext(), R.raw.destruction, 1);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener((SensorEventListener) ArkanoidView.this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

            paused = true;

            newGame = getIntent().getBooleanExtra("newGame", true);
            if(newGame)Log.d("Game session","New Game");
            else Log.d("Game session", "Continuing Game");

            surfHolder = getHolder();
            paint = new Paint();

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.space);
            background = Bitmap.createScaledBitmap(bitmap, screenX, screenY, true);

            brickWidth = screenX / 8;
            brickHeight = screenY / 10;


            paddle = new Paddle(screenX, screenY);
            ball = new Ball(screenX, screenY);

         //   if(!newGame){
           //     ball = new Ball((int)pref.getFloat("Ball_left",0),screenY);
         //   }

            createBricksAndRestart();

            draw();
        }

        public void createBricksAndRestart() {
            if(newGame){
                ball.reset(screenX, screenY);
            }
            else {
                ball.getBall().left = getIntent().getFloatExtra("ball_left",0);
                ball.getBall().right = getIntent().getFloatExtra("ball_right",0);
                ball.getBall().top = getIntent().getFloatExtra("ball_top",0);
                ball.getBall().bottom = getIntent().getFloatExtra("ball_bottom",0);

                paddle.getPaddle().left = getIntent().getFloatExtra("paddle_left",0);
                paddle.getPaddle().right = getIntent().getFloatExtra("paddle_right",0);
                paddle.getPaddle().top = getIntent().getFloatExtra("paddle_top",0);
                paddle.getPaddle().bottom = getIntent().getFloatExtra("paddle_bottom",0);

                paddle.setPositionX(paddle.getPaddle().left + ((paddle.getPaddle().right - paddle.getPaddle().left)/2));
                paddle.setPositionY(paddle.getPaddle().top + ((paddle.getPaddle().bottom - paddle.getPaddle().top)/2));


                Log.d("SHARED","VELOCITY X in Main" + Float.toString(getIntent().getFloatExtra("ball_velocity_x",200)));
                ball.setxVelocity(getIntent().getFloatExtra("ball_velocity_x",200));
                ball.setyVelocity(getIntent().getFloatExtra("ball_velocity_y",-400));

                lives = getIntent().getIntExtra("lives",3);
                score = getIntent().getIntExtra("score",0);
                current_level_map = getIntent().getStringExtra("level_map");
            }

            int width = screenX / 10;
            int height = screenY / 12;

            Random rand = new Random();
            int life;


            selectedLevel = getIntent().getIntExtra("levelSelected",1);

       /*     SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putInt("lastSelectedLevel",selectedLevel);
            editor.apply();*/

       if(newGame){
           Map mapa = new Map();
           List<String> newMap = mapa.getMap();
           levelCount = newMap.size();

           level_map = newMap.get(selectedLevel-1);
       }
       else {
           level_map = current_level_map;
       }

           /* if(continuePref.contains("level_map")&&!newGame){
                level_map = continuePref.getString("level_map","");
            }
*/
/*
           Log.d("MAPS","L:"+level_map);
           if(loaded_map != ""){
               loaded_map = level_map;
           }
*/
            brickCount = 0;
            for (int column = 0; column < 10; column++) {
                for (int row = 0; row < 6; row++) {

                    life = Integer.parseInt(Character.toString(level_map.charAt((row*10)+column)));
                    brickLifeCount = brickLifeCount + life;
                    bricks[brickCount] = new Brick(row, column, width, height, life);
                    if(life == 0) bricks[brickCount].setInvisible();
                    brickCount++;
                }
            }

            if (lives == 0) {
                score = 0;
                lives = 3;
                paddle.reset(screenX, screenY);
            }
        }

        @Override
        public void run() {
            while(play){
                long startFrameTime = System.currentTimeMillis();

                if(!paused){
                    update();
                }

                draw();

                fpsTime = System.currentTimeMillis() - startFrameTime;
                if(fpsTime >= 1){
                    fps = 1000 / fpsTime;
                }
            }
        }

        public void update() {
            if(!ballStopped){
                paddle.update(fps);
                ball.update(fps);
            }



            //for (int i = 0; i < brickCount; i++) {
            for (int row = 0; row < 6; row++) {
                for (int column = 0; column < 10; column++) {

                if (bricks[((row*10)+column)].getVisibility()) {
                    if (RectF.intersects(bricks[((row*10)+column)].getBrick(), ball.getBall())) {

                        //SharedPreferences StringBuilder(lives of bricks --> map)

                        playBounceSound();
                        if(bricks[((row*10)+column)].getLives() == 1) {bricks[((row*10)+column)].setInvisible();bricks[((row*10)+column)].lostLife();}
                        else bricks[((row*10)+column)].lostLife();



                        ball.reverseYVelocity();
                        score = score + 10;

                        if(score == brickLifeCount*10){
                            paused = true;
                            paddle.reset(screenX,screenY);
                            realScore = realScore + score + score * 100000 / ((int)(System.currentTimeMillis() - startTime));

                            Intent intent = new Intent(getContext(), TheEnd.class);
                            intent.putExtra("victory",true);
                            intent.putExtra("score",realScore);
                            intent.putExtra("current_level", selectedLevel);
                            intent.putExtra("nextLevel",selectedLevel+1);
                            intent.putExtra("level_count",levelCount);
                            startActivity(intent);
                        }
                    }


                }
                sb.append(bricks[((row)+column*6)].getLives());
                }
                //current_level_map += bricks[i].getLives();
            }

            current_level_map = sb.toString();

            sb.delete(0,sb.length());

            Log.d("MAPS","C:"+current_level_map);
            Log.d("MAPS","R:"+level_map);

       //     editorC.putString("continueMap",s);
       //     editorC.apply();

         /*   SharedPreferences pref = getApplicationContext().getSharedPreferences("MyContinuePref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putString("continueMap",s);
            editor.apply();*/

            // Kontrola kdy se míč dotkne paddle
            if (RectF.intersects(paddle.getPaddle(), ball.getBall())) {
                playBounceSound();
               // if(ball.)
                if(angle < 0){
                    ball.setxVelocity(-ball.getyVel() * (float)(Math.sqrt(-angle))/((10+(-angle))/10));

                //    ball.setxVelocity(-ball.getyVel()*(float)(Math.sqrt(-angle)));
                    ball.setyVelocity(ball.getyVel()/(float)(Math.sqrt(-angle)));
                }
                else if(angle > 1){
                    ball.setxVelocity(ball.getyVel() * (float)(Math.sqrt(angle))/((10+(angle))/10));

                   // ball.setxVelocity(ball.getxVel()*(float)(Math.sqrt(angle)));
                    ball.setyVelocity(ball.getyVel()/((float)(Math.sqrt(angle))));
                    Log.d("Velocity angle", Float.toString(angle));
                }

//                ball.setxVelocity(-ball.getyVel() * (float)(Math.sqrt(-angle))/((10+(-angle))/10));

              //  ball.setyVelocity(ball.getyVelocity()*angle);
               // ball.setRandomXVelocity();
                ball.reverseYVelocity();


                Log.d("Velocity X", Float.toString(ball.getxVelocity()));
                Log.d("Velocity Y", Float.toString(ball.getyVelocity()));

                //ball.BallDirection(ball,paddle);
                ball.clearObstacleY(paddle.getPaddle().top - 2);


            }


            // Pokud se míč dotkne země
            if (ball.getBall().bottom > screenY) {
                playDestructionSound();
                paused = true;
                realScore = score;

                // hráč ztratí život, hra se stopne a obnoví se pozice míčku a paddlu
                lives--;
                ball.reset(screenX,screenY);
                paddle.reset(screenX,screenY);

                ball.setyVelocity(ball.getyVel());
                ball.setxVelocity(ball.getxVel());

                // pokud jsou životy rovny 0 tak se hra stopne a obnoví se vše.
                if (lives == 0) {
                    paused = true;
                }
            }

            // vrchní zeď odraz
            if (ball.getBall().top < 0)
            {
                playBounceSound();
                ball.reverseYVelocity();
               // ball.clearObstacleY(24);
                ball.clearObstacleY(30);
            }

            // levá zeď
            if (ball.getBall().left < 0)
            {
                playBounceSound();
                ball.reverseXVelocity();
                ball.clearObstacleX(4);
            }

            // pravá zeď
            if (ball.getBall().right > screenX - 10) {
                playBounceSound();
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 44);

            }

            // všechny cihly rozbité
            if (score == brickLifeCount * 10)
            {
                paused = true;
                createBricksAndRestart();
            }


            // podmínka pro to jestli se paddle dotkne boků, buď levého nebo pravého
            if (paddle.getPaddle().right > screenX - 10) {

                paddle.setMoving(paddle.stop);
            }

            if (paddle.getPaddle().left < 50) {

                paddle.setMoving(paddle.stop);
            }
        }

        public void draw()
        {
            if(surfHolder.getSurface().isValid())
            {
                canvas = surfHolder.lockCanvas();
                canvas.drawBitmap(background,0,0,null);
             //   paint.setColor(Color.argb(255,255,255,255));

                paint.setColor(Color.argb(150,150,150,150));

                canvas.drawRoundRect(paddle.getPaddle(),20,20,paint);
          //      canvas.drawRect(,paint);
                //canvas.drawRect(obstruction.getRect(),paint);

                paint.setColor(Color.argb(255,80,80,80));
                canvas.drawCircle(ball.getBall().centerX(),ball.getBall().centerY(), ball.getRadius(),paint);
          //      canvas.drawRect(ball.getBall(),paint);

                paint.setColor(Color.argb(103, 56, 117, 170));

                for (int i = 0; i < brickCount; i++)
                {
                    if (bricks[i].getVisibility())
                    {
                        switch(bricks[i].getLives()){
                            case 1: paint.setColor(Color.WHITE);break;
                            case 2: paint.setColor(Color.YELLOW);break;
                            case 3: paint.setColor(Color.GREEN);break;
                            case 4: paint.setColor(Color.RED);break;
                            case 5: paint.setColor(Color.CYAN);break;
                        }

                        canvas.drawRect(bricks[i].getBrick(), paint);
                    }
                }

                // výběr barvy pro nabarvení
                paint.setColor(Color.argb(255, 255, 255, 255));

                // skore
                paint.setTextSize(40);
                canvas.drawText("Score: " + score + "   Lives: " + lives + " Level: " + selectedLevel, 10, screenY - 50, paint);


                if (lives <= 0) {
                    Intent intent = new Intent(getContext(), TheEnd.class);
                    intent.putExtra("victory", false);
                    intent.putExtra("current_level",selectedLevel);
                    intent.putExtra("score",score);
                    intent.putExtra("level_count",levelCount);
                    startActivity(intent);

                }

                surfHolder.unlockCanvasAndPost(canvas);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            switch (event.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    paused = false;
                    if(ballStopped){
                        startTime = System.currentTimeMillis();
                    }
                    if(event.getX() > screenX/2){
                        ballStopped = false;

                    }
                    else{
                        ballStopped = false;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    paddle.setMoving(paddle.stop);
                    break;
            }

            return true;
        }

        // zastavení hry
        public void pause()
        {
            play = false;
            try{
                gameThread.join();
            }catch (InterruptedException e){
                Log.d("Error:","Thread error");
            }
        }

        // pokračování ve hře
        public void resume()
        {
            play = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            if((event.values[1] >=-1 && event.values[1] <= 1) || (paddle.getPaddle().right > screenX -50) || (paddle.getPaddle().left < 50))
            {
                angle = 1;
                paddle.setMoving(paddle.stop);
            }

            if(event.values[1] < -1 && paddle.getPaddle().left > 10)
            {
                paddle.setMoving(paddle.left);
                angle = event.values[1];
                Log.d("Angle",Float.toString(event.values[1]));

            }

            if(event.values[1] > 1 && (paddle.getPaddle().right < screenX -10 ))
            {
                paddle.setMoving(paddle.right);
                angle = event.values[1];
                Log.d("Angle",Float.toString(event.values[1]));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public void playBounceSound()
    {
        soundPool.play(bounce, 1, 1, 0, 0, 1);
    //    soundPool.autoPause();
    }

    public void playDeadEndSound()
    {
        soundPool.play(deadend, 1, 1, 0, 0, 1);
       // soundPool.autoPause();
    }

    public void playDestructionSound()
    {
        soundPool.play(destruction, 1, 1, 0, 0, 1);
       // soundPool.autoPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        arkanoidView.resume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        continuePref.edit().putFloat("Ball_left",ball.getBall().left).apply();
        continuePref.edit().putFloat("Ball_right",ball.getBall().right).apply();
        continuePref.edit().putFloat("Ball_top",ball.getBall().top).apply();
        continuePref.edit().putFloat("Ball_bottom",ball.getBall().bottom).apply();

        continuePref.edit().putFloat("Ball_velocity_x",ball.getxVelocity()).apply();
        continuePref.edit().putFloat("Ball_velocity_y",ball.getyVelocity()).apply();

        continuePref.edit().putFloat("Paddle_left",paddle.getPaddle().left).apply();
        continuePref.edit().putFloat("Paddle_right",paddle.getPaddle().right).apply();
        continuePref.edit().putFloat("Paddle_top",paddle.getPaddle().top).apply();
        continuePref.edit().putFloat("Paddle_bottom",paddle.getPaddle().bottom).apply();

        continuePref.edit().putInt("Lives",lives).apply();
        continuePref.edit().putString("Level_map",current_level_map).apply();
        continuePref.edit().putInt("Current_level",selectedLevel).apply();
        continuePref.edit().putInt("Score",score).apply();

        Log.d("SHARED","pause");
        arkanoidView.pause();
    }
}
