package sortbot;

import lejos.util.Delay;
import lejos.nxt.*;
import java.util.Arrays;

public class Controller {

    SortBot bot;
    public String[] tests = {"Init", "moveTo", "readColor", "lightSensor", "move across", 
        "swap(2, 4)", "moveAcrossBlacks"};

    public Controller(SortBot bot) {
        this.bot = bot;
    }

    public void execTest(int test, UI ui) {
        switch(test) {
            case 0:
				bot.init();
                break;
			/* old: take and drop
            case 1:
                bot.init();
                for (int i = 0; i < 6; ++i) {
                    bot.takeCube(1, i);
                    bot.dropCube(1, i);
                }
                break;
			*/
			case 1:
				bot.init();
				int pos = 1;
				while(!Button.ESCAPE.isDown()){
					LCD.clear();
					LCD.drawInt(pos, 0, 0);
					LCD.drawInt(bot.curCell, 0, 3);
					if(Button.RIGHT.isDown()){
						pos = Math.min(6, pos+1);
						while(Button.RIGHT.isDown()){
						}
					}
					if(Button.LEFT.isDown()){
						pos = Math.max(0, pos-1);
						while(Button.LEFT.isDown()){
						}
					}
					if(Button.ENTER.isDown()){
						bot.moveTo(pos);
						while(Button.ENTER.isDown()){
						}
					}
				}
				break;
			/* old: color test
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
			*/
			case 3:
				int detecciones = 0;
				int dir = 0;
				int velDir = 1;
				int vel = bot.stepVel;
				boolean caca = false;
				boolean white = false;
                while (true) {
					bot.rail.setSpeed(vel);
					LCD.clear();
					LCD.drawInt(bot.getBarValue(), 0, 0);
					LCD.drawInt(vel, 0, 2);
					LCD.drawInt(detecciones, 0, 4);
					if (white != bot.onWhiteBar()) {
						white = bot.onWhiteBar();
						if (white == true) {
							detecciones += 1;
						}
					}
					if(Button.ESCAPE.isDown()){
						detecciones = 0;
					}
					if(Button.RIGHT.isDown()){
						dir = Math.min(dir+1, 1);
					}
					if(Button.LEFT.isDown()){
						dir = Math.max(dir-1, -1);
					}
					if(Button.ENTER.isDown()){
						vel += velDir*50;
						if(vel == 400){
							velDir*=-1;
						}
						if(vel == 50){
							velDir*=-1;
						}
					}
					if(dir == 1){
						bot.rail.backward();
					}
					else if(dir == -1){
						bot.rail.forward();
					}
					else{
						bot.rail.stop();
					}
					if(caca){
						break;
					}
                }
                break;
			
            case 4:
                bot.init();
                for (int i = 0; i <= 6; ++i) {
                    bot.moveTo(i);
                    Delay.msDelay(500);
                };
                for (int i = 6; i >= 0; --i) {
					bot.moveTo(i);
                    Delay.msDelay(500);
                }
                bot.init();
                break;
            
            case 5:
				bot.init();
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
