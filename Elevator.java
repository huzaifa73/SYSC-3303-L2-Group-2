/**
 * Class: Elevator class which continuously requests events from the Schduler
 * 
 * @authors Desmond, Hovish
 * @verison 1.00
 */

package pack;
import java.util.concurrent.ThreadLocalRandom;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

class Elevator implements Runnable{
    private Thread scheduler;
    private int id;
    //private ArrayList<String> statusDirection;
    private String currentDirection;
    private Event receivedInfo;
    private Event sendingInfo;
    //private ArrayList<boolean> elevatorLamps;
    private boolean doorStatus;
    private boolean motorStatus;
    private int currentFloor;
    private int targetFloor;
    private String timeString;
    private boolean upDown;
    
    //private ArrayList<boolean> buttonStatus; //Check later if needed

    public Elevator(Scheduler scheduler) 
    {
        this.scheduler = scheduler;
        doorStatus = false;
        motorStatus = false;
        //elevatorLamps = new ArrayList();
        statusDirection = new ArrayList();
        this.id = id;
        this.currentFloor = -1;
        this.targetFloot = null;
        
        
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    	//LocalDateTime now = LocalDateTime.now();
        
    }
    
    /**
     * Method: Requests sendInfo() from the scheduler
     */
    public void readEvent() {
    	//request recievedInfo from Schedular
    	receivedInfo = scheduler.request_event(); //************Need to confirm on the name of the method
    	receivedInfo.toString();
    	readInfo(receivedInfo);
    }
    
    /**
     * Method: Sends the sendingInfo Info back to the scheduler
     */
    public void sendEvent() {
    	scheduler.receive_request(sendingInfo); //************Need to confirm on the name of the method
    }
    
    /**
     * Method: reads the recievedInfo from the dataObject Event 
     */
    public void readInfo(Event data) {
    	//Extract  info from DataObject

    	currentFloor = data.getCurrentFloor();   
    	targetFloor = data.getTargetFloor();
    	upDown = data.getupDown();
    	timeString = data.getTimeString();
    }
    
    /**
     * Method: Elevator action to perform certain actions based on the current Information
     */
    public void ElevatorAction() {
    	if (currentFloor == targetFloor) {
    		motorStatus = false;
    		elevatorLamps.get(currentFloor) = false;
    		currentDirection = "Stopped"; //****
    		doorStatus = true;
    		
    	}
    	else {
    		motorStatus = true;
    		elevatorLamps.get(currentFloor) = false;
    		currentDirection = "Stopped";//****
    		doorStatus = true;
    		
    	}
    }    
    
    /**
     * Method: Initialize the information to send to the scheduler
     */
    public void initializeInfotoSend() {
    	//Current floor, time, up/down, target floor
    	
    	//Set Time
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    	LocalDateTime now = LocalDateTime.now();
    	sendingInfo.setTime(dtf.format(now));
    	
    	//Set current Floor
    	sendingInfo.setCurrentFloor(currentFloor);
    	
    	//set target Floor
    	sendingInfo.setTargetFloor(targetFloor);
    	
    	//set Direction
    	sendingInfo.setUpDown(upDown);
    }
    
    
    /**
     * Method: pushes the button in the elevator and updates button/lamp status
     * @param button The button that was pressed
     */
    public void pushButton(int button) {
    	//elevatorLamps.get(button) = true;
    	initializeInfotoSend();
    }
    
    
    /**
     * Method: The run() method is used to start the elevator thread
     */
    public void run()
    {
        while(true) {
        	
        	readEvent();
        	initializeInfotoSend();
        	sendEvent();
        	//readEvent();
        	//ElevatorAction();
	            
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {}
        	
        }
    }
}
