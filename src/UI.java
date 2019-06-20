import lejos.util.Delay;
import lejos.nxt.*;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;

public class UI {

    SortBot bot;
    Controller control;

    public UI(SortBot bot, Controller controller) {
        this.bot = bot;
        this.control = controller;
    }

    public void start() {
        String[] options = {"Algoritmos", "Configuracion", "Test", "Calibrar colores"};
        int ans;
        do {
            LCD.clear();
            ans = menu(options, 1);
            switch (ans) {
                case 0:
                    selectAlgorithmScreen();
                    break;
                case 1:
                    setupScreen();
                    break;
                case 2:
                    testScreen();
                    break;
                case 3:
                    bot.init();
                    bot.calibrateColors();
                    break;
            }
        } while (ans != -1);
    }

    private void setupScreen() {
        String[] options = {"Step vel", "Step angle"};
        int ans, ansBar;
        do {
            LCD.clear();
            ans = menu(options, 1);
            switch (ans) {
                case 0:
                    ansBar = bar(100, 800, 7, bot.stepVel, 6);
                    if (ansBar != -1) bot.stepVel = ansBar;
                    break;
                case 1:
                    ansBar = bar(60, 70, 10, (int) bot.stepAngle, 6);
                    if (ansBar != -1) bot.stepAngle = ansBar;
                    break;
            }
        } while (ans != -1);
    }

    private void testScreen() {
        int ans;
        do {
            LCD.clear();
            String[] options = control.tests;
            ans = menu(options, 1);
            control.execTest(ans, this);
        } while (ans != -1);
    }

    public void printTestOutput(String s) {
        while (!Button.ENTER.isPressed()) {
            LCD.drawString(s, 1, 7);
        }
        Button.ENTER.waitForPressAndRelease();
    }

    private void selectAlgorithmScreen() {
        int ans;
        do {
            LCD.clear();
            String[] options = {"InsertionSort", "MergeSort", "BubbleSort"};
            ans = menu(options,1);
            switch (ans) {
                case 0:
                    control.insertionSort();
                    break;
                case 1:
                    break;
                case 2:
                    control.bubbleSort();
                    break;
            }
        } while (ans != -1);
    }

    private int menu(String[] options, int p) {
        int ans = 0;
        for (int i = 0; i < options.length; ++i) {
            option(options[i], i, p);
        }
        while (!Button.ENTER.isPressed()) {
            for (int i = 0; i < options.length; ++i) {
                LCD.drawChar(' ', 1, p + i);
            }
            LCD.drawString(">", 1, ans+p);
            if (Button.LEFT.isPressed()) {
                ans = Math.max(0, ans - 1);
            } else if (Button.RIGHT.isPressed()) {
                ans = Math.min(ans+1, options.length-1);
            } else if (Button.ESCAPE.isPressed()) {
                Button.ESCAPE.waitForPressAndRelease();
                return -1;
            }
            Delay.msDelay(200); 
        }
        Button.ENTER.waitForPressAndRelease();
        return ans;
    }

    private int bar(int left, int right, int n, int def, int p) {
        int ans = def;
        int step = (right - left) / n;

        while (!Button.ENTER.isPressed()) {
            int curSquare = (ans - left)/step;
            for (int i = 0; i < n; ++i) {
                char c = i <= curSquare ? '*':'_';
                LCD.drawChar(c, 1 + i, p);
            }
            LCD.drawInt(ans, 8 - (int)(Math.log(ans)/Math.log(10)+1)/2, p+1);
            if (Button.LEFT.isPressed()) {
                ans = Math.max(left, ans - step);
            } else if (Button.RIGHT.isPressed()) {
                ans = Math.min(ans + step, right);
            } else if (Button.ESCAPE.isPressed()) {
                Button.ESCAPE.waitForPressAndRelease();
                clearLine(1, 14, p);
                clearLine(1, 14, p+1);
                return -1;
            }
            Delay.msDelay(200);
        }
        Button.ENTER.waitForPressAndRelease();
        clearLine(1, 14, p);
        clearLine(1, 14, p+1);
        return ans;
    }

    private void clearLine(int start, int end, int p) {
        for (int i = start; i <= end; ++i) {
            LCD.drawChar(' ', i, p);
        }
    }

    private void option(String s, int p, int initp) {
        LCD.drawString(String.valueOf(p) + ". " + s, 2, p+initp); 
    }

}

interface Drawable {

        public void draw();

}

class Node implements Drawable {

    private Map<String, Node> childs;

    public Node() {
        childs = new HashMap<String, Node>(); 
    }

    public void addChild(Node node, String name) {
        childs.put(name, node);
    }

    public Node getChild(String name) {
        return childs.get(name);
    }

    public Collection<Node> getChilds() {
        return childs.values();
    }

    public void draw() {
        for (Node node : getChilds()) {
            node.draw();
        }
    }

}
