package pack;
import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;  
import java.awt.*;
import java.awt.event.*;
/**
 * Class to run the elevator system
 * 
 * @author
 * @version 1.00
 */
public class ElevatorPanel extends JFrame implements ActionListener{
	
	//Using JButtons because they look nicer and style better
	private JButton[] elevator1 = new JButton[22];
	private int e1CurrentFloor = 1;
	private int e1TargetFloor = 1;
	private String e1State = "";
	
	private JButton[] elevator2 = new JButton[22];
	private int e2CurrentFloor = 1;
	private int e2TargetFloor = 1;
	private String e2State = "";
	
	private JButton[] elevator3 = new JButton[22];
	private int e3CurrentFloor = 1;
	private int e3TargetFloor = 1;
	private String e3State = "";
	
	private JButton[] elevator4 = new JButton[22];
	private int e4CurrentFloor = 1;
	private int e4TargetFloor = 1;
	private String e4State = "";
	
	final String TARGET = "TARGET";
	
	
	
	
	public ElevatorSystemGui() {
		super("Elevator System");
		this.setLayout(new GridLayout(1,4));

		JPanel buttonPanel = new JPanel(new GridLayout(22, 4));

		buttonPanel.setPreferredSize(new Dimension(600,1000));
		//JButton simFloorsButton = new JButton("Simulate Floor Requests");
		for(int i = 87; i >= 0; i--) {
			//The division is the floor 
			int f = i/4;
			//The remainder is the elevator.
			int r = i % 4;
			JButton temp = new JButton("");
			//JButton temp = new JButton(String.valueOf(f+1));
			temp.setPreferredSize(new Dimension(80,80));
			temp.setEnabled(false);
			buttonPanel.add(temp);
			switch(r) {
				case 0:
					elevator4[f] = temp;
					
					break;
				case 1:
					elevator3[f] = temp;
	
					break;
				case 2:
					elevator2[f] = temp;
					break;
				case 3:
					elevator1[f] = temp;
					break;
				default:
					//error
			}
			

			
		}
		
		setElevatorFloor(1, 1);
		setElevatorFloor(2, 1);
		setElevatorFloor(3, 1);
		setElevatorFloor(4, 1);
		
		
		
		setElevatorFloor(1, 2);
		setElevatorFloor(2, 8);
		setElevatorFloor(3, 10);
		setElevatorFloor(4, 19);
		
		setElevatorState(1, "happy");
		setElevatorState(2, "sad");
		setElevatorState(3, "broken");
		setElevatorState(4, "confused");
		
		
		setTargetFloor(1, 9);
		setTargetFloor(2, 15);
		setTargetFloor(3, 11);
		setTargetFloor(4, 2);
		

		this.add(buttonPanel);
		
		this.setSize(600, 1000);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new ElevatorSystemGui();
	}
	

	public void setElevatorFloor(int ElevatorNum, int Floor) {
		
		switch(ElevatorNum) {
			case 1:
				elevator1[e1CurrentFloor -1].setEnabled(false);
				elevator1[e1CurrentFloor -1].setText("");
				
				e1CurrentFloor = Floor;
				elevator1[e1CurrentFloor -1].setEnabled(true);
				elevator1[e1CurrentFloor -1].setText(e1State);
				break;
			case 2:
				elevator2[e2CurrentFloor -1].setEnabled(false);
				elevator2[e2CurrentFloor -1].setText("");
				
				e2CurrentFloor = Floor;
				elevator2[e2CurrentFloor -1].setEnabled(true);
				elevator2[e2CurrentFloor -1].setText(e2State);
				break;
			case 3:
				elevator3[e3CurrentFloor -1].setEnabled(false);
				elevator3[e3CurrentFloor -1].setText("");
				
				e3CurrentFloor = Floor;
				elevator3[e3CurrentFloor -1].setEnabled(true);
				elevator3[e3CurrentFloor -1].setText(e3State);
				break;
			case 4:
				elevator4[e4CurrentFloor -1].setEnabled(false);
				elevator4[e4CurrentFloor -1].setText("");
				
				e4CurrentFloor = Floor;
				elevator4[e4CurrentFloor -1].setEnabled(true);
				elevator4[e4CurrentFloor -1].setText(e4State);
				break;
			default:
				//error
			}
	}
	
	
	public void setTargetFloor(int ElevatorNum, int Floor) {

		switch(ElevatorNum) {
			case 1:
				elevator1[e1TargetFloor -1].setText("");
				e1TargetFloor = Floor;
				elevator1[e1TargetFloor -1].setText(TARGET);
				break;
			case 2:
				elevator2[e2TargetFloor -1].setText("");
				e2TargetFloor = Floor;
				elevator2[e2TargetFloor -1].setText(TARGET);
		
				break;
			case 3:
				elevator3[e3TargetFloor -1].setText("");
				e3TargetFloor = Floor;
				elevator3[e3TargetFloor -1].setText(TARGET);
				break;
			case 4:
				elevator4[e4TargetFloor -1].setText("");
				e4TargetFloor = Floor;
				elevator4[e4TargetFloor -1].setText(TARGET);
				break;
			default:
				//error
		}
	}
	
	public void setElevatorState(int ElevatorNum, String state) {
		
		switch(ElevatorNum) {
			case 1:
				e1State = state;
				elevator1[e1CurrentFloor -1].setText(e1State);

				break;
			case 2:
				e2State = state;;
				elevator2[e2CurrentFloor -1].setText(e2State);

				break;
			case 3:
				e3State = state;
				elevator3[e3CurrentFloor -1].setText(e3State);
				break;
			case 4:
				e4State = state;
				elevator4[e4CurrentFloor -1].setText(e4State);
				break;
			default:
				//error
			}
	}
	
	
//	public static void main(String[] args) {
//		
//		//Create threads
//		Thread floor_subsystem, scheduler;
//		
//		//Create Objects
//		Scheduler schedulerObj = new Scheduler();
//		
//		//File FloorInputFile.txt should be stored in directly in the project folder
//		File ioFile = new File("src\\pack\\FloorInputFile.txt");
//		FloorSubsystem floorSubsystem = new FloorSubsystem(schedulerObj, ioFile);
//		schedulerObj.setup(floorSubsystem);
//
//		
//		//Initialize threads
//		scheduler = new Thread(schedulerObj, "scheduler");
//		floor_subsystem = new Thread(floorSubsystem,"floor_subsystem");
//
//
//		//Start threads
//		floor_subsystem.start();
//		scheduler.start();
//
//	}
//	
	public void testprint() {
		System.out.println("CAN CALL THIS FUNCTION");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
