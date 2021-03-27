package pack;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import javax.naming.directory.InvalidAttributesException;

import org.junit.jupiter.api.Test;

class EventTest {

	@Test
	void test() {
		
		Event e1 = null, e2 = null;
		try {
			e1 = new Event("05:13:24", "UP", 4, 5);
		} catch (InvalidAttributesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] temp = new byte[100];
		
		temp = Event.buildByteArray(e1);
		e2 = Event.rebuildEvent(temp);
		
		System.out.println("EVENT 1 - original: " + e1);
		System.out.println("e1 time: " + e1.getTimeString() + "\nlength: " + e1.getTimeString().length());
		
		System.out.println("EVENT 2 - reconstructed: " + e2);
		System.out.println("e2 time: " + e2.getTimeString() + "\nlength: " + e2.getTimeString().length());
		
		assertEquals(e1.getTimeString(), e2.getTimeString());
		assertEquals(e1.getCurrentFloor(), e2.getCurrentFloor());
		assertEquals(e1.getTargetFloor(), e2.getTargetFloor());
		assertEquals(e1.getUpDown(), e2.getUpDown());
	}

}