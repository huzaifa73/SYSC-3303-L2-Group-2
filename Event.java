package pack;

import javax.naming.directory.InvalidAttributesException;

public class Event {
	
	private String timeString;
	private boolean upDown;
	private int elevatorNumber;
	private int targetFloor;
	private int currentFloor;

	//Default Constructor
	public Event(){
		String timeString = "no";
		boolean upDown = true;
		int elevatorNumber = -1;
		int targetFloor = -1;
		int currentFloor = -1;
	}
	
	public Event(String timeString, String upDownS, int targetFloor, int currentFloor) throws InvalidAttributesException {
		this.timeString = timeString;
		this.targetFloor = targetFloor;
		this.currentFloor = currentFloor;
		
		if(upDownS.toUpperCase().equals("UP"))
			upDown = true;
		else if(upDownS.toUpperCase().equals("DOWN"))
			upDown = false;
		else {
			throw new InvalidAttributesException("Direction string not matching UP or DOWN");
		}
	}
	
	@Override
	public String toString() {
		String direction = upDown==true ? "UP" : "DOWN";
		return new String("TIME: " + timeString + 
				"\nDIRECTION: " + direction + 
				"\nTARGET FLOOR: " + targetFloor + 
				"\nCURRENT FLOOR: " + currentFloor);
	}

	//Getters
	public String getTimeString(){
		return this.timeString;
	}
	
	public boolean getUpDown(){
		return this.upDown;
	}
	
	public int getElevatorNumber(){
		return this.elevatorNumber;
	}

	public int getTargetFloor(){
		return this.targetFloor;
	}

	public int getCurrentFloor(){
		return this.currentFloor;
	}

	//Setters
	public void setTimeString(String timeString){
		this.timeString = timeString;
	}

	public void setUpDown(boolean upDown){
		this.upDown = upDown;
	}
	
	public void setElevatorNumber(int elevatorNumber){
		this.elevatorNumber = elevatorNumber;
	}

	public void setTargetFloor(int targetFloor){
		this.targetFloor = targetFloor;
	}

	public void setCurrentFloor(int currentFloor){
		this.currentFloor = currentFloor;
	}
	

	
	
}
