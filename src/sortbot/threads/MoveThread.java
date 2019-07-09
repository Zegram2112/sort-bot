package sortbot.threads;
import sortbot.SortBot;

public class MoveThread extends Thread{
	int steps;
	SortBot bot;
	
	public MoveThread(int steps, SortBot bot){
		this.steps = steps;
		this.bot = bot;
	}
	
	
	public void run(){
		if (steps == 0) return;
        int targetSteps = Math.abs(steps); //cantidad de pasos a dar
        int curSteps = 0; //pasos que lleva, se cuenta por muralla
        int correction; //corrección a hacer (mejora de precisión)
        int targetCell = bot.curCell + steps;
        int dir;
        boolean white = false; //indica si sensor está en linea blanca
        bot.rail.setSpeed(bot.stepVel);
        if (steps > 0) {
            bot.rail.backward();
            correction = -bot.barToCenterBackward;
            dir = 1;
        } else {
            bot.rail.forward();
            correction = bot.barToCenterForward;
            dir = -1;
        }

        while (curSteps < targetSteps - 1) {
            if (white != bot.onWhiteBar()) {
                white = bot.onWhiteBar();
                if (white == true) {
                    curSteps += 1;
                    //bot.curCell += dir;
                }
            }
        }
        while(bot.onWhiteBar()){
            //wait to exit current bar
        }
        bot.rail.setSpeed(bot.dropVel);
        while(!bot.onTargetBar(targetCell, dir)){
            //wait
        }
        //finish movement
        bot.rail.stop();
        bot.rail.setSpeed(bot.stepVel);
        bot.rail.rotate(correction);
        bot.curCell = Math.max(Math.min(6, bot.curCell + steps), 0);
	}
    
}