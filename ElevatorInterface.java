package pack;

import java.io.*;
import java.net.*;


//make a method to 

public class ElevatorInterface implements Runnable{
	
	
//this class will act as an intermediary between elevator and schedular, need 2 sockets and packets (send and receive)
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;
	Elevator elle; //elevator object
	private int port; //need local port variable
	private int elevatorID;
	private InetAddress schedulerAddress;
	
	
	
	public ElevatorInterface(int port, int elevatorID, Scheduler scheduler) {
		
		Thread elevatorThread;
		elle = new Elevator(scheduler, elevatorID);
		
		//Starts Thread for elevator
		elevatorThread = new Thread(scheduler, "scheduler"); 
		elevatorThread.start();
		try {
			receiveSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //instantiate with 23 for schedular port
		try {
			sendSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //instantiate
		
		this.port = port; 

		
	}
	
	//receive information from elevator, send to schedular
	public void send() {
		
		//take in event from elevator, process, send to schedular
		//receive datapacket, validate, call Event.RebuildEvent() to get the event
		
		Event eventToSend = elle.getEvent();   //not sure if this should be elevator.intializeinfotosend instead or not, basically the elevator function that sends the data
		
		try {
			schedulerAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		byte msg[] = Event.buildByteArray(eventToSend);
		sendPacket = new DatagramPacket(msg, msg.length, schedulerAddress, 1999); //1999 is port for schedular
		
		
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
           
           elle.readInfo(eventToSend);
        
           
           send();
		
	}
	
	//close the sockets
	public void closeSockets() {
		sendSocket.close();
		receiveSocket.close();

	}
	
	//run method
	public void run() {
		receive();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//return method to retreive Elevator
	public Elevator getElevator(){
		return elle;
	
	}
	
	//return the port number of the elevator
	public int getPort(){
		return port;
	}
	
	

}