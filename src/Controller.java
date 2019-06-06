import lejos.util.Delay;
import lejos.nxt.*;
import java.util.Arrays;

public class Controller {

    SortBot bot;
    public String[] tests = {"Init", "move(1) x 6", "readColor", "moveTo(3)", "baseToggle",
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
                    bot.move(1) ;
                    Delay.msDelay(300);
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
                bot.moveTo(3);
                break;
            case 4:
                bot.baseToggle();
                break;
            case 5:
                bot.swap(2, 4);
                break;
        }
    }

}
