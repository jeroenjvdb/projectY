package server.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import interfaces.IWrapper;

public class Wrapper extends UnicastRemoteObject implements IWrapper{
	
	private static final long serialVersionUID = 1L;
	
	private HashingMap hmap;
	
	public Wrapper() throws RemoteException
	{
		super();
		try {
			this.hmap = xmlToObject();
			System.out.println("start up");
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public String getFileNode(String name)
	{
		Hashing hash = new Hashing(name);
		System.out.println(hash.getHash());
		
		return this.hmap.getNode(hash);
	}
	
	public int removeNode(String name)
	{
		int success = this.hmap.removeRecord(new Hashing(name));
		try {
			objectToXml();
			
			return success;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return 0;
		}
	}
	
	private void objectToXml() throws JAXBException {
		HashingMap object = this.hmap;
		JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
		// StringWriter writerTo = new StringWriter();
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream("D:/school/hashMap.xml");
			Marshaller marshaller = jaxbContext.createMarshaller();
			//StringWriter wr = new StringWriter();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(object, fileOut);
			//System.out.println(wr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getIp(String name)
	{
		String ip = this.hmap.getIp(new Hashing(name));
		
		return ip;
	}
	
	public int createNode(String name)
	{
		try {
			int success = this.hmap.addRecord(new Hashing(name), getClientHost());
			objectToXml();
			
			return success;
		} catch (ServerNotActiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return 0;
		} catch (JAXBException e) {
			e.printStackTrace();
			
			return 0;
		}
	}

	private HashingMap xmlToObject() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(HashingMap.class);
		Unmarshaller u = jaxbContext.createUnmarshaller();
		this.hmap = new HashingMap();
		
		return (HashingMap) u.unmarshal(new File("D:/school/hashMap.xml"));

	}

}