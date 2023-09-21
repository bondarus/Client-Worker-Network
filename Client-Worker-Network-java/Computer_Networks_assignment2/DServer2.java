/*
 * Author
 * Name: Steven Bondaruk
 * Student ID: 20333385
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DServer2 extends Node {
	static final int PORT = 54321;
    //static final String DSERVER1_IP = "172.32.0.4";
	static final String CP_IP = "172.32.0.3";
	static final String ISP_IP = "172.31.0.3";
    InetSocketAddress dstAddress;
	
	DServer2(int port) {
		try {
			socket= new DatagramSocket(port);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

    /**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public void onReceipt(DatagramPacket packet) {
		try {
			System.out.println("DServer2 Received packet");

			PacketContent content= PacketContent.fromDatagramPacket(packet);
			
			if (content.getType()==PacketContent.DSERVER2) {
				String welcome = ((DServer2Content)content).getFileName();
				if(welcome.equalsIgnoreCase("Welcome")){
					System.out.println("CP message: " + ((DServer2Content)content).getFileName());
					DatagramPacket response;
					response= new AckPacketContent("OK - from DServer2").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);
				}
				else{
					System.out.println("File name: " + ((DServer2Content)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from DServer2").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);
					
					ClaptopContent fcontent;
					DatagramPacket returnPacket= null;
					String fname = ((DServer2Content)content).getFileName() + " Response";

					InetAddress ip = InetAddress.getByName(CP_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ClaptopContent(fname);
					returnPacket= fcontent.toDatagramPacket();
					returnPacket.setSocketAddress(dstAddress);
					socket.send(returnPacket);
					System.out.println("DServer2 sent Packet to Claptop");
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

    public synchronized void start() throws Exception {
		CPContent fcontent;
		DatagramPacket packet= null;
		String fname = "Hello, I am DServer2";

		InetAddress ip = InetAddress.getByName(CP_IP);
		dstAddress= new InetSocketAddress(ip, PORT);
		fcontent= new CPContent(fname);
		packet= fcontent.toDatagramPacket();
		packet.setSocketAddress(dstAddress);
		socket.send(packet);

		System.out.println("DServer2 Waiting for contact");
		this.wait();
	}

	public static void main(String[] args) {
		try {
			(new DServer2(PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}