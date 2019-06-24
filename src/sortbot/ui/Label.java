package sortbot.ui.Label;

public class Label extends Node {

    private String content

    public void Label(String s) {
        content = s;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String s) {
        content = s; 
    }

    @Override
    public void draw(int x, int y, int w, int h) {
        String s = content.substring(0, h);
        LCD.drawString(content, x, y);
    }

}
