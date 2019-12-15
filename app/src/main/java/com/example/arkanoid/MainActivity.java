package com.example.arkanoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SoundPool soundPool;
    int bounce;
    int deadend;
    int destruction;

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

        // textView = (TextView)findViewById(R.id.text);
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

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

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

        int lives = 3;
        int score = 0;

        Paddle paddle;
        Ball ball;

        Brick[] bricks = new Brick[100];
        int brickCount = 0;
        int brickLifeCount = 0;

        //private boolean ballStopped = true;

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

        //    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       //     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener((SensorEventListener) ArkanoidView.this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

            //play = false;
            paused = true;



        //    background = BitmapFactory.decodeResource(getResources(), R.drawable.space);
           // background = Bitmap.createScaledBitmap(bitmap, screenX, screenY, true);

            surfHolder = getHolder();
            paint = new Paint();

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.space);
            background = Bitmap.createScaledBitmap(bitmap, screenX, screenY, true);

         /*   BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.space, options);

            options.inSampleSize = calculateInSampleSize(options, screenX,screenY);
            options.inJustDecodeBounds = false;
            background = BitmapFactory.decodeResource(getResources(), R.drawable.space, options);*/


            brickWidth = screenX / 8;
            brickHeight = screenY / 10;

            paddle = new Paddle(screenX, screenY);
            ball = new Ball(screenX, screenY);

            createBricksAndRestart();

            draw();
        }

        public void createBricksAndRestart() {
            ball.reset(screenX, screenY);
            int width = screenX / 10;
            int height = screenY / 12;

            Random rand = new Random();
            int life;

            brickCount = 0;
            for (int column = 0; column < 10; column++) {
                for (int row = 0; row < 5; row++) {
                    life = rand.nextInt(3)-1; //5 +1
                  //  life = 5 - row;
                    if(life < 0) life = 0;
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

            for (int i = 0; i < brickCount; i++) {
                if (bricks[i].getVisibility()) {
                    if (RectF.intersects(bricks[i].getBrick(), ball.getBall())) {
                        playBounceSound();
                        if(bricks[i].getLives() == 1) bricks[i].setInvisible();
                        else bricks[i].lostLife();

                        ball.reverseYVelocity();
                        score = score + 1;

                        if(score == brickLifeCount){
                            paused = true;
                            paddle.reset(screenX,screenY);
                            setContentView(R.layout.activity_main);
                        }
                       /* if (score == 4 || score == 48 || score == 72 || score == 96 || score == 120 || score == 144 || score == 168 || score == 192 || score == 216)
                        {
                            lives = lives + 2;
                            paused = true;
                            createBricksAndRestart();
                            paddle.reset(screenX,screenY);
                        }*/
                    }
                }
            }

            // Kontrola kdy se míč dotkne paddle
            if (RectF.intersects(paddle.getPaddle(), ball.getBall())) {
                playBounceSound();
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                //ball.BallDirection(ball,paddle);
                ball.clearObstacleY(paddle.getPaddle().top - 2);
            }

           /* if (RectF.intersects(obstruction.getRect(), ball.getRect())) {
                //ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.BallDirection(ball,obstruction);
                ball.clearObstacleY(obstruction.getRect().top - 2);
            }*/


            // Pokud se míč dotkne země
            if (ball.getBall().bottom > screenY) {
                playDestructionSound();
                paused = true;
                // hráč ztratí život, hra se stopne a obnoví se pozice míčku a paddlu
                lives--;
                ball.reset(screenX,screenY);
                paddle.reset(screenX,screenY);

                // pokud jsou životy rovny 0 tak se hra stopne a obnoví se vše.
                if (lives == 0) {
                    paused = true;

                    String Hscore = Integer.toString(score);
                    Intent intent = new Intent(getContext(), MainMenu.class);
                    intent.putExtra("highscore", Hscore);
                    startActivity(intent);
                }


            }

            // vrchní zeď odraz
            if (ball.getBall().top < 0)
            {
                playBounceSound();
                ball.reverseYVelocity();
                ball.clearObstacleY(24);

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
                canvas.drawText("Score: " + score + "   Lives: " + lives, 10, screenY - 50, paint);

                // Pokud hráč vyhrál ( zničil všechny kostky )
                if (score == brickLifeCount * 10) {
                    paint.setTextSize(90);
                    canvas.drawText("You Win!", 10, screenY / 2, paint);
                }

                // Pokud hráč prohrál
                if (lives <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("You loose!", 10, screenY / 2, paint);
                }

                surfHolder.unlockCanvasAndPost(canvas);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            switch (event.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    paused = false;
                    if(event.getX() > screenX/2){
                        ballStopped = false;
                        paddle.setMoving(paddle.right);
                    }
                    else{
                        ballStopped = false;
                        paddle.setMoving(paddle.left);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    paddle.setMoving(paddle.stop);
                    break;
            }

            return true;
        }

        /*@Override
        public boolean onTouchEvent(MotionEvent event) {

            switch (event.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    paused = false;
                    if(!touched) {
                        touched = true;
                        ballStopped = false;
                        paddle.setMoving(paddle.left);
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }


                resume();

            return true;
        }*/

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
                paddle.setMoving(paddle.stop);
            }

            /*if(event.values[1] > 1 && paddle.getPaddle().right > 50)
            {
                paddle.setMoving(paddle.right);
            }

            if(event.values[1] < -1 && (paddle.getPaddle().right > screenX -50 ))
            {
                paddle.setMoving(paddle.left);
            }*/

            if(event.values[1] < -1 && paddle.getPaddle().left > 10)
            {
                paddle.setMoving(paddle.left);
            }

            if(event.values[1] > 1 && (paddle.getPaddle().right < screenX -10 ))
            {
                paddle.setMoving(paddle.right);
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
        arkanoidView.pause();
    }
}
