package pack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	DatagramSocket sendSocket;
	DatagramPacket sendPacket;
	
	
	public FloorSubsystem(Scheduler scheduler) {   
		
		this.scheduler = scheduler;     

	}
	/**
	 * Create a new FloorSubsystem with the provided Scheduler and Input file
	 * @param scheduler The scheduler to send the processed event requests to
	 * @param requestEvents The file from which to read the list of requests
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
		
		try {
			sendSocket = new DatagramSocket();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		ArrayList<Event>eventList;
		// Load list of request events from input file
		eventList = readRequestEvents(); 
		
		//Checks if delay matches the delay in the events
		//If not, wait that long
		long delay = 0;
		
		// Send list of events to scheduler
		for(Event eventToSend : eventList) {
			
			long eventDelay = eventToSend.getDelay();
			if(eventDelay <= delay) {
				//No delay needed
			}else {
				//Delay not reached, wait and then send
				try {
					printWrapper("Waited " + delay + " seconds, before sending event");
					Thread.sleep(eventDelay*1000);
					delay += eventDelay;
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

			}

			//Set the time before sending
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    			LocalDateTime now = LocalDateTime.now();
			eventToSend.setTimeString(dtf.format(now));
			
			// Load sendpacket with event data
			try {
				byte msg[] = Event.buildByteArray(eventToSend);
				sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), 1999); //1999 is port for scheduler
			}
			catch (IOException ee) {
				ee.printStackTrace();	
			}
			
			// Send packet data to scheduler
			try {
				sendSocket.send(sendPacket);
			}
			catch (IOException e){
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		sendSocket.close;
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
