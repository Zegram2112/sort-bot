package sortbot.ui;

import lejos.nxt.*;
import lejos.util.Delay;
import java.util.Stack;

public class NodeEngine {

    public static final SCREEN_WIDTH = 16;
    public static final SCREEN_HEIGHT = 10;
    public static final REFRESH_RATE = 200;
    private static Stack<Node> screens;

    public static void run(Node root) {
        screens = new Stack<Node>();
        screens.push(root);
        while (!screens.empty()) {
            handleInput();
            draw();
            Delay.msDelay(REFRESH_RATE);            
        }
    }

    public static void addNode(Node node) {
        screens.push(node);
    }

    private static void draw() {
        screens.peek().draw(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private static void handleInput() {
        int input = Button.readButtons();
        if (input == 0) {
            return; 
        }
        Button.waitForAnyPressAndRelease();
        if (input == Button.ID_ESCAPE) {
            screens.pop(); 
        } else {
            screens.peek().handleInput(input);
        }
    }

}
