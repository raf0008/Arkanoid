package com.example.arkanoid;

import android.graphics.RectF;

public class Brick {

    private RectF brick;
    private boolean isVisible;
    private int lives = 1;

    public Brick(int row, int column, int width, int height, int lifeCount){
        isVisible = true;
        int padding = 10;
        brick = new RectF(column * width + padding,row * height + padding, column * width + width - padding, row * height + height - padding);

        lives = lifeCount;
    }

    public RectF getBrick() {return this.brick;}

    public void setInvisible(){isVisible = false;}

    public boolean getVisibility() {return isVisible;}

    public int getLives(){return lives;}

    public void lostLife() {lives--;}
}
