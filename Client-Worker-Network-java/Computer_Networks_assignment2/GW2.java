/*
 * Author
 * Name: Steven Bondaruk
 * Student ID: 20333385
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.InetAddress;

public class GW2 extends Node {
	static final int PORT = 54321;
	static String CONTROLLER_IP = "172.31.0.4";
	static String PLAPTOP_IP = "unknown";
	static String ISP_IP = "unknown";
	InetSocketAddress dstAddress;
	PacketContent tempContent;
	String tempName;

	boolean server1DestinationKnown = false;
	boolean plaptopDestinationKnown = false;

	GW2(int srcPort) {
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
					String gatewayName = "GW2,server";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("GW2 requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((DServer1Content)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from GW2").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(ISP_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("GW2 forwarded Packet to ISP");
				}
			}

			if (content.getType()==PacketContent.GW2) {
				String info = ((GW2Content)content).getFileName();
				String arr[] = info.split(",");
				String nextIP = arr[0];
				String dest = arr[1];
				if(dest.equals("server"))
				{
					server1DestinationKnown = true;
					ISP_IP = nextIP;
					DatagramPacket response;
					response= new AckPacketContent("OK - from GW2").toDatagramPacket();
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
					System.out.println("GW2 forwarded Packet to ISP");
				}
				else if(dest.equals("plaptop"))
				{
					plaptopDestinationKnown = true;
					PLAPTOP_IP = nextIP;
					DatagramPacket response;
					response= new AckPacketContent("OK - from GW2").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					System.out.println("Received ip address from controller: " + nextIP);
					PlaptopContent fcontent;
					DatagramPacket forwardPacket= null;

					InetAddress ip = InetAddress.getByName(nextIP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new PlaptopContent(tempName);
					forwardPacket= fcontent.toDatagramPacket();
					forwardPacket.setSocketAddress(dstAddress);
					socket.send(forwardPacket);
					System.out.println("GW2 forwarded Packet to Plaptop");
				}
			}
			
			if (content.getType()==PacketContent.PLAPTOP) {
				if(plaptopDestinationKnown == false)
				{
					tempName = ((PlaptopContent)content).getFileName();
					String gatewayName = "GW2,plaptop";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("GW2 requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((PlaptopContent)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from GW2").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(PLAPTOP_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("GW2 forwarded Packet to Plaptop");
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
			(new GW2(PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
