/**
 * Class: Elevator class which continuously requests events from the Scheduler
 * 
 * @authors Desmond, Hovish
 * @verison 1.00
 */

package pack;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

class Elevator implements Runnable{
    private Scheduler scheduler;
    private int id;
    private ArrayList<String> statusDirection;
    private String currentDirection;
    
    private Event newReceivedInfo;
    private Event oldReceivedInfo;
    private Event sendingInfo;
    
    //private ArrayList<Boolean> elevatorLamps;
    private boolean doorOpen;
    
    //Keep track of button lamps that are pressed with list //TODO
    
    
    private MotorState motorState;
    private ElevatorStates state;
    
    private int currentFloor;
    private int targetFloor;
    private int tempTargetFloor;
    private String timeString;
    private boolean upDown;
    

    public Elevator(Scheduler scheduler) 
    {
        this.scheduler = scheduler;
        doorOpen = false;
        motorState = MotorState.STOPPED;
        state = ElevatorStates.State0;
        
        //elevatorLamps = new ArrayList();
        //statusDirection = new ArrayList();
        this.id = 1;
        this.currentFloor = 1;
        this.targetFloor = -1;
        tempTargetFloor = -1;
        sendingInfo = new Event();
        newReceivedInfo = new Event();
        oldReceivedInfo = new Event();
        
    }
    
    /**
     * Method: Requests event from the scheduler
     */
    public void readEvent() {
    	//request recievedInfo from Scheduler
    	scheduler.requestEvent(); 
    }
    
    /**
     * Method: Receives previously requested event from Scheduler
     */
    public void receiveRequest(Event event) {
    	newReceivedInfo = event;
    	System.out.println("RECEIVE REQUEST: " + newReceivedInfo.toString());
    	readInfo(newReceivedInfo);
    	
	}
    
    /**
     * Method: Sends the sendingInfo Info back to the scheduler
     */
    public void sendEvent() {
    	scheduler.receiveData(sendingInfo);
    }
    
    /**
     * Method: reads the recievedInfo from the dataObject Event 
     */
    public void readInfo(Event data) {
    	//Extract  info from DataObject
    	sendingInfo = data;

    	//currentFloor = data.getCurrentFloor();   
    	targetFloor = data.getTargetFloor();
    	//upDown = data.getUpDown();
    	timeString = data.getTimeString();
    }
    
    
    /**
     * Method: pushes the button in the elevator and updates button/lamp status
     * @param button The button that was pressed
     */
    public void pushButton(int button) {
    	//elevatorLamps.get(button) = true;
    	tempTargetFloor = button;
    	readInfo(newReceivedInfo);
    }
    
    //Getters for testing
    public boolean getUpDown(){
		return this.upDown;
	}
	
	public String getTimeString(){
		return this.timeString;
	}

	public int getTargetFloor(){
		return this.targetFloor;
	}

	public int getCurrentFloor(){
		return this.currentFloor;
	}
    
    /**
     * Method: The run() method is used to start the elevator thread
     */
    public void run()
    {
        while(true) {
        	
        	readEvent();  //Request an event from the scheduler    	
          	changeState(); //Change the state of the elevator System
      
        	
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {}
        	
        }
    }
    

    /**
     * Method: This method changes the state of the Elevator
     */
	private void changeState() {
	    
		switch(state) {
		
		case State0 :
			//Set the motorState to STOPPED  and open the door
			motorState = motorState.STOPPED;
			doorOpen = true;
			
			//Check if we received a task and check the targetFloor 
			if (newReceivedInfo != null) {
				
				if (currentFloor == targetFloor) {
					state = state.State2;
				}
				else {
					state = state.State1;
				}
			}
			else {
				System.out.println("Elevator stopped and waiting...");
			}
			break;
			
		case State1:
			
			//Check if there is new Received info from the Scheduler
			if (newReceivedInfo != null) {
				//Check if targetFloot is greater than currentFloor
				if (targetFloor != currentFloor && targetFloor > currentFloor) {
					doorOpen = false;
					motorState = motorState.UP;
					move();
					
				}
				//Check if targetFloor is less than currentFloor
				else if (targetFloor != currentFloor && targetFloor < currentFloor) {
					doorOpen = false;
					motorState = motorState.DOWN;
					move();
				}
			}
			break;
			
		case State2:
			if (newReceivedInfo != null) {
				if (targetFloor == currentFloor) {
					
					//Change state of motor to stopped and open door
					motorState = motorState.STOPPED;
					doorOpen = true;
					
					//Task is complete, clear the task info
					newReceivedInfo = null; 
					oldReceivedInfo = null;
					System.out.println("Elevator " + id + "reached target and is stopped on floor:" + currentFloor);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					state = state.State0;				
				}
			}
			break;
		}
	}

	/**
	 * Method: Moves the Elevator up or down a floor while taking into account the time 
	 */
	private void move() {
		
		while(currentFloor != targetFloor) {
			//Set oldReceivedInfo equal to newReceivedInfo
			oldReceivedInfo = newReceivedInfo;
			
			//Check if motorState equals UP. If true, then move elevator UP
			if (motorState.equals(MotorState.UP)) {
				try {
					Thread.sleep(9500);  //The time it takes the elevator to move one floor
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currentFloor++;
				System.out.println("Elevator " + id + " moved to:" + currentFloor);
				
			}
			//Check if motorState equals UP. If true, then move elevator DOWN
			else if (motorState.equals(MotorState.DOWN)) {
				try {
					Thread.sleep(9500);  //The time it takes the elevator to move one floor
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currentFloor--;
				System.out.println("Elevator " + id + " moved to:" + currentFloor);
			}
			
			//Request an event from the scheduler to see if there's an updated one
			readEvent();
			
			//Check if there is a new request
			if (oldReceivedInfo != newReceivedInfo) {
				state = state.State1;
				break;			
			}
		}
		//Go to state 2 if currentFloor == targetFloor
		if (currentFloor == targetFloor) {
			state = state.State2;	
		}
		
	}
	
	 private void printWrapper(String msg) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		    	LocalDateTime now = LocalDateTime.now();
				
			System.out.println("_____________________________________________________");
			System.out.println("                 Elevator");
			System.out.println("-----------------------------------------------------");
			System.out.println("Log at time: " + dtf.format(now));
			System.out.println(msg);
			System.out.println("_____________________________________________________");
		    }
}
