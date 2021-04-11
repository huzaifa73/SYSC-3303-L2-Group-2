/**
 * 
 */
package pack;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.*;

/**
 * @author Blake, Hovish, Jerry
 *
 */
public class ElevatorSystemGUI extends JFrame implements ActionListener {
	
	//Left Section fields
	private int SIZE = 9;
	private JButton startButton; //Start Button field
	private JLabel floorTimeInstruction;
	private JLabel doorTimeInstruction;
	private TextField floorTimeInput; //Floor Time input field
	private TextField doorTimeInput; //Door Time Input Field
	private JLabel eventCompletion;  //Count of number of completed events
	private JLabel executionTime; //Execution Time of system
	private JLabel elevatorSystemTitle;  //Title of GUI Elevator System
	private JButton fileInputButton; //Button used to upload the file
	File IOFile;
	
	//Table
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
		
		
		//Other Fields
		ElevatorSystem eleSystem;
		
	
	
	public ElevatorSystemGUI(){
		super("Elevator System");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setLayout(new GridLayout(1,2));
	
		
		JPanel fieldPanel = new JPanel(new GridLayout(SIZE, 1));
		fieldPanel.setBorder(BorderFactory.createCompoundBorder(
			    BorderFactory.createLoweredBevelBorder(),
			    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		eleSystem = new ElevatorSystem();
		
		//LEFT SECTION
		
				startButton = new JButton("StartButton");
				startButton.addActionListener((e -> { //NOTE: only defaults... have not connected to other buttons.

					//Convert floor time and door time from String to Double
					String floorTimeString = floorTimeInput.getText().trim();
					String doorTimeString = doorTimeInput.getText().trim();
					
					double floorTime = Double.parseDouble(floorTimeString);
					double doorTime = Double.parseDouble(doorTimeString);
					
					System.out.println("THE FLOOR TIME IS: " + floorTime);
					System.out.println("THE DOOR TIME IS: " + doorTime);
					
					//Create threads
					Thread floor_subsystem, scheduler;
				
					//Create Objects
					Scheduler schedulerObj = new Scheduler(this);
					
					//File FloorInputFile.txt should be stored in directly in the project folder
					File defaultFile = new File("src\\pack\\FloorInputFile.txt");
					
					//Check if IOFile is null
					if (IOFile == null) {
						IOFile = defaultFile;
					}
					
					System.out.println("THE FILENAME IS: " + IOFile.getName());
					FloorSubsystem floorSubsystem = new FloorSubsystem(schedulerObj, IOFile); 
					schedulerObj.setup(floorSubsystem);

					
					//Initialize threads
					scheduler = new Thread(schedulerObj, "scheduler");
					floor_subsystem = new Thread(floorSubsystem,"floor_subsystem");

					//Start threads
					floor_subsystem.start();
					scheduler.start();

				}));
				
				fileInputButton = new JButton("FileInput");
				
				fileInputButton.addActionListener((e -> {
					JFileChooser C = new JFileChooser();
		            C.showDialog(null,"Choose Event File to Import");
		            C.setVisible(true);
		            IOFile = C.getSelectedFile();
		            
				}));
				
				floorTimeInstruction = new JLabel("Please input the time to move between floors in seconds below:");
				doorTimeInstruction = new JLabel("Please input the opening/closing time for the doors in seconds below:");
				

				floorTimeInput = new TextField("9.4");
				//floorTimeInput.addActionListener(this);
				//floorTimeInput.addActionListener();
				
				doorTimeInput = new TextField("9.1");
				//doorTimeInput.addActionListener(this);
				
				//doorTimeInput.addActionListener();
				
				executionTime = new JLabel(" ");
				executionTime.setText("File Execution Time: 0:00");
				
				eventCompletion = new JLabel("Completed Events: 0");
				elevatorSystemTitle = new JLabel("Elevator System: Group 2 - 2021");
		
		//Add file input HERE *******
		fieldPanel.add(elevatorSystemTitle);
		fieldPanel.add(fileInputButton);
		fieldPanel.add(floorTimeInstruction);
		fieldPanel.add(floorTimeInput);
		fieldPanel.add(doorTimeInstruction);
		fieldPanel.add(doorTimeInput);
		fieldPanel.add(eventCompletion);
		fieldPanel.add(executionTime);
		fieldPanel.add(startButton);
	
		
				
				
		//Right Section

		JPanel buttonPanel = new JPanel(new GridLayout(27, 4));

		buttonPanel.setPreferredSize(new Dimension(600,1000));
		buttonPanel.setBorder(BorderFactory.createCompoundBorder(
			    BorderFactory.createLoweredBevelBorder(),
			    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		JLabel floorText = new JLabel("Floor");
		JLabel e1Text = new JLabel("Elevator 1");
		JLabel e2Text = new JLabel("Elevator 2");
		JLabel e3Text = new JLabel("Elevator 3");
		JLabel e4Text = new JLabel("Elevator 4");
		buttonPanel.add(floorText);
		buttonPanel.add(e1Text);
		buttonPanel.add(e2Text);
		buttonPanel.add(e3Text);
		buttonPanel.add(e4Text);
		
		//JButton simFloorsButton = new JButton("Simulate Floor Requests");
		for(int i = 109; i >= 0; i--) {
			//The division is the floor 
			int f = i/5;
			//The remainder is the elevator.
			int r = i % 5;
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
				case 4:
					temp.setText(String.valueOf(f+1));
				default:
					//error
			}
			
		}
		
		setElevatorFloor(0, 1);
		setElevatorFloor(1, 1);
		setElevatorFloor(2, 1);
		setElevatorFloor(3, 1);
		
		

		this.add(fieldPanel);
		this.add(buttonPanel);
		
		this.setSize(800, 600);
		this.setVisible(true);

	}
	
	public static void main(String[] args) {
		new ElevatorSystemGUI();
	}
	
	/**
	 * Method: Updates the number of completed events
	 * @param numberEvents
	 */
	public void setCompletedEvents(int numberEvents) {
		eventCompletion.setText("Completed Events: " + String.valueOf(numberEvents));
	}
	
	/**
	 * Method: Updates the execution Time of the Elevator System
	 * @param time
	 */
	public void setExecutionTime(String time) {
		executionTime.setText(time);	
	}
	
	/**
	 * Method: Update the elevator floor for each Elevator
	 * @param ElevatorNum
	 * @param Floor
	 */
	public void setElevatorFloor(int ElevatorNum, int Floor) {
		
		switch(ElevatorNum + 1) {
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
	
	/**
	 * MethodL Update the Target floor for each Elevator
	 * @param ElevatorNum
	 * @param Floor
	 */
	public void setTargetFloor(int ElevatorNum, int Floor) {

		switch(ElevatorNum + 1) {
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
	
	/**
	 * Method: Update the elevator state for each elevator
	 * @param ElevatorNum
	 * @param state
	 */
	public void setElevatorState(int ElevatorNum, String state) {
		
		switch(ElevatorNum + 1) {
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
	
	
	/**
	 * Method: Test Method
	 */
	public void testprint() {
		System.out.println("CAN CALL THIS FUNCTION");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	
}
