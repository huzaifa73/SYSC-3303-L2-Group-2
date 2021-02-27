package pack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.naming.directory.InvalidAttributesException;


/**
Floor Subsystem is a class that could make requests for elevators events


@author Cameron Maccoll, Huzaifa Mazhar
@version 1.0
@date February 6th 2020 
*/


public class FloorSubsystem implements Runnable{
	
	private Scheduler scheduler;
	private File requestEvents;
	
	
	public FloorSubsystem(Scheduler scheduler) {   
		
		this.scheduler = scheduler;     

	}
	/**
	 * make a new FloorSubsystem with the provided Scheduler and Input file
	 * @param scheduler
	 * @param requestEvents
	 */
	public FloorSubsystem(Scheduler scheduler, File requestEvents) {   
	
		this.scheduler = scheduler;     
		this.requestEvents = requestEvents;
	}
	

	/**
	 * Read an input file with lines structured as 
	 * "<TIME> <DIRECTION> <TARGET FLOOR> <CURRENT FLOOR>" and create a list
	 * of events to be sent to the Scheduler.
	 * 
	 * @return ArrayList<Event> The list of events read from the input file
	 */
	ArrayList<Event> readRequestEvents() {
		
		ArrayList<Event> eventsList = null;   //create new array list object
		
		try {
			BufferedReader bReader = new BufferedReader( //new buffered reader to read requests events
					new FileReader(requestEvents));
			
			String s;     //instantiate a dummy string
			String[] sArr;   //instantiate a dummy array of strings 
			eventsList = new ArrayList<Event>(); //new arraylist of our event
			
			
			while((s=bReader.readLine()) != null) { //while reading the file
				
				sArr = s.split(" ");  //split will break any string into an array of strings called sArr, where the the said string is broken apart by some whitespace
			
				
				try {
					eventsList.add(new Event(sArr[0], sArr[1], Integer.parseInt(sArr[2]), Integer.parseInt(sArr[3])));  //parse sArr to retrieve information about the event
				} catch (InvalidAttributesException e) {
					System.out.println(e);
					System.exit(1);
				}
				System.out.println("\nNew data obj:\n" + eventsList.get(eventsList.size() - 1));  //display new data object
			}
			
			bReader.close();

		} catch (IOException ioException) {                 //error exception handling
			System.out.println("Error reading input file: " + ioException);   
			System.exit(1);
		}

		return eventsList;   
		
	}
	
	
	public void completeTransfer(Event receievedEvent) {
		System.out.println("ReceievedEvent for Floor: " + receievedEvent);
		
		// For iter1 testing, shutdown after 1 successful data loop
		System.exit(0);
	}
	
	/**
	 * Read input file and send events to scheduler
	 */
	public void run() {
		
		ArrayList<Event>eventList;
		// Load list of request events from input file
		eventList = readRequestEvents(); 
		
		// Send list of events to scheduler
		for(Event e : eventList) {
			scheduler.receiveRequest(e);
		}
	}
	
	private void printWrapper(String msg) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    		LocalDateTime now = LocalDateTime.now();
		
		System.out.println("_____________________________________________________");
		System.out.println("                Floor Subsystem");
		System.out.println("-----------------------------------------------------");
		System.out.println("Log at time: " + dtf.format(now));
		System.out.println(msg);
		System.out.println("_____________________________________________________");
	}
}
