package sortbot;

import lejos.util.Delay;
import lejos.nxt.*;
import java.util.Arrays;

public class Controller {

    SortBot bot;
    public String[] tests = {"Init", "take & drop", "readColor", "move across", "lightSensor",
        "swap(2, 4)", "moveAcrossBlacks"};

    public Controller(SortBot bot) {
        this.bot = bot;
    }

    public void execTest(int test, UI ui) {
        switch(test) {
            case 0:
                bot.init();
                break;
            case 1:
                bot.init();
                for (int i = 0; i < 6; ++i) {
                    bot.takeCube(1, i);
                    bot.dropCube(1, i);
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
                for (int i = 0; i <= 6; ++i) {
                    bot.moveTo(i);
                    Delay.msDelay(500);
                };
                for (int i = 6; i >= 0; --i) {

                }
                bot.init();
                break;
            case 4:
                while (true) {
                    LCD.clear();
                    int color = bot.getBarValue();
                    LCD.drawInt(color, 0, 0); 
                    Delay.msDelay(100);
                    if (Button.ESCAPE.isDown()) {
                        break;
                    }
                }
                break;
            case 5:
                bot.swap(2, 4);
                break;
            case 6:
                bot.init();
                while (true) {
                    Motor.A.backward();
                    while (!bot.onBlackBar()) {
                    }
                    Motor.A.stop();

                    if (Button.ESCAPE.isDown()) break;
                    Button.ENTER.waitForPressAndRelease();

                    Motor.A.backward();
                    while (bot.onBlackBar()) {
                
                    }
                }
                break;
        }
    }

    public void bubbleSort() {
        bot.init(); 
        bot.readColors();
        bot.moveTo(6);
        bot.moveTo(0);
        while (!bot.isSorted()) {
            int[] colors = bot.getCellColors();
            for (int i = 0; i < colors.length - 1; ++i) {
                if (colors[i] > colors[i+1]) {
                    bot.swap(i, i+1); 
                }
            }
            // se mueve el robot por todo el tablero
            // como si este estuviese chequeando el orden
            bot.moveTo(6);
            bot.moveTo(0);
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
