/**
 * 
 */
package pack;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.*;

/**
 * @author Blake, Hovish, Jerry, Cam
 *
 */
public class ElevatorSystemGUI extends JFrame implements ActionListener {
	
	//Left Section fields
	private int SIZE = 9;
	private JButton startButton; //Start Button field
	private JButton stopButton; //Stop Button field
	private JLabel floorTimeInstruction;
	private JLabel doorTimeInstruction;
	private TextField floorTimeInput; //Floor Time input field
	private TextField doorTimeInput; //Door Time Input Field
	private JLabel eventCompletion;  //Count of number of completed events
	private int completeCount;
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
		private Timer timer;
		
		private JButton[] passengerCounters = new JButton[4];
		private int[] passengerCounts = new int[4];
		
		final String TARGET = "TARGET";
		
		
		//Other Fields
		ElevatorSystem eleSystem;
	
	//Scheduler Object
	Scheduler schedulerObj;
		
	
	
	public ElevatorSystemGUI(){
		super("Elevator System");
		
		completeCount = 0;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setLayout(new GridLayout(1,2));
	
		
		JPanel fieldPanel = new JPanel(new GridLayout(SIZE, 1));
		fieldPanel.setBorder(BorderFactory.createCompoundBorder(
			    BorderFactory.createLoweredBevelBorder(),
			    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		eleSystem = new ElevatorSystem();
		
		//LEFT SECTION
				
				//Set timer for display
				//TODO: stop the timer on complete event timer.stop()
				executionTime = new JLabel(" ");
				executionTime.setText("File Execution Time: 0:00");
				ActionListener countDown=new ActionListener()
				{
					int count = 0;
				    public void actionPerformed(ActionEvent e)
				    {
				        count += 100;
				        SimpleDateFormat df=new SimpleDateFormat("mm:ss:S");
				        executionTime.setText("File Execution Time: " + df.format(count));

				    }
				};
				timer=new Timer(100, countDown);
				
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
				
					//File FloorInputFile.txt should be stored in directly in the project folder
					File defaultFile = new File("src\\pack\\FloorInputFile.txt");
					
					//Check if IOFile is null
					if (IOFile == null) {
						IOFile = defaultFile;
					}
					
					System.out.println("THE FILENAME IS: " + IOFile.getName());
					

					//Create Objects
					schedulerObj = new Scheduler(this, doorTime, floorTime, IOFile);
					
					FloorSubsystem floorSubsystem = new FloorSubsystem(schedulerObj, IOFile); 
					schedulerObj.setup(floorSubsystem);

					
					//Initialize threads
					scheduler = new Thread(schedulerObj, "scheduler");
					floor_subsystem = new Thread(floorSubsystem,"floor_subsystem");

					//Start threads
					floor_subsystem.start();
					scheduler.start();
					
					//Start timer display
					timer.start();

				}));
		
