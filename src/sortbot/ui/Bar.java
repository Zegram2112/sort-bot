package sortbot.ui;

import lejos.nxt.*;


public abstract class Bar extends Node { 

    private int left;
    private int right;
    private int n;
    private int defaultVal;
    private int val;

    public Bar(int left, int right, int n, int def) {
        this.left = left;
        this.right = right;
        this.n = n;
        this.defaultVal = def;
    }

    public int getVal() {
        return val;
    }

    abstract public void onSelect(int val);

    @Override
    public void draw(int x, int y, int w, int h) {
        int curSquare = (val - defaultVal) / getStep();
        for (int i = 0; i < n; ++i) {
            char c = i <= curSquare ? '*' : '_';
            LCD.drawChar(c, x + i, y);
        }
        LCD.drawInt(val,
                x + (w/2) - (int)(Math.log(val)/Math.log(10)+1)/2,
                y + 1);
    }

    @Override
    public void handleInput(int i) {
        switch (i) {
            case Button.ID_LEFT:
                val = Math.max(left, val - getStep());
                break;
            case Button.ID_RIGHT:
                val = Math.min(val + getStep(), right);
                break;
            case Button.ID_ENTER:
                onSelect(val);
                destroy();
                break;
        }
    }

    private int getStep() {
        return (right - left) / n;
    }

}
