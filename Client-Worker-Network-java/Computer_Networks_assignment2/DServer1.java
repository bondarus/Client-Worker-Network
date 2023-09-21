/*
 * Author
 * Name: Steven Bondaruk
 * Student ID: 20333385
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.InetAddress;

public class DServer1 extends Node {
	static final int PORT = 54321;
	static final String CP_IP = "172.32.0.3";
	static final String ISP_IP = "172.31.0.3";
    InetSocketAddress dstAddress;
	
	DServer1(int port) {
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
			System.out.println("DServer1 Received packet");

			PacketContent content= PacketContent.fromDatagramPacket(packet);
			
			if (content.getType()==PacketContent.DSERVER1) {
				String welcome = ((DServer1Content)content).getFileName();
				if(welcome.equalsIgnoreCase("Welcome")){
					System.out.println("CP message: " + ((DServer1Content)content).getFileName());
					DatagramPacket response;
					response= new AckPacketContent("OK - from DServer1").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);
				}
				else{
					System.out.println("File name: " + ((DServer1Content)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from DServer1").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					String info = ((DServer1Content)content).getFileName();
					String arr[] = info.split(",");
					String message = arr[0];
					String node = arr[1];
					
					if(node.equalsIgnoreCase("claptop")) {
						ClaptopContent fcontent;
						DatagramPacket returnPacket= null;
						String fname = message + " Response";

						InetAddress ip = InetAddress.getByName(CP_IP);
						dstAddress= new InetSocketAddress(ip, PORT);
						fcontent= new ClaptopContent(fname);
						returnPacket= fcontent.toDatagramPacket();
						returnPacket.setSocketAddress(dstAddress);
						socket.send(returnPacket);
						System.out.println("DServer1 sent Packet to Claptop");
					}
					else if(node.equalsIgnoreCase("plaptop")) {
						PlaptopContent fcontent;
						DatagramPacket returnPacket= null;
						String fname = message + " Response";

						InetAddress ip = InetAddress.getByName(CP_IP);
						dstAddress= new InetSocketAddress(ip, PORT);
						fcontent= new PlaptopContent(fname);
						returnPacket= fcontent.toDatagramPacket();
						returnPacket.setSocketAddress(dstAddress);
						socket.send(returnPacket);
						System.out.println("DServer1 sent Packet to Plaptop");
					}
					else {
						MlaptopContent fcontent;
						DatagramPacket returnPacket= null;
						String fname = message + " Response";

						InetAddress ip = InetAddress.getByName(CP_IP);
						dstAddress= new InetSocketAddress(ip, PORT);
						fcontent= new MlaptopContent(fname);
						returnPacket= fcontent.toDatagramPacket();
						returnPacket.setSocketAddress(dstAddress);
						socket.send(returnPacket);
						System.out.println("DServer1 sent Packet to Mlaptop");
					}
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

    public synchronized void start() throws Exception {
		CPContent fcontent;
		DatagramPacket packet= null;
		String fname = "Hello, I am DServer1";

		InetAddress ip = InetAddress.getByName(CP_IP);
		dstAddress= new InetSocketAddress(ip, PORT);
		fcontent= new CPContent(fname);
		packet= fcontent.toDatagramPacket();
		packet.setSocketAddress(dstAddress);
		socket.send(packet);

		System.out.println("DServer1 Waiting for contact");
		this.wait();
	}

	public static void main(String[] args) {
		try {
			(new DServer1(PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}