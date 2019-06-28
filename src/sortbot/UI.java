package sortbot;

import lejos.util.Delay;
import lejos.nxt.*;
import sortbot.ui.Node;
import sortbot.ui.Menu;
import sortbot.ui.NodeEngine;
import sortbot.ui.Label;
import sortbot.ui.Bar;

public class UI {

    SortBot bot;
    Controller control;

    public UI(SortBot bot, Controller controller) {
        this.bot = bot;
        this.control = controller;
    }

    public void start() {
        NodeEngine.run(new MenuPrincipal());
    }

    class MenuPrincipal extends Menu {
        @Override
        public String[] getOptions() {
            String[] ops = {"Algoritmos", "Test", "Calibrar"};
            return ops;
        }
        @Override
        public void optionSelected(int i) {
            switch (i) {
                case 0:
                    NodeEngine.addNode(new MenuAlgoritmos());
                    break;
                case 1:
                    NodeEngine.addNode(new MenuTest());
                    break;
                case 2:
                    bot.init();
                    bot.calibrateColors();
                    break;
            }
        }
    }

    class MenuAlgoritmos extends Menu {
        @Override
        public String[] getOptions() {
            String[] r = {"Insertion Sort", "Bubble Sort"};
            return r;
        }
        @Override
        public void optionSelected(int i) {
            switch (i) {
                case 0:
                    control.insertionSort();
                    break;
                case 1:
                    control.bubbleSort();
                    break;
            }
        }
    }

    class MenuTest extends Menu {
        @Override
        public String[] getOptions() {
            return control.tests;
        }
        @Override
        public void optionSelected(int i) {
            control.execTest(i, UI.this);
        }
    }

    public void printTestOutput(String s) {
        NodeEngine.addNode(new Label(s));
    }

}

