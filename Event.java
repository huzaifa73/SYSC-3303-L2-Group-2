/**
 * The Event class to model the request events triggered by floor & elevator buttons
 * in the ElevatorSystem
 */

package pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.naming.directory.InvalidAttributesException;

public class Event {
	private long delay;
	public boolean isFloorRequest;
	private String timeString = "null";
	private boolean upDown;
	private int elevatorNumber;
	private int targetFloor;
	private int currentFloor;
	private int finalDestination;

	//Default Constructor
	public Event(){
		String timeString = "no";
		boolean upDown = true;
		int elevatorNumber = -1;
		int targetFloor = -1;
		int currentFloor = -1;
	}
	
	//Copy constructor for elevator to call
	public Event(Event e) {
		isFloorRequest = false;
		this.targetFloor = e.finalDestination;
		this.finalDestination = e.finalDestination;
		this.timeString = e.timeString;
		
	}
	
	/**
	 * Create a new Event with the specified params
	 * @param timeString
	 * @param upDownS "UP" or "DOWN" case irrelevant
	 * @param targetFloor
	 * @param currentFloor
	 * @throws InvalidAttributesException
	 */
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
	
	/**
	 * Create a new Event with the specified params using delay
	 * @param isFloorRequest
	 * @param delay
	 * @param upDownS "UP" or "DOWN" case irrelevant
	 * @param finalDestination
	 * @param targetFloor
	 * @throws InvalidAttributesException
	 */
	public Event(boolean isFloorRequest, long delay, String upDownS, int finalDestination, int targetFloor) throws InvalidAttributesException {
		this.isFloorRequest = isFloorRequest;
		this.delay = delay;
		this.finalDestination = finalDestination;
		this.targetFloor = targetFloor;
		this.currentFloor = targetFloor;
		
		
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
		return new String(
				"isFloorRequest: " + isFloorRequest +
				"\nDELAY: " + delay + 
				"\nTIME: " + timeString + 
				"\nDIRECTION: " + direction + 
				"\nTARGET FLOOR: " + targetFloor + 
				"\nCURRENT FLOOR: " + currentFloor +
				"\nFINAL DESTINATION: " + finalDestination);
	}
	
	
	
	static public byte[] buildByteArray(Event e) {
		
		// CREATES AN ARRAY OF BYTES IN THE FORM
		// direction (1), targetfloor (1), currentfloor(1), time (x)
		ByteArrayOutputStream eventDataBaos = new ByteArrayOutputStream();
		
		// first byte corresponds to direction, 1 for up 0 for down
		byte toWrite = (byte)((e.getUpDown()) ? 1 : 0);
		eventDataBaos.write(toWrite);
		
		// second byte corresponds to target floor
		eventDataBaos.write(e.getTargetFloor());
		
		// third byte corresponds to current floor
		eventDataBaos.write(e.getCurrentFloor());
		
		
		eventDataBaos.write(e.getFinalDestination());
		
		// the actual last byte corresponds to whether it came from a floor or elevator
		byte isFloor = (byte)((e.getIsFloorRequest()) ? 1 : 0);
		eventDataBaos.write(isFloor);
		
		//System.out.println("build byte array is floor: " + e.getIsFloorRequest() +" byte: " + isFloor);
		
		// last bytes correspond to time
		// convert timestring (in the form "yyyy/MM/dd HH:mm:ss") to bytes
		try {
			eventDataBaos.write(e.getTimeString().getBytes());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		return eventDataBaos.toByteArray();
	}
	
	
	static public Event rebuildEvent(byte[] eventDataBytes) {
		
		// READS IN AN ARRAY OF BYTES IN THE FORM
		// direction (1), targetfloor (1), currentfloor(1), time (x)
		Event e = new Event();
		
		// first byte corresponds to direction
		boolean upDown = (eventDataBytes[0] == 1) ? true : false;
		e.setUpDown(upDown);
		
		// second byte corresponds to target floor
		e.setTargetFloor((int) eventDataBytes[1]);
		
		// third byte corresponds to current floor
		e.setCurrentFloor((int) eventDataBytes[2]);
		
		// third byte corresponds to current floor
		e.setFinalDestination((int) eventDataBytes[3]);
		
		// the actual last byte corresponds to whether it came from a floor or elevator
		boolean isFloor = (eventDataBytes[4] == 1) ? true : false;
		e.setIsFloorRequest(isFloor);
		//System.out.println("rebuild event data ....  is floor: " + e.getIsFloorRequest() +" byte: " + isFloor);
		
		// last bytes correspond to time
		// convert timestring (in the form "yyyy/MM/dd HH:mm:ss") to bytes
		byte[] timeBytes = new byte[100];
		for(int i = 0; i < eventDataBytes.length-3; i++) {
			timeBytes[i] = eventDataBytes[i+3];
		}
		

		
		e.setTimeString(new String(timeBytes).trim());
		
		return e;
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
	
	public long getDelay(){
		return this.delay;
	}
	
	public boolean getIsFloorRequest() {
		return this.isFloorRequest;
	}
	
	public int getFinalDestination() {
		return this.finalDestination;
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
	
	public void setFinalDestination(int finalDestination){
		this.finalDestination = finalDestination;
	}
	

	public void setIsFloorRequest(boolean isFloorRequest){
		this.isFloorRequest = isFloorRequest;
	}

	
	
}
