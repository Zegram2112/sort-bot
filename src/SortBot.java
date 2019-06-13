import lejos.nxt.*;
import lejos.nxt.addon.ColorHTSensor;
import lejos.util.Delay;
import lejos.robotics.Color;

public class SortBot { 

    public int stepAngle = 66;
    public int stepVel = 300;
    public int baseTime = 500;

    public int takeTime = 200;

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
        colorSensor = new ColorHTSensor(SensorPort.S1);
        colorMapper = new ColorMapper();

        base.setSpeed(200);
    }

    public void init() {
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
        move((float) steps);
        curCell = Math.max(Math.min(6, curCell + steps), 0);
    }

    private void move(float steps) {
        rail.setSpeed(stepVel);
        rail.rotate((int) (-steps * stepAngle));
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
        if (!baseRetracted) {
            baseToggle();
        }
        for (int i = 0; i < 6; ++i) {
            moveTo(i+1);
            cellColors[i] = colorMapper.getId(
                    colorSensor.getColor());
            Delay.msDelay(300);
        }
        baseToggle();
        move(-6);
    }

    public void calibrateColors() {
        if (!baseRetracted) {
            baseToggle();
        }
        Color[] colors = new Color[6];
        for (int i = 0; i < 6; ++i) {
            moveTo(i+1);
            colors[i] = colorSensor.getColor();
            Delay.msDelay(300);
        }
        colorMapper.setColors(colors);
        baseToggle();
        move(-6);
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
        move(-0.5f);
        head.rotate(90 * slot);
        baseToggle();
        Delay.msDelay(takeTime);
        if (drop) {
            move(0.5f); 
            baseToggle();
            move(-0.5f);
        } else {
            baseToggle();
        }
        head.rotate(-90 * slot);
        move(0.5f);
    }

    public void swap(int posA, int posB) {
        takeCube(-1, posA);
        takeCube(1, posB);
        dropCube(-1, posB);
        dropCube(1, posA);
        move(-7);
    }

}