				//Stop Button actions ***********CHNAGE
				stopButton = new JButton("Stop");
				stopButton.addActionListener((e -> {
					//Stop the Elevator Threads
					schedulerObj.stopElevatorThreads();
					//Stop the timer display
					timer.stop();
					startButton.setEnabled(false);
							
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
				

				floorTimeInput = new TextField("6");
				//floorTimeInput.addActionListener(this);
				//floorTimeInput.addActionListener();
				
				doorTimeInput = new TextField("6");
				//doorTimeInput.addActionListener(this);
				
				//doorTimeInput.addActionListener();
		
				//Added Button Panel ************CHANGE
				JPanel ButtonPanel = new JPanel(new GridLayout(1, 2));
				fieldPanel.setBorder(BorderFactory.createCompoundBorder(
					    BorderFactory.createLoweredBevelBorder(),
					    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
				
				ButtonPanel.add(startButton);
				ButtonPanel.add(stopButton);
				
				eventCompletion = new JLabel("");
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
		fieldPanel.add(ButtonPanel);
	
		
				
				
		//Right Section

		JPanel buttonPanel = new JPanel(new GridLayout(28, 4)); // change back to 27, 4

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
		
		for(int i = 0; i < passengerCounts.length; i++){
			passengerCounts[i] = 0;
		}
		
		JButton counterLead = new JButton("# People");
		JButton counter1 = new JButton("" + passengerCounts[0]);
		JButton counter2 = new JButton("" + passengerCounts[1]);
		JButton counter3 = new JButton("" + passengerCounts[2]);
		JButton counter4 = new JButton("" + passengerCounts[3]);
		
		passengerCounters[0] = counter1;
		passengerCounters[1] = counter2;
		passengerCounters[2] = counter3;
		passengerCounters[3] = counter4;
		
		counterLead.setEnabled(false);
		buttonPanel.add(counterLead);
		
		for(int i = 0; i < passengerCounts.length; i++){
			passengerCounters[i].setEnabled(false);
			buttonPanel.add(passengerCounters[i]);
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
	 * Method: Updates the number of passengers in elevator n
	 * @param n The elevator to change
	 * @param passCount The number of passengers in the elevator
	 */
	public void setPassengerCount(int n, int passCount) {
		if(passengerCounters != null) {
			if(passengerCounters[n-1] != null) {
				passengerCounters[n-1].setText("" + passCount);
			}
		}
	}
	
	/**
	 * Method: Updates the execution Time of the Elevator System
	 * @param time
	 */
	public void setExecutionTime(String time) {
		executionTime.setText("File Execution Time: " + time);	
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
		System.out.println("State: " + state);
		switch(ElevatorNum + 1) {
			case 1:
				e1State = state;
				elevator1[e1CurrentFloor -1].setText(e1State);
				if(state.equals("Door Stuck")) {
					elevator1[e1CurrentFloor -1].setBackground(Color.yellow);
				}else if(state.equals("OUT_OF_ORDER")) {
					elevator1[e1CurrentFloor -1].setBackground(Color.red);
				}else {
					elevator1[e1CurrentFloor -1].setBackground(null);
				}

				break;
			case 2:
				e2State = state;;
				elevator2[e2CurrentFloor -1].setText(e2State);
				if(state.equals("Door Stuck")) {
					elevator2[e2CurrentFloor -1].setBackground(Color.yellow);
				}else if(state.equals("OUT_OF_ORDER")) {
					elevator2[e2CurrentFloor -1].setBackground(Color.red);
				}else {
					elevator2[e2CurrentFloor -1].setBackground(null);
				}
				break;
			case 3:
				e3State = state;
				elevator3[e3CurrentFloor -1].setText(e3State);
				if(state.equals("Door Stuck")) {
					elevator3[e3CurrentFloor -1].setBackground(Color.yellow);
				}else if(state.equals("OUT_OF_ORDER")) {
					elevator3[e3CurrentFloor -1].setBackground(Color.red);
				}else {
					elevator3[e3CurrentFloor -1].setBackground(null);
				}
				break;
			case 4:
				e4State = state;
				elevator4[e4CurrentFloor -1].setText(e4State);
				if(state.equals("Door Stuck")) {
					elevator4[e4CurrentFloor -1].setBackground(Color.yellow);
				}else if(state.equals("OUT_OF_ORDER")) {
					elevator4[e4CurrentFloor -1].setBackground(Color.red);
				}else {
					elevator4[e4CurrentFloor -1].setBackground(null);
				}
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
	
	/**
	 * Method: Test Method
	 */
	public void stopTimer() {
		if(timer != null) {
			timer.stop();
			System.out.println("Timer stopped!");
			System.out.println("Final time: " + executionTime.getText());
		}else {
			System.out.println("ERROR: timer is null!");
		}
	}
	
	/**
	 * Method: Test Method
	 */
	public void setCompleteCount(int count, int total) {
		if(eventCompletion != null) {
			eventCompletion.setText("Completed Events: " + count + "/" + total);
		}else {
			System.out.println("ERROR: completed events null");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	
}
