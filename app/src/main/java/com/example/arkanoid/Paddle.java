package com.example.arkanoid;

import android.graphics.RectF;

public class Paddle {
    private RectF paddle;

    private float positionX, positionY;
    private int screenX, screenY;

    private int length;
    private int height;

    private int moving;
    private int speed = 400;

    public final int stop = 0;
    public final int left = 1;
    public final int right = 2;

    public Paddle(int screenX, int screenY){
        this.length = 200;
        this.height = 40;

        this.screenX = screenX;
        this.screenY = screenY;

        this.positionX = (screenX/2)-(this.length/2);
        this.positionY = screenY - this.height;

        this.paddle = new RectF(positionX,positionY,positionX+length,positionY+height);
    }

    public RectF getPaddle(){return this.paddle;}

    public void setMoving(int moving){this.moving = moving;}

    public void update(float fps){
        if(moving == left){
            positionX = positionX - speed / fps;
        }
        if(moving == right){
            positionX = positionX + speed / fps;
        }

        paddle.left = positionX;
        paddle.right = positionX + length;
    }

    public void reset(int x, int y){
        this.screenX = x;
        this.screenY = y;

        this.positionX = (screenX/2)-(this.length/2);
        this.positionY = screenY - this.height;

        this.paddle = new RectF(positionX,positionY,positionX+length,positionY+height);

        paddle.left = positionX;
        paddle.top = positionY;
        paddle.right = positionX + length;
        paddle.bottom = positionY + height;
    }

    public void setPositionX(float positionX){
        this.positionX = positionX;
    }

    public void setPositionY(float positionY){
        this.positionY = positionY;
    }
}
