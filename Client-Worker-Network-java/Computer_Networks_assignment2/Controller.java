/*
 * Author
 * Name: Steven Bondaruk
 * Student ID: 20333385
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.util.ArrayList;

public class Controller extends Node {
	static final int PORT = 54321;
    static final String CLAPTOP_IP = "172.30.0.2";
    static final String CLAPTOP_IP2 = "172.30.0.2";
    static final String PLAPTOP_IP = "172.30.0.4";
    static final String PLAPTOP_IP2 = "172.30.0.4";
    static final String MLAPTOP_IP = "172.30.0.6";
    static final String MLAPTOP_IP2 = "172.30.0.6";
    static final String GW1_IP = "172.31.0.2";
    static final String GW1_IP2 = "172.31.0.2";
    static final String GW2_IP = "172.31.0.5";
    static final String GW2_IP2 = "172.31.0.5";
    static final String GW3_IP = "172.31.0.6";
    static final String GW3_IP2 = "172.31.0.6";
    static final String ISP_IP = "172.31.0.3";
    static final String ISP_IP2 = "172.32.0.2";
    static final String CP_IP = "172.32.0.3";
    static final String DSERVER1_IP = "172.32.0.4";
	InetSocketAddress dstAddress;
    String ipName;
    String nodeName;
    String nodeString;
    String dest;

    ArrayList<String []> table = new ArrayList<String[]>();

	/**
	 * Constructor
	 * Attempts to create socket at given port and create an InetSocketAddress for the destinations
	 */
	Controller(int srcPort) {
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

			if (content.getType()==PacketContent.CONTROLLER) {
				System.out.println("File name: " + ((ControllerContent)content).getFileName());
                nodeString = ((ControllerContent)content).getFileName();
                String [] arr = nodeString.split(",");
                nodeName = arr[0];
                dest = arr[1];

                boolean found = false;
                int i = 0;
                while(found == false && i < table.size())
                {
                    String [] node = table.get(i);
                    if(nodeName.equals(node[0]))
                    {
                        String adjacentNode;
                        if(nodeName.equals("ISP") && dest.equals("server")) {
                            adjacentNode = node[3];
                        }
                        else if(nodeName.equals("ISP") && dest.equals("claptop")) {
                            adjacentNode = node[4];
                        }
                        else if(nodeName.equals("ISP") && dest.equals("plaptop")) {
                            adjacentNode = node[5];
                        }
                        else if(nodeName.equals("ISP") && dest.equals("mlaptop")) {
                            adjacentNode = node[6];
                        }
                        else if(dest.equalsIgnoreCase("server")) {adjacentNode = node[3];}
                        else {adjacentNode = node[4];}
                        for(int j = 0; j < table.size() && found != true; j++)
                        {
                            node = table.get(j);
                            if(adjacentNode.equals(node[0])){
                                if(dest.equals("server")) {ipName = node[1] + "," + dest;}
                                else {ipName = node[2] + "," + dest;} 
                                found = true;
                            }
                        }
                    }
                    i++;
                }
                if(found != true) {System.out.println("There are no adjacent nodes");}
                else
                {
                    if(nodeName.equals("GW1"))
                    {
                        GW1Content fcontent;
                        DatagramPacket forwardPacket= null;

                        InetAddress ip = InetAddress.getByName(GW1_IP);
                        dstAddress= new InetSocketAddress(ip, PORT);
                        fcontent= new GW1Content(ipName);
                        forwardPacket= fcontent.toDatagramPacket();
                        forwardPacket.setSocketAddress(dstAddress);
                        socket.send(forwardPacket);
                        
                        System.out.println("Sent packet with address for next node: " + ipName);
                    }
                    else if(nodeName.equals("GW2"))
                    {
                        GW2Content fcontent;
                        DatagramPacket forwardPacket= null;

                        InetAddress ip = InetAddress.getByName(GW2_IP);
                        dstAddress= new InetSocketAddress(ip, PORT);
                        fcontent= new GW2Content(ipName);
                        forwardPacket= fcontent.toDatagramPacket();
                        forwardPacket.setSocketAddress(dstAddress);
                        socket.send(forwardPacket);
                        
                        System.out.println("Sent packet with address for next node: " + ipName);
                    }
                    else if(nodeName.equals("GW3"))
                    {
                        GW3Content fcontent;
                        DatagramPacket forwardPacket= null;

                        InetAddress ip = InetAddress.getByName(GW3_IP);
                        dstAddress= new InetSocketAddress(ip, PORT);
                        fcontent= new GW3Content(ipName);
                        forwardPacket= fcontent.toDatagramPacket();
                        forwardPacket.setSocketAddress(dstAddress);
                        socket.send(forwardPacket);
                        
                        System.out.println("Sent packet with address for next node: " + ipName);
                    }
                    else if(nodeName.equals("ISP"))
                    {
                        ISPContent fcontent;
                        DatagramPacket forwardPacket= null;

                        InetAddress ip = InetAddress.getByName(ISP_IP);
                        dstAddress= new InetSocketAddress(ip, PORT);
                        fcontent= new ISPContent(ipName);
                        forwardPacket= fcontent.toDatagramPacket();
                        forwardPacket.setSocketAddress(dstAddress);
                        socket.send(forwardPacket);
                        
                        System.out.println("Sent packet with address for next node: " + ipName);
                    }
                    else if(nodeName.equals("CP"))
                    {
                        CPContent fcontent;
                        DatagramPacket forwardPacket= null;

                        InetAddress ip = InetAddress.getByName(CP_IP);
                        dstAddress= new InetSocketAddress(ip, PORT);
                        fcontent= new CPContent(ipName);
                        forwardPacket= fcontent.toDatagramPacket();
                        forwardPacket.setSocketAddress(dstAddress);
                        socket.send(forwardPacket);
                        
                        System.out.println("Sent packet with address for next node: " + ipName);
                    }
                    //this.notify();
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
        String [] forwarder1 = { "CLAPTOP", CLAPTOP_IP, CLAPTOP_IP2, "GW1" };
        table.add(forwarder1);
        String [] forwarder2 = { "PLAPTOP", PLAPTOP_IP, PLAPTOP_IP2, "GW2" };
        table.add(forwarder2);
        String [] forwarder3 = { "MLAPTOP", MLAPTOP_IP, MLAPTOP_IP2, "GW3" };
        table.add(forwarder3);
        String [] forwarder4 = { "GW1", GW1_IP, GW1_IP2, "ISP", "CLAPTOP" };
        table.add(forwarder4);
        String [] forwarder5 = { "GW2", GW2_IP, GW2_IP2, "ISP", "PLAPTOP" };
        table.add(forwarder5);
        String [] forwarder6 = { "GW3", GW3_IP, GW3_IP2, "ISP", "MLAPTOP" };
        table.add(forwarder6);
        String [] forwarder7 = { "ISP", ISP_IP, ISP_IP2, "CP", "GW1", "GW2", "GW3" };
        table.add(forwarder7);
        String [] forwarder8 = { "CP", CP_IP, "", "DSERVER1", "ISP" };
        table.add(forwarder8);
        String [] forwarder9 = { "DSERVER1", DSERVER1_IP, "", "CP" };
        table.add(forwarder9);
		System.out.println("Waiting for contact");
		this.wait();
	}

	public static void main(String[] args) {
		try {
			(new Controller(PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
