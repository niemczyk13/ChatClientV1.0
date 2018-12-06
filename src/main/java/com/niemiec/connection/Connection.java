package com.niemiec.connection;

import java.io.IOException;
import java.net.Socket;

import com.niemiec.controllers.GetNickController;
import com.niemiec.objects.Client;

public class Connection extends Thread {
	private GetNickController getNickController;
	private Client client;
	private Socket socket;
	private boolean isConnected;
	private InputOutputStream inputOutputStream;
	private boolean closeTheConnection;
	
	public Connection(GetNickController getNickController, String host, int port) {
		this.getNickController = getNickController;
		this.client = null;
		this.isConnected = false;
		makeTheConnection(host, port);
		this.closeTheConnection = false;
		this.inputOutputStream = new InputOutputStream(socket);
	}

	@Override
	public void run() {
		Object object = null;
		while (!closeTheConnection) {
			object = inputOutputStream.receiveTheObject();
			receiveTheObject(object);
			
		}
		inputOutputStream.closeInputOutputStream();
		interrupt();
	}

	private void receiveTheObject(Object object) {
		if (client != null) {
			client.receiveTheObject(object);
		} else {
			getNickController.receiveTheObject(object);
		}
	}

	public void makeTheConnection(String host, int port) {
		socket = null;
		try {
			socket = new Socket(host, port);
			isConnected = true;
		} catch (Exception e) {
			System.out.println("Błąd tworzenia połączenia: " + e);
		}
	}
	
	public void interrupt() {
		super.interrupt();
		try {
			socket.close();
			isConnected = false;
		} catch (IOException e) {
		}
	}

	public boolean isConnected() {
		return isConnected;
	}
	
	public void sendTheObject(Object object) {
		inputOutputStream.sendTheObject(object);
	}
	
	public void closeTheConnection() {
		closeTheConnection = true;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
}
