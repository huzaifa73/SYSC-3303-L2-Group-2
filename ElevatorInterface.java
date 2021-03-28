package pack;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
* Class is the communication between the elevator and th scheduler
* This class will act as an intermediary between elevator and schedular, need 2 sockets and packets (send and receive)
* By Huzaifa
* @version 3/27/2021
*/

public class ElevatorInterface implements Runnable{
	
	

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
		elevatorThread = new Thread(elle, "Elle-vator"); 
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
		this.elevatorID = elevatorID; 
		elle.setElevatorInterface(this);
	}
	
	//receive information from elevator, send to schedular
	public void send(Event eventToSend) {
		
		//take in event from elevator, process, send to schedular
		//receive datapacket, validate, call Event.RebuildEvent() to get the event
		
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
		printWrapper("Receive ");
			//instatiate data and receivepacket
            byte data[] = new byte[100];
            receivePacket = new DatagramPacket(data, data.length);
            Event eventToSend;
            printWrapper("Receive 1 ");
            
            //receive a packet
            try {   
            	printWrapper("Receive 2");
                receiveSocket.receive(receivePacket);
            
            } catch (IOException e) {
            	printWrapper("Receive error ");
                e.printStackTrace();
                System.exit(1);
            }
            printWrapper("Receive 3 ");
            //sleep
            try { 
                Thread.sleep(500);
            } catch (InterruptedException e ) {
                e.printStackTrace();
                System.exit(1);
            }

            printWrapper("Receive 4");
           //get data from packet
           data = receivePacket.getData();
           printWrapper("Receive 5 ");
           //call Event function rebuildEvent that will convert the byte into a Event object
           eventToSend = Event.rebuildEvent(data); //
           
           elle.readInfo(eventToSend);
           
           

		
	}
	
	//close the sockets
	public void closeSockets() {
		sendSocket.close();
		receiveSocket.close();

	}
	
	//run method
	public void run() {
		if(elle.getSystemError() == SystemError.NO_ERROR) {
			receive();
		}else {
			//printWrapper("Elevator out of order");
		}
		//send();
		
		
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
	
	 private void printWrapper(String msg) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		    LocalDateTime now = LocalDateTime.now();
				
			System.out.println("_____________________________________________________");
			System.out.println("                Elevator Interface: " + elevatorID);
			System.out.println("-----------------------------------------------------");
			System.out.println("Log at time: " + dtf.format(now));
			System.out.println(msg);
			System.out.println("_____________________________________________________");
	}

}
