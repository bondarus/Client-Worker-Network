/*
 * Author
 * Name: Steven Bondaruk
 * Student ID: 20333385
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.InetAddress;

public class CP extends Node {
    static final int PORT = 54321;
	static String CONTROLLER_IP = "172.32.0.5";
	static String ISP_IP = "unknown";
	static String DSERVER1_IP = "172.32.0.4";
	InetSocketAddress dstAddress;
	PacketContent tempContent;
	String tempName;

	boolean server1DestinationKnown = false;
	boolean claptopDestinationKnown = false;
	boolean plaptopDestinationKnown = false;
	boolean mlaptopDestinationKnown = false;

	CP(int srcPort) {
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
					String gatewayName = "CP,server";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("CP requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((DServer1Content)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from CP").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(DSERVER1_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("CP forwarded Packet to DServer1");
				}
			}

			if (content.getType()==PacketContent.CP) {
				String info = ((CPContent)content).getFileName();
				String arr[] = info.split(",");
				String submessage = info.substring(0, 5);
				String nextIP = arr[0];
				String dest = arr[1];
				if(submessage.equalsIgnoreCase("Hello"))
				{
					System.out.println("DServer message: " + ((CPContent)content).getFileName());
					DServer1Content fcontent;
					DatagramPacket returnPacket= null;
					String fname = "Welcome";

					InetAddress ip = InetAddress.getByName(DSERVER1_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new DServer1Content(fname);
					returnPacket= fcontent.toDatagramPacket();
					returnPacket.setSocketAddress(dstAddress);
					socket.send(returnPacket);
				}
				else if(dest.equals("server"))
				{
					server1DestinationKnown = true;
					DSERVER1_IP = nextIP;
					DatagramPacket response;
					response= new AckPacketContent("OK - from CP").toDatagramPacket();
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
					System.out.println("CP forwarded Packet to DServer1");
				}
				else if(dest.equals("claptop"))
				{
					claptopDestinationKnown = true;
					ISP_IP = nextIP;
					DatagramPacket response;
					response= new AckPacketContent("OK - from CP").toDatagramPacket();
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
					System.out.println("CP forwarded Packet to ISP");
				}
				else if(dest.equals("plaptop"))
				{
					plaptopDestinationKnown = true;
					ISP_IP = nextIP;
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
					System.out.println("CP forwarded Packet to ISP");
				}
				else
				{
					mlaptopDestinationKnown = true;
					ISP_IP = nextIP;
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
					System.out.println("CP forwarded Packet to ISP");
				}
			}
			
			if (content.getType()==PacketContent.CLAPTOP) {
				if(claptopDestinationKnown == false)
				{
					tempName = ((ClaptopContent)content).getFileName();
					String gatewayName = "CP,claptop";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("CP requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((ClaptopContent)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from CP").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(ISP_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("CP forwarded Packet to ISP");
				}
			}

			if (content.getType()==PacketContent.PLAPTOP) {
				if(plaptopDestinationKnown == false)
				{
					tempName = ((PlaptopContent)content).getFileName();
					String gatewayName = "CP,plaptop";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("CP requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((PlaptopContent)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from CP").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(ISP_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("CP forwarded Packet to ISP");
				}
			}

			if (content.getType()==PacketContent.MLAPTOP) {
				if(mlaptopDestinationKnown == false)
				{
					tempName = ((MlaptopContent)content).getFileName();
					String gatewayName = "CP,mlaptop";
					ControllerContent fcontent;
					DatagramPacket requestPacket= null;

					InetAddress ip = InetAddress.getByName(CONTROLLER_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					fcontent= new ControllerContent(gatewayName);
					requestPacket= fcontent.toDatagramPacket();
					requestPacket.setSocketAddress(dstAddress);
					socket.send(requestPacket);
					System.out.println("CP requests address from controller");
				}
				else
				{
					System.out.println("File name: " + ((MlaptopContent)content).getFileName());

					DatagramPacket response;
					response= new AckPacketContent("OK - from CP").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					InetAddress ip = InetAddress.getByName(ISP_IP);
					dstAddress= new InetSocketAddress(ip, PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					System.out.println("CP forwarded Packet to ISP");
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
			(new CP(PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
