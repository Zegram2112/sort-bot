package sortbot.ui;

abstract class Menu extends Node {
    private cursor = 0;

    abstract public String[] getOptions();
    abstract public void optionSelected(int i);

    @Override
    public draw(int x, int y, int w, int h) {
        LCD.drawString(">", x, y + cursor);
        for (int i = 0; i < min(getOptions().length, h); ++i) {
            LCD.drawString(String.valueOf(i+1) + ". " + s, x + 1, y + i); 
        }
    }

    @Override
    public handleInput(int input) {
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

