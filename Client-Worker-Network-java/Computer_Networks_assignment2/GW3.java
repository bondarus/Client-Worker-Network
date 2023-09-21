/*
 * Author
 * Name: Steven Bondaruk
 * Student ID: 20333385
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.InetAddress;

public class GW3 extends Node {
	static final int PORT = 54321;
	static String CONTROLLER_IP = "172.31.0.4";
	static String MLAPTOP_IP = "unknown";
	static String ISP_IP = "unknown";
	InetSocketAddress dstAddress;
	PacketContent tempContent;
	String tempName;

	boolean server1DestinationKnown = false;
	boolean mlaptopDestinationKnown = false;

	GW3(int srcPort) {
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
					String gatewayName = "GW3,server";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("GW3 requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((DServer1Content)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from GW3").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(ISP_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("GW3 forwarded Packet to ISP");
				}
			}

			if (content.getType()==PacketContent.GW3) {
				String info = ((GW3Content)content).getFileName();
				String arr[] = info.split(",");
				String nextIP = arr[0];
				String dest = arr[1];
				if(dest.equals("server"))
				{
					server1DestinationKnown = true;
					ISP_IP = nextIP;
					DatagramPacket response;
					response= new AckPacketContent("OK - from GW3").toDatagramPacket();
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
					System.out.println("GW3 forwarded Packet to ISP");
				}
				else if(dest.equals("mlaptop"))
				{
					mlaptopDestinationKnown = true;
					MLAPTOP_IP = nextIP;
					DatagramPacket response;
					response= new AckPacketContent("OK - from GW3").toDatagramPacket();
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
					System.out.println("GW3 forwarded Packet to Mlaptop");
				}
			}
			
			if (content.getType()==PacketContent.MLAPTOP) {
				if(mlaptopDestinationKnown == false)
				{
					tempName = ((MlaptopContent)content).getFileName();
					String gatewayName = "GW3,mlaptop";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("GW3 requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((MlaptopContent)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from GW3").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(MLAPTOP_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("GW3 forwarded Packet to Mlaptop");
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
			(new GW3(PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
