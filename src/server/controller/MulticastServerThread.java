package server.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;

public class MulticastServerThread extends Thread {
	private Wrapper wrap;

	public MulticastServerThread(Wrapper wrap)
	{
		this.wrap = wrap;
	}
	
	public void run()
	{
		String groupIP = "224.0.0.0";
        int portMulticasting = 4446;
        MulticastSocket socket;
        
        DatagramSocket socketUni = null;
		try {
			socketUni = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        InetAddress group;
        //if (args.length > 0)
        //    groupIP = args[0];
        try{
            //get a multicast socket and join group
            socket = new MulticastSocket(portMulticasting);
            group = InetAddress.getByName(groupIP);
            socket.joinGroup(group);
            //get packet
            DatagramPacket packet;
            boolean is_true = true;
            while (is_true){
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf,buf.length);
                socket.receive(packet);
                buf = packet.getData();
                int len = packet.getLength();
                String received = (new String(buf)).substring(0,len);
                try{
                	this.wrap.createNode(received, packet.getAddress().getHostAddress());
                    System.out.println("Agent name: " + received + " (" + packet.getAddress() + ")");
                    
                    buf = new byte[256];
                    buf = String.valueOf(wrap.getCount()).getBytes();
                    packet = new DatagramPacket(buf , buf.length, packet.getAddress(), 3000);
                    socketUni.send(packet);
                } catch (NumberFormatException e){
                    System.out.println("cannot interpret number");
                }
            }
            socket.leaveGroup(group);
            socket.close();
            
        } catch (IOException e){
            e.printStackTrace();
        } finally {
        	
        }
	}
}