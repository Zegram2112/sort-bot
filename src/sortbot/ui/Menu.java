package sortbot.ui;

import lejos.nxt.*;

public abstract class Menu extends Node {
    private int cursor = 0;

    abstract public String[] getOptions();
    abstract public void optionSelected(int i);

    @Override
    public void draw(int x, int y, int w, int h) {
        LCD.drawString(">", x, y + cursor);
        String s;
        for (int i = 0; i < Math.min(getOptions().length, h); ++i) {
            s = getOptions()[i];
            LCD.drawString(String.valueOf(i+1) + ". " + s, x + 1, y + i); 
        }
    }

    @Override
    public void handleInput(int input) {
        switch (input) {
            case Button.ID_LEFT:
                cursor = Math.max(0, cursor - 1);
                break;
            case Button.ID_RIGHT:
                cursor += Math.min(cursor + 1, getOptions().length);
                break;
            case Button.ID_ENTER:
                optionSelected(cursor);
                break;
        }
    }
}

