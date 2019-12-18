package com.example.arkanoid;

import android.graphics.RectF;

import java.util.Random;

public class Ball {
    private RectF ball;
    private float xVelocity, yVelocity;

    private float positionX, positionY;

    private int ballHeight, ballWidth;

    int xVel = 200;
    int yVel = -400;

    public Ball(int screenX, int screenY){
        ballHeight = 30;
        ballWidth = ballHeight;


        xVelocity = xVel;
        yVelocity = yVel;

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
        yVel = -yVel;
    }

    public void reverseXVelocity(){
        xVelocity = -xVelocity;
        xVel = -xVel;
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

    public void setPosition(int left, int right, int top, int bottom){
        ball.left = left;
        ball.right = right;
        ball.top = top;
        ball.bottom = bottom;
    }

    public void setxVelocity(float xVelocity){
        this.xVelocity = xVelocity;
    }

    public void setyVelocity(float yVelocity){

        //this.yVelocity = yVel - yVelocity;
        this.yVelocity = yVelocity;
    }

    public float getxVel() {
        return xVel;
    }

    public float getyVel(){

        return yVel;
    }

    public float getxVelocity() {
        return xVelocity;
    }

    public float getyVelocity() {
        return yVelocity;
    }

    public int getRadius(){
        return ballHeight / 2;
    }
}
