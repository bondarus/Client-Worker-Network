/*
 * Author
 * Name: Steven Bondaruk
 * Student ID: 20333385
 */

import java.net.DatagramPacket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * The class is the basis for packet contents of various types.
 *
 *
 */
public abstract class PacketContent {

	public static final int ACKPACKET= 1;
	public static final int CLAPTOP= 10;
	public static final int PLAPTOP= 11;
	public static final int MLAPTOP= 12;
	public static final int GW1= 20;
	public static final int GW2= 21;
	public static final int GW3= 22;
	public static final int ISP= 30;
	public static final int CP= 40;
	public static final int DSERVER1= 50;
	public static final int DSERVER2= 51;
	public static final int CONTROLLER= 200;

	int type= 0;

	/**
	 * Constructs an object out of a datagram packet.
	 * @param packet Packet to analyse.
	 */
	public static PacketContent fromDatagramPacket(DatagramPacket packet) {
		PacketContent content= null;

		try {
			int type;

			byte[] data;
			ByteArrayInputStream bin;
			ObjectInputStream oin;

			data= packet.getData();  // use packet content as seed for stream
			bin= new ByteArrayInputStream(data);
			oin= new ObjectInputStream(bin);

			type= oin.readInt();  // read type from beginning of packet

			switch(type) {   // depending on type create content object
			case ACKPACKET:
				content= new AckPacketContent(oin);
				break;
			case CLAPTOP:
				content= new ClaptopContent(oin);
				break;
			case PLAPTOP:
				content= new PlaptopContent(oin);
				break;
			case MLAPTOP:
				content= new MlaptopContent(oin);
				break;
			case GW1:
				content= new GW1Content(oin);
				break;
			case GW2:
				content= new GW2Content(oin);
				break;
			case GW3:
				content= new GW3Content(oin);
				break;
			case ISP:
				content= new ISPContent(oin);
				break;
			case CP:
				content= new CPContent(oin);
				break;
			case DSERVER1:
				content= new DServer1Content(oin);
				break;
			case DSERVER2:
				content= new DServer2Content(oin);
				break;
			case CONTROLLER:
				content= new ControllerContent(oin);
				break;
			default:
				content= null;
				break;
			}
			oin.close();
			bin.close();

		}
		catch(Exception e) {e.printStackTrace();}

		return content;
	}


	/**
	 * This method is used to transform content into an output stream.
	 *
	 * @param out Stream to write the content for the packet to.
	 */
	protected abstract void toObjectOutputStream(ObjectOutputStream out);

	/**
	 * Returns the content of the object as DatagramPacket.
	 *
	 * @return Returns the content of the object as DatagramPacket.
	 */
	public DatagramPacket toDatagramPacket() {
		DatagramPacket packet= null;

		try {
			ByteArrayOutputStream bout;
			ObjectOutputStream oout;
			byte[] data;

			bout= new ByteArrayOutputStream();
			oout= new ObjectOutputStream(bout);

			oout.writeInt(type);         // write type to stream
			toObjectOutputStream(oout);  // write content to stream depending on type

			oout.flush();
			data= bout.toByteArray(); // convert content to byte array

			packet= new DatagramPacket(data, data.length); // create packet from byte array
			oout.close();
			bout.close();
		}
		catch(Exception e) {e.printStackTrace();}

		return packet;
	}


	/**
	 * Returns the content of the packet as String.
	 *
	 * @return Returns the content of the packet as String.
	 */
	public abstract String toString();

	/**
	 * Returns the type of the packet.
	 *
	 * @return Returns the type of the packet.
	 */
	public int getType() {
		return type;
	}

}
