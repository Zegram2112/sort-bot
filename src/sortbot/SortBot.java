package sortbot;

import lejos.nxt.*;
import lejos.nxt.addon.ColorHTSensor;
import lejos.util.Delay;
import lejos.robotics.Color;
import sortbot.threads.MoveThread;

public class SortBot {
    public int[] backwardCorrections = {0, 0, 0, 0, 0, 0, 0}; //corrección al avanzar
	public int[] forwardCorrections = {0, 0, 0 , 0, 0, 0, 0}; //corrección al retroceder
	public int[] dropCorrections = {35, 35, 35, 33, 37, 42, 0}; // rotaciones para dropear
    public int[] barLights = {508, 500, 507, 510, 510, 507, 507};
	//public int backwardCorrection = 0; // Moviendose izq a derecha (-20)
	//public int forwardCorrection = 0; // Moviendose derecha a izquierda (45)
    public int stepVel = 200; // recomendado 200
    public int dropVel = 50;
    public int baseTime = 800;

	public int barLight = 420; // Sensibilidad sensor moviendose rápido
    public int barLightsError = 10;
    public int barToCenterBackward = 28;
    public int barToCenterForward = 25;

    public NXTRegulatedMotor rail;
    public NXTRegulatedMotor base;
    public NXTRegulatedMotor head;
    public ColorHTSensor colorSensor;
    public ColorMapper colorMapper;
    public LightSensor lightSensor;
    public int curCell;

    boolean baseRetracted;
    int[] cellColors = new int[6];
    int[] slots = {-1, -1};

    public SortBot() {
        rail = Motor.A;
        base = Motor.B;
        head = Motor.C;
        colorSensor = new ColorHTSensor(SensorPort.S4);
        colorMapper = new ColorMapper();
        lightSensor = new LightSensor(SensorPort.S3);

        base.setSpeed(stepVel);
    }

    public void init() {
        curCell = 0;
        rail.setSpeed(stepVel);
        rail.forward();
        Delay.msDelay(3000);
        rail.stop();
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
	
	public void move(int steps, boolean immediateReturn){
		MoveThread T = new MoveThread(steps, this);
		T.start();
		if(!immediateReturn){
			try{
				T.join();
			}
			catch(Exception ex){
				
			}
		}
	}
	
	//actions: takeFront, putBack
	public void magnetAct(int slot, String action){
		magnetAct(slot, action, false);
	}
	public void magnetAct(int slot, String action, boolean immediateReturn){
		MagnetThread T = new MagnetThread(slot, action, this);
		if(!immediateReturn){
			try{
				T.join();
			}
			catch(Exception ex){
				
			}
		}
	}

	public boolean onWhiteBar(){
		int light = getBarValue();
        return light > barLight;
    }

    public boolean onBlackBar() {
        return !onWhiteBar();
    }

    public boolean onTargetBar(int targetCell, int dir){
        int light = getBarValue();
        dir = Math.max(dir, 0); // si es -1 se hace 0
        return light > barLights[targetCell - dir] - barLightsError;
    }

    public int getBarValue() {
        return lightSensor.readNormalizedValue(); 
    }

    /** 
     * Mueve el robot a una celda
     **/
	public void moveTo(int cell){
		moveTo(cell, false);
	}
    public void moveTo(int cell, boolean immediateReturn) {
        int steps = cell-curCell;
        move(steps, immediateReturn);
    }

    public void moveToNextWall(){
        rail.backward();
        rail.setSpeed(dropVel);
        while(!onTargetBar(curCell+1, 1)){}
        rail.stop();
        rail.setSpeed(stepVel);
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
     * Nota: Requiere calibración previa
     */
    public void readColors() {
        init();
        Delay.msDelay(300);
        for (int i = 0; i < 6; ++i) {
            moveTo(i+1);
            cellColors[i] = colorMapper.getId(
                    colorSensor.getColor());
            Delay.msDelay(100);
        }
        init();
    }
	
    public void calibrateColors() {
        Color[] colors = new Color[6];
        init();
        Delay.msDelay(300);
        for (int i = 0; i < 6; ++i) {
            moveTo(i+1);
            colors[i] = colorSensor.getColor();
            Delay.msDelay(100);
        }
        colorMapper.setColors(colors);
        init();
    }
	
	public void takeCube(int slot, int pos){
		if(pos == 0){
			// TODO
		}
		else{
			// TODO
		}
		cubeAct(slot, pos, false, "normal");
        slots[slotIdToIndex(slot)] = cellColors[pos];
        cellColors[pos] = -1;
	}

    public void dropCube(int slot, int pos) {
        cubeAct(slot, pos, true, "normal");
        cellColors[pos] = slots[slotIdToIndex(slot)];
        slots[slotIdToIndex(slot)] = -1;
    }
	
    private int slotIdToIndex(int id) {
        return (id == -1 ? 0 : 1);
    }
	
    private void cubeAct(int slot, int pos, boolean drop, String rotate) {
        if (baseRetracted) {
            baseToggle();
        }
		head.rotate(90*slot,true);
        moveTo(pos);
        //Delay.msDelay(200);
        baseToggle();
		//drop nuevo con rotaciones
		if(drop){
			moveToNextWall();
			baseToggle();
			rail.rotate(barToCenterForward);
		}
		/* old drop con sensor de luz
        if (drop){
            rail.setSpeed(dropVel);
            rail.backward();
            while (!onWhiteBar()){
                // moverse hasta la pared
            }
            rail.stop();
            baseToggle();
            rail.rotate(forwardCorrections[pos]);
            rail.setSpeed(stepVel);
		*/
        else {
            baseToggle();
        }
        head.rotate(-90*slot);
    }

    public void swap(int posA, int posB) {
		if(posA == posB) return;
		if(posA > posB){ //swapear las variables para que posA sea menor
			int aux = posA;
			posA = posB;
			posB = aux;
		}
        takeCube(-1, posA);
        takeCube(1, posB);
        dropCube(-1, posB);
        dropCube(1, posA);
    }

}