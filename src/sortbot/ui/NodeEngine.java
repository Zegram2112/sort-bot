package sortbot.ui;

import lejos.nxt.*;
import lejos.util.Delay;
import java.util.Stack;

public class NodeEngine {

    public static final int SCREEN_WIDTH = 16;
    public static final int SCREEN_HEIGHT = 10;
    public static final int REFRESH_RATE = 200;
    private static Stack<Node> screens;

    public static void run(Node root) {
        screens = new Stack<Node>();
        screens.push(root);
        while (!screens.empty()) {
            LCD.clear();
            handleInput();
            draw();
            Delay.msDelay(REFRESH_RATE);            
        }
    }

    public static void addNode(Node node) {
        screens.push(node);
    }

    public static void removeActiveNode() {
        screens.pop();
    }

    public static Node getActiveNode() {
        return screens.peek();
    }

    private static void draw() {
        screens.peek().draw(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private static void handleInput() {
        int input = Button.readButtons();
        if (input == 0) {
            return; 
        }
        waitForRelease();
        if (input == Button.ID_ESCAPE) {
            screens.pop(); 
        } else {
            screens.peek().handleInput(input);
        }
    }

    private static void waitForRelease() {
        while (Button.ENTER.isDown()
                || Button.LEFT.isDown()
                || Button.RIGHT.isDown()
                || Button.ESCAPE.isDown()) {
           Delay.msDelay(50);
        }
    }

}
