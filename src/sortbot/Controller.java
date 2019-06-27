package sortbot;

import lejos.util.Delay;
import lejos.nxt.*;
import java.util.Arrays;

public class Controller {

    SortBot bot;
    public String[] tests = {"Init", "take & drop", "readColor", "move across", "move(1)",
        "swap(2, 4)"};

    public Controller(SortBot bot) {
        this.bot = bot;
    }

    public void execTest(int test, UI ui) {
        switch(test) {
            case 0:
                bot.init();
                break;
            case 1:
                for (int i = 0; i < 6; ++i) {
                    bot.takeCube(1, i);
                    bot.dropCube(1, i);
                    bot.init();
                }
                break;
            case 2:
                bot.readColors();
                int[] colors = bot.getCellColors();
                String s = String.valueOf(colors[0]) + " " +
                    colors[1] + " " +
                    colors[2] + " " +
                    colors[3] + " " +
                    colors[4] + " " +
                    colors[5];
                ui.printTestOutput(s);
                break;
            case 3:
                bot.init();
                bot.baseToggle();
                Button.ENTER.waitForPress();
                bot.move(4);
                Button.ENTER.waitForPress();
                bot.baseToggle();
                bot.init();
                break;
            case 4:
                bot.move(1);
                break;
            case 5:
                bot.swap(2, 4);
                break;
        }
    }

    public void bubbleSort() {
        bot.init(); 
        bot.readColors();
        bot.move(6);
        bot.move(-6);
        while (!bot.isSorted()) {
            int[] colors = bot.getCellColors();
            for (int i = 0; i < colors.length - 1; ++i) {
                if (colors[i] > colors[i+1]) {
                    bot.swap(i, i+1); 
                }
            }
            // se mueve el robot por todo el tablero
            // como si este estuviese chequeando el orden
            bot.move(6);
            bot.move(-6);
        }
    }

    public void insertionSort() {
        bot.init();
        bot.readColors();
        for (int i = 1; i < bot.getCellColors().length; ++i) {
            for (int j = i; j >= 1; --j) {
                if (bot.getColor(j) > bot.getColor(j-1)) {
                    break; 
                }
                bot.swap(j, j-1);
            }
        }
    }

}
