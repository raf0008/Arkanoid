package com.example.arkanoid;

import android.graphics.RectF;

import java.util.Random;

public class Ball {
    private RectF ball;
    private float xVelocity, yVelocity;

    private float positionX, positionY;

    private int ballHeight, ballWidth;

    public Ball(int screenX, int screenY){
        ballHeight = 30;
        ballWidth = ballHeight;

        xVelocity = 200;
        yVelocity = -400;

        positionX = screenX/2;
        positionY = screenY - 80;

        ball = new RectF(positionX,positionY,positionX + ballHeight,positionY + ballWidth);
    }

    public RectF getBall(){return this.ball;}

    public void update(float fps){
        ball.left = ball.left + (xVelocity/fps);
        ball.top = ball.top + (yVelocity/fps);
        ball.right = ball.left + ballWidth;
        ball.bottom = ball.top + ballHeight;
    }

    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity(){
        xVelocity = -xVelocity;
    }

    public void setRandomXVelocity(){
        Random rand = new Random();
        int ans = rand.nextInt(2);

        if(ans == 0) {
           // if(xVelocity == -xVelocity){
           //     xVelocity = 100;
           // }
            reverseXVelocity();

        }
    }

    public void clearObstacleY(float y){
        ball.bottom = y;
        ball.top = y - ballHeight;
    }

    public void clearObstacleX(float x){
        ball.left = x;
        ball.right = x + ballWidth;
    }

    public void reset(int x, int y){
        //reverseXVelocity(); reverseYVelocity();
        ball.left = x / 2 ;
        ball.top = y - 40;
        ball.right = x / 2 + ballWidth;
        ball.bottom = y - 40 - ballHeight;
    }

    public int getRadius(){
        return ballHeight / 2;
    }
}