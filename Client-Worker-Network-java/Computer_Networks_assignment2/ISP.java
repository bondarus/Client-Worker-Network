/*
 * Author
 * Name: Steven Bondaruk
 * Student ID: 20333385
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.InetAddress;

public class ISP extends Node {
    static final int PORT = 54321;
	static String CONTROLLER_IP = "172.31.0.4";
	static String GW1_IP = "unknown";
	static String GW2_IP = "unknown";
	static String GW3_IP = "unknown";
	static String CP_IP = "unknwon";
	InetSocketAddress dstAddress;
	PacketContent tempContent;
	String tempName;

	boolean server1DestinationKnown = false;
	boolean claptopDestinationKnown = false;
	boolean plaptopDestinationKnown = false;
	boolean mlaptopDestinationKnown = false;

	ISP(int srcPort) {
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
					String gatewayName = "ISP,server";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("ISP requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((DServer1Content)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from ISP").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(CP_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("ISP forwarded Packet to CP");
				}
			}

			if (content.getType()==PacketContent.ISP) {
				String info = ((ISPContent)content).getFileName();
				String arr[] = info.split(",");
				String nextIP = arr[0];
				String dest = arr[1];
				if(dest.equalsIgnoreCase("server"))
				{
					server1DestinationKnown = true;
					CP_IP = nextIP;
					DatagramPacket response;
					response= new AckPacketContent("OK - from ISP").toDatagramPacket();
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
					System.out.println("ISP forwarded Packet to CP");
				}
				else if(dest.equalsIgnoreCase("claptop"))
				{
					claptopDestinationKnown = true;
					GW1_IP = nextIP;
					DatagramPacket response;
					response= new AckPacketContent("OK - from ISP").toDatagramPacket();
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
					System.out.println("ISP forwarded Packet to GW1");
				}
				else if(dest.equalsIgnoreCase("plaptop"))
				{
					plaptopDestinationKnown = true;
					GW2_IP = nextIP;
					DatagramPacket response;
					response= new AckPacketContent("OK - from ISP").toDatagramPacket();
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
					System.out.println("ISP forwarded Packet to GW2");
				}
				else
				{
					mlaptopDestinationKnown = true;
					GW3_IP = nextIP;
					DatagramPacket response;
					response= new AckPacketContent("OK - from ISP").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					System.out.println("Received ip address from controller: " + nextIP);
					MlaptopContent fcontent;
					DatagramPacket forwardPacket= null;

					InetAddress ip = InetAddress.getByName(nextIP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new MlaptopContent(tempName);
					forwardPacket= fcontent.toDatagramPacket();
					forwardPacket.setSocketAddress(dstAddress);
					socket.send(forwardPacket);
					System.out.println("ISP forwarded Packet to GW3");
				}
			}
			
			if (content.getType()==PacketContent.CLAPTOP) {
				if(claptopDestinationKnown == false)
				{
					tempName = ((ClaptopContent)content).getFileName();
					String gatewayName = "ISP,claptop";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("ISP requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((ClaptopContent)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from ISP").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(GW1_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("ISP forwarded Packet to GW1");
				}
			}

			if (content.getType()==PacketContent.PLAPTOP) {
				if(plaptopDestinationKnown == false)
				{
					tempName = ((PlaptopContent)content).getFileName();
					String gatewayName = "ISP,plaptop";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("ISP requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((PlaptopContent)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from ISP").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(GW2_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("ISP forwarded Packet to GW2");
				}
			}

			if (content.getType()==PacketContent.MLAPTOP) {
				if(mlaptopDestinationKnown == false)
				{
					tempName = ((MlaptopContent)content).getFileName();
					String gatewayName = "ISP,mlaptop";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("ISP requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((MlaptopContent)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from ISP").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(GW3_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("ISP forwarded Packet to GW3");
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
		this.wait();
	}

	public static void main(String[] args) {
		try {
			(new ISP(PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
