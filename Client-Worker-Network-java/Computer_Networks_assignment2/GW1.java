/*
 * Author
 * Name: Steven Bondaruk
 * Student ID: 20333385
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.InetAddress;

public class GW1 extends Node {
	static final int PORT = 54321;
	static String CONTROLLER_IP = "172.31.0.4";
	static String CLAPTOP_IP = "unknown";
	static String ISP_IP = "unknown";
	InetSocketAddress dstAddress;
	PacketContent tempContent;
	String tempName;

	boolean server1DestinationKnown = false;
	boolean claptopDestinationKnown = false;

	GW1(int srcPort) {
		try {
			socket= new DatagramSocket(srcPort);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public void onReceipt(DatagramPacket packet) {
		try {
			System.out.println("Received packet");

			PacketContent content= PacketContent.fromDatagramPacket(packet);
			
			if (content.getType()==PacketContent.DSERVER1) {
				if(server1DestinationKnown == false)
				{
					tempName = ((DServer1Content)content).getFileName();
					String gatewayName = "GW1,server";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("GW1 requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((DServer1Content)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from GW1").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(ISP_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("GW1 forwarded Packet to ISP");
				}
			}

			if (content.getType()==PacketContent.GW1) {
				String info = ((GW1Content)content).getFileName();
				String arr[] = info.split(",");
				String nextIP = arr[0];
				String dest = arr[1];
				if(dest.equals("server"))
				{
					server1DestinationKnown = true;
					ISP_IP = nextIP;
					DatagramPacket response;
					response= new AckPacketContent("OK - from GW1").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					System.out.println("Received ip address from controller: " + nextIP);
					DServer1Content fcontent;
					DatagramPacket forwardPacket= null;

					InetAddress ip = InetAddress.getByName(nextIP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new DServer1Content(tempName);
					forwardPacket= fcontent.toDatagramPacket();
					forwardPacket.setSocketAddress(dstAddress);
					socket.send(forwardPacket);
					System.out.println("GW1 forwarded Packet to ISP");
				}
				else if(dest.equals("claptop"))
				{
					claptopDestinationKnown = true;
					CLAPTOP_IP = nextIP;
					DatagramPacket response;
					response= new AckPacketContent("OK - from GW1").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					System.out.println("Received ip address from controller: " + nextIP);
					ClaptopContent fcontent;
					DatagramPacket forwardPacket= null;

					InetAddress ip = InetAddress.getByName(nextIP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ClaptopContent(tempName);
					forwardPacket= fcontent.toDatagramPacket();
					forwardPacket.setSocketAddress(dstAddress);
					socket.send(forwardPacket);
					System.out.println("GW1 forwarded Packet to Claptop");
				}
			}

			if (content.getType()==PacketContent.CLAPTOP) {
				if(claptopDestinationKnown == false)
				{
					tempName = ((ClaptopContent)content).getFileName();
					String gatewayName = "GW1,claptop";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("GW1 requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((ClaptopContent)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from GW1").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(CLAPTOP_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("GW1 forwarded Packet to Claptop");
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	/**
	 * Sender Method
	 *
	 */
	public synchronized void start() throws Exception {
		System.out.println("Waiting for contact");
		//System.out.println(InetAddress.getLocalHost().getHostAddress());
		this.wait();
	}

	public static void main(String[] args) {
		try {
			(new GW1(PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
