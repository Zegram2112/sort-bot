package sortbot;

import lejos.robotics.Color;


public class ColorMapper {

    Color[] colors = {
        new Color(11, 10, 10), new Color(15, 36, 24), new Color(31, 5, 10),
        new Color(9, 12, 17), new Color(61, 11, 51), new Color(60, 50, 59)
    }; // colores por defecto

    public ColorMapper() {
    }

    public void setColors(Color[] colors) {
        this.colors = colors;    
    }

    public int getId(Color color) {
        int min = 255 * 3; 
        int id = -1;
        for (int i = 0; i < colors.length; ++i) {
            int diff = ColorUtils.difference(color, colors[i]);
            if (diff < min) {
                min = diff;
                id = i;
            }
        }
        return id;
    }

}

class ColorUtils {

    public static int norm(Color c) {
        return (c.getRed() + c.getGreen() + c.getBlue());
    }

    public static Color colorDifference(Color a, Color b) {
        return new Color(Math.abs(a.getRed() - b.getRed()),
                Math.abs(a.getGreen() - b.getGreen()),
                Math.abs(a.getBlue() - b.getBlue()));
    }

    public static int difference(Color a, Color b) {
        return norm(colorDifference(a, b));
    }

}
