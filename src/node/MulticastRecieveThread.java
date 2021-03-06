package node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * The thread that listens for multicast messages and handles accordingly.
 */
public class MulticastRecieveThread extends Thread
{
	/**
	 * The object that maintains all sockets.
	 */
	private SocketHandler sHandler;
	/**
	 * The group address of the multicast.
	 */
	private String addr;
	/**
	 * The node that runs the project.
	 */
	private Node node;

	/**
	 * The constructor method for the MulticastReceiveThread.
	 * 
	 * @param group
	 *            The socket for the multicast group.
	 * @param node
	 *            The IP of the multicast.
	 * @param sHandler
	 *            The node that uses the multicastReceiveThread.
	 */
	public MulticastRecieveThread(String group, Node node, SocketHandler sHandler)
	{
		this.addr = group;
		this.node = node;
		this.sHandler = sHandler;
	}

	@Override
	public void run()
	{
		try
		{
			joinMulticast();

			while (true)
				handleData(receiveData());

		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (JSONException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Join Multicast group.
	 * 
	 * @throws IOException Something went wrong while joining the group.
	 * @throws UnknownHostException Something went wrong while joining the group.
	 */
	private void joinMulticast() throws UnknownHostException, IOException
	{
		System.out.println("before join");
		sHandler.getMultiSocket().joinGroup(InetAddress.getByName(this.addr));
		System.out.println("joined group");
	}

	/**
	 * handle data of the JSON string.
	 * 
	 * @param data
	 *            the string that needs to be handled
	 * @throws JSONException
	 *             Something went wrong while parsing the data to a json object.
	 *             possible that the received data was not JSON
	 */
	private void handleData(String data) throws JSONException
	{
		String name;
		System.out.println("received: " + data);
		JSONObject jobj = new JSONObject(data);
		String type = jobj.getString("type");

		switch (type)
		{
		case "next" :
			name = jobj.getString("data");			
			break;
		case "new" :
			name = jobj.getString("data");
			this.node.setNodes(name);
			System.out.println("new node: ");
			this.node.printNodes();
		}

	}

	/**
	 * receive Data from the multicast
	 * 
	 * @return Data sent over UDP parsed as string.
	 * @throws IOException
	 *             Something went wrong while receiving multicast date.
	 */
	private String receiveData() throws IOException
	{
		byte[] buf = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf, buf.length);

		this.sHandler.getMultiSocket().receive(dp);
		System.out.println("lol");

		buf = dp.getData();
		int len = dp.getLength();
		System.out.println((new String(buf)).substring(0, len));
		return (new String(buf)).substring(0, len);

	}

}
