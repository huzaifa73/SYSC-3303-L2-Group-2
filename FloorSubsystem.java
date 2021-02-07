package pack1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.naming.directory.InvalidAttributesException;



public class FloorSubsystem {
	
	private Scheduler scheduler;
	private File requestEvents;

	public FloorSubsystem(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public FloorSubsystem(Scheduler scheduler, File requestEvents) {
	
		this.scheduler = scheduler;
		this.requestEvents = requestEvents;
	}
	
	
	ArrayList<DataObject> readRequestEvents() {
		
		ArrayList<DataObject> eventsList = null;
		
		try {
			BufferedReader bReader = new BufferedReader(
					new FileReader(requestEvents));
			
			String s;
			String[] sArr;
			eventsList = new ArrayList<DataObject>();
			
			while((s=bReader.readLine()) != null) {
				
				sArr = s.split(" ");
				
				try {
					eventsList.add(new DataObject(sArr[0], sArr[1], Integer.parseInt(sArr[2]), Integer.parseInt(sArr[3])));
				} catch (InvalidAttributesException e) {
					System.out.println(e);
					System.exit(1);
				}
				System.out.println("\nNew data obj:\n" + eventsList.get(eventsList.size() - 1));
			}
			
			bReader.close();

		} catch (IOException ioException) {
			System.out.println("Error reading input file: " + ioException);
			System.exit(1);
		}

		return eventsList;
		
	}
	
	
	public void run() {
		
		// Load list of request events from input file
		readRequestEvents();
		
		// Send list of events to scheduler
		// (insert function here)
		
		while(true) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException ie) {
			}
		}
	}
}
