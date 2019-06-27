package sortbot;

import lejos.nxt.*;
import lejos.nxt.addon.ColorHTSensor;
import lejos.util.Delay;
import lejos.robotics.Color;

public class SortBot { 

    public int stepAngle = 66;
    public int stepVel = 300;
    public int baseTime = 800;

    // stepError fue calculado en base al movimiento del SortBot
    // utilizando un stepAngle de 67 y 68.
    // Corresponde a cuanto error (en grados) acumula
    // cada vez que se mueve un paso
    // Un sistema ideal se movería exactamente un bloque, pero
    // como el robot no es 100% preciso este se desfasa
    // cada movimiento.
    float accumulatedError = 0.0f;
    float stepError = 0.5f;

    NXTRegulatedMotor rail;
    NXTRegulatedMotor base;
    NXTRegulatedMotor head;
    ColorHTSensor colorSensor;
    ColorMapper colorMapper;

    int curCell;
    boolean baseRetracted;
    int[] cellColors = new int[6];
    int[] slots = {-1, -1};

    public SortBot() {
        rail = Motor.A;
        base = Motor.B;
        head = Motor.C;
        colorSensor = new ColorHTSensor(SensorPort.S4);
        colorMapper = new ColorMapper();

        base.setSpeed(200);
    }

    public void init() {
        accumulatedError = 0;
        curCell = 0;
        move(-6); 
        baseRetracted = false;
    }

    /**
     * Lee el color del bloque que esté abajo y lo retorna
     **/
    public int getColorID() {
        return colorSensor.getColorID();
    }

    /**
     * Retorna los colores de cada una de las celdas
     * */
    public int[] getCellColors() {
        return cellColors;
    }
    
    /**
     * Retorna el color en la celda de un index dado
     * */
    public int getColor(int index) {
        return cellColors[index];
    }

    /**
     * Retorna true si los cubos están ordenados
     * */
    public boolean isSorted() {
        for (int i = 0; i < cellColors.length - 1; ++i) {
            if (cellColors[i] > cellColors[i+1]) {
                return false;
            }
        }
        return true;
    }

    public static void main (String[] args) {
        SortBot bot = new SortBot();;
        Controller control = new Controller(bot);
        UI ui = new UI(bot, control); 
        ui.start();
    }

    // movimientos bácos

    /**
     * Mueve el robot en una dirección dada avanzando una cantidad dada
     * de celdas
     **/
    public void move(int steps) {
        move((float) steps, stepVel, true);
        curCell = Math.max(Math.min(6, curCell + steps), 0);
    }

    private void move(float steps) {
        move(steps, stepVel);
    }

    private void move(float steps, int stepVel) {
        move(steps, stepVel, false);
    }

    private void move(float steps, int stepVel, boolean checkError) {
        rail.setSpeed(stepVel);
        if (!checkError) {
            rail.rotate((int)(-steps * stepAngle));
            return;
        }
        int correction = 0;
        accumulatedError += -steps * stepError;
        if (accumulatedError > 1 || accumulatedError < -1) {
            correction = -((int) accumulatedError);    
            accumulatedError += correction;
        }
        rail.rotate(((int)(-steps * stepAngle)) - correction);
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

    // movimientos compuestos

    /**
     * Recorre el carril leyendo cada uno de los colores
     * en las celdas, los cuales pueden ser obtenidos con getCellColors()
     **/
    public void readColors() {
        move(0.3f);
        Delay.msDelay(300);
        for (int i = 0; i < 6; ++i) {
            moveTo(i+1);
            cellColors[i] = colorMapper.getId(
                    colorSensor.getColor());
            Delay.msDelay(100);
        }
        move(-7);
    }

    public void calibrateColors() {
        Color[] colors = new Color[6];
        move(0.3f);
        Delay.msDelay(300);
        for (int i = 0; i < 6; ++i) {
            moveTo(i+1);
            colors[i] = colorSensor.getColor();
            Delay.msDelay(100);
        }
        colorMapper.setColors(colors);
        move(-7);
    }

    public void takeCube(int slot, int pos) {
        cubeAct(slot, pos, false);
        slots[slotIdToIndex(slot)] = cellColors[pos];
        cellColors[pos] = -1;
    }

    public void dropCube(int slot, int pos) {
        cubeAct(slot, pos, true);
        cellColors[pos] = slots[slotIdToIndex(slot)];
        slots[slotIdToIndex(slot)] = -1;
    }

    private int slotIdToIndex(int id) {
        return (id == -1 ? 0 : 1);
    }

    private void cubeAct(int slot, int pos, boolean drop) {
        if (baseRetracted) {
            baseToggle();
        }
        moveTo(pos);
        head.rotate(90 * slot);
        baseToggle();
        Delay.msDelay(200);
        if (drop){
            float step;
            switch (pos) {
                case 4:
                    step = 0.55f;
                case 5:
                    step = 0.65f;
                default:
                    step = 0.5f;
            }
            move(step, stepVel/2); 
            baseToggle();
            move(-step);
        } else {
            baseToggle();
        }
        head.rotate(-90 * slot);
    }

    public void swap(int posA, int posB) {
        takeCube(-1, posA);
        takeCube(1, posB);
        dropCube(-1, posB);
        dropCube(1, posA);
        move(-7);
    }

}

