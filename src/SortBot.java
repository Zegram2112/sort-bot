import lejos.nxt.*;
import lejos.util.Delay;

public class SortBot { 

    public int stepAngle = 66;
    public int stepVel = 300;
    public int baseTime = 600;

    NXTRegulatedMotor rail;
    NXTRegulatedMotor base;
    NXTRegulatedMotor head;
    ColorSensor colorSensor;
    int curCell;
    boolean baseRetracted;
    int[] cellColors = new int[6];

    public SortBot() {
        rail = Motor.A;
        base = Motor.B;
        head = Motor.C;
        colorSensor = new ColorSensor(SensorPort.S1);
        base.setSpeed(200);
    }

    public void init() {
        curCell = 0;
        move(-6); 
        baseRetracted = true;
        base.backward();
        Delay.msDelay(baseTime);
        base.stop();
    }

    public static void main (String[] args) {
        SortBot bot = new SortBot();;
        Controller control = new Controller(bot);
        UI ui = new UI(bot, control); 
        ui.start();
    }

    /**
     * Mueve el robot en una dirección dada avanzando una cantidad dada
     * de celdas
     **/
    public void move(int steps) {
        rail.setSpeed(stepVel);
        rail.rotate(-steps * stepAngle);
        curCell = Math.max(Math.min(6, curCell + steps), 0);
    }

    /** 
     * Mueve el robot a una celda
     **/
    public void moveTo(int cell) {
        int steps = cell-curCell;
        move(steps);
    }

    /**
     * Cambia el estado de la base, empujandola hacia afuera
     * o hacia adentro
     **/
    public void baseToggle() {
        if (baseRetracted) {
            base.forward();
        } else {
            base.backward();
        }
        baseRetracted = !baseRetracted;
        Delay.msDelay(baseTime);
        base.stop();
    }

    /**
     * Lee el color del bloque que esté abajo y lo retorna
     **/
    public int getColorID() {
        return colorSensor.getColorID();
    }

    public void readColors() {
        for (int i = 0; i < 6; ++i) {
            moveTo(i+1);
            cellColors[i] = colorSensor.getColorID();
            Delay.msDelay(300);
        }
    }

    public int[] getCellColors() {
        return cellColors;
    }


}

