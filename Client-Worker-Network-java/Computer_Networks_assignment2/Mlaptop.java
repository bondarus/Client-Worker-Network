/*
 * Author
 * Name: Steven Bondaruk
 * Student ID: 20333385
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.util.Scanner;

public class Mlaptop extends Node {
	static final int PORT = 54321;
	static final String GW3_IP = "172.30.0.7";
	InetSocketAddress dstAddress;

	/**
	 * Constructor
	 */
	Mlaptop(int srcPort) {
		try {
			socket= new DatagramSocket(srcPort);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public synchronized void onReceipt(DatagramPacket packet) {
		try {
			PacketContent content= PacketContent.fromDatagramPacket(packet);

			if (content.getType()==PacketContent.MLAPTOP) {
				System.out.println("File name: " + ((MlaptopContent)content).getFileName());
				System.out.println(content.toString());

				DatagramPacket response;
				response= new AckPacketContent("OK - From Mlaptop to GW3").toDatagramPacket();
				response.setSocketAddress(packet.getSocketAddress());
				socket.send(response);
				this.notify();
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	/**
	 * Sender Method
	 *
	 */
	public synchronized void start() throws Exception {
		//System.out.println(InetAddress.getLocalHost().getHostAddress());
		boolean quit = false;
		while(quit == false) {
			Scanner input = new Scanner(System.in);
			System.out.print("File name request: ");
			String fname = input.next();
			if(fname.equalsIgnoreCase("exit") || 
			   fname.equalsIgnoreCase("quit")) {quit = true;}
			else { 
				DServer1Content fcontent;
				DatagramPacket packet= null;

				InetAddress ip = InetAddress.getByName(GW3_IP);
				dstAddress= new InetSocketAddress(ip, PORT);
				fcontent= new DServer1Content(fname);
				packet= fcontent.toDatagramPacket();
				packet.setSocketAddress(dstAddress);
				socket.send(packet);

				System.out.println("Sending packet to GW3");
				this.wait();
			}
		}
	}

	public static void main(String[] args) {
		try {
			(new Mlaptop(PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
