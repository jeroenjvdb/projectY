package node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

public class ReceiveUDPThread extends Thread {
	private DatagramSocket serverSocket;
	private Node node;

	/**
	 * The constructor method for the ReceiveUDPThread
	 * 
	 * @param node
	 *            The node that receives the data.
	 */
	public ReceiveUDPThread(Node node) {
		try {
			this.node = node;
			this.serverSocket = new DatagramSocket(6789);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				System.out.println("receiveUDPThread run");
				DatagramPacket data = getData(serverSocket);
				String ip = data.getAddress().getHostAddress();

				handleData(new String(data.getData()), ip);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * receive data from the socket.
	 * 
	 * @param socket
	 *            the socket over which the data comes.
	 * @return the received data
	 * @throws IOException
	 */
	private DatagramPacket getData(DatagramSocket socket) throws IOException {
		byte[] receiveData = new byte[10240];
		byte[] sendData = new byte[1024];

		System.out.println("rft receiveUDP");

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		System.out.println("waiting for udp receive");
		socket.receive(receivePacket);
		System.out.println("UDP received");
		
		//reply();

		return receivePacket;
	}

	/**
	 * Handle JSON data on type. handleData checks for the given type in the
	 * JSON message. starts the TCP receiveThread when it gets file, start the
	 * control files method when the type is next.
	 * 
	 * @param data
	 *            the unparsed JSON string
	 * @param ip
	 *            the ip of the UDP sender
	 * @throws JSONException
	 * @throws IOException
	 */
	private void handleData(String data, String ip) throws JSONException, IOException {
		JSONObject jobj = new JSONObject(data);

		String type = jobj.getString("type");
		System.out.println("type: "  + type);
		switch (type) {
		case "file":
			System.out.println("receive file");
			TCPReceive receive = new TCPReceive(node, 5555);
			String name = jobj.getString("data");
			int size = (int) jobj.getLong("size");
			System.out.println("the name is: " + name);
			receive.receiveFile(ip, name, size);
			break;
		case "next":
			this.node.controlFiles();
			break;
		}

	}

}