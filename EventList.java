package pack;

import java.util.LinkedList;

/**
 * The basic container class to show mutual exclusion and 
 * condition synchronization.
 * 
 * plate can hold up to three items
 * 
 * @author Jerry Xiong
 * @version 1.00
 */
public class EventList
{
	private LinkedList<Event> eventList = new LinkedList<>();
    
    /**
     * Puts an object in the box.  This method returns when
     * the object has been put into the box.
     * 
     * @param item The object to be put in the box.
     */
    public synchronized void put(Event event) {
    	eventList.add(event);
        notifyAll();
    }
    
    /**
     * Gets an object from the list without removing it.
     * 
     * @return The object taken from the list.
     */
    public synchronized Event get() {
        while (eventList.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        
        return eventList.getFirst();
    }
    
    /**
     * Removes the first event from the list.  This method returns once the
     * object has been removed from the list.
     * 
     * @return Whether the event was removed.
     */
    public synchronized boolean remove() {
        while (eventList.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                return false;
            }
        }
        
        return (eventList.removeFirst() != null);
    }
    
    /**
     * Removes an event from the list.  This method returns once the
     * object has been removed from the list.
     * 
     * @return Whether the event was removed.
     */
    public synchronized boolean remove(Event event) {
        while (eventList.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                return false;
            }
        }
        
        return eventList.remove(event);
    }
    
    /**
     * Removes an event from the list.  This method returns once the
     * object has been removed from the list.
     * 
     * @return Whether the event was removed.
     */
    public synchronized void print() {
        for(Event e: eventList) {
        	e.print();
        }

    }

}
