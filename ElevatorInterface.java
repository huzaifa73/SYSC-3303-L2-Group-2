package pack1;
import java.io.*;
import java.net.*;


//make a method to 

public class ElevatorInterface {
	
	
//this class will act as an intermediary between elevator and schedular, need 2 sockets and packets (send and receive)
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;
	Elevator elle; //elevator object
	private int port; //need local port variable
	private int elevatorID;
	private InetAddress address,
	
	
	
	public ElevatorInterface(int port, int elevatorID) {
		
		elle = new Elevator(elevatorID);
		receiveSocket = new DatagramSocket(port); //instantiate with 23 for schedular port
		sendSocket = new DatagramSocket(); //instantiate
		
		this.port = port; 

		
	}
	
	//receive information from elevator, send to schedular
	public void send() {
		
		//take in event from elevator, process, send to schedular
		//receive datapacket, validate, call Event.RebuildEvent() to get the event
		
		Event eventToSend = elevator.sendEvent();   //not sure if this should be elevator.intializeinfotosend instead or not, basically the elevator function that sends the data
		
		address = InetAddress.getLocalHost();
		
		//call function from Event that converts the ElevatorObject to byte array and assign it to a local variable 
		try {
			byte msg[] = Event.buildByteArray(eventToSend);
			sendPacket = new DatagramPacket(msg, msg.length, address, 1999); //1999 is port for schedular
			
		}
		catch (IOException ee) {
			ee.printStackTrace();
			
		}
		
		
		try {
			sendSocket.send(sendPacket);
		}
		catch (IOException e){
			e.printStackTrace();
			System.exit(1);

		}
		
		
	}
	
	//receive an event from schedular in byte format, convert to event, send said event to elevator
	public void receive() {
		
			//instatiate data and receivepacket
            byte data[] = new byte[100];
            receivePacket = new DatagramPacket(data, data.length);
            Event eventToSend;

            
            //receive a packet
            try {   
                receiveSocket.receive(receivePacket);
            
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
            
            //sleep
            try { 
                Thread.sleep(5000);
            } catch (InterruptedException e ) {
                e.printStackTrace();
                System.exit(1);
            }

            
           //get data from packet
           data = receivePacket.getData();
           
           //call Event function rebuildEvent that will convert the byte into a Event object
           eventToSend = Event.rebuildEvent(data); //
           
           elevator.readInfo(data);
        
  
            sendPacket = new DatagramPacket(data, receivePacket.getLength(),
                    receivePacket.getAddress(), receivePacket.getPort());

            //send packet
            try {
                sendSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
     
            
            
        //receive datapacket, validate, call Event.RebuildEvent() to get the event
            
             
		
	}
	
	//close the sockets
	public void closeSockets() {
		sendSocket.close();
		receiveSocket.close();

	}
	
	//run method
	public void run() {
		receive();
		sleep(500);
	}
	
	//return method to retreive Elevator
	public Elevator getElevator(){
		return elle;
	
	}
	
	

}
