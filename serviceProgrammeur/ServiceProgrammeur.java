package serviceProgrammeur;


import java.io.*;
import java.net.*;

import bri.ServeurBRi;
import bri.ServiceBRi;


public class ServiceProgrammeur implements Runnable {
	private Socket client;

	public ServiceProgrammeur(Socket socket) {
		client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			boolean logged = false;
			while (!logged) {
				out.println("Souahitez vous créer un compte (1) ou vous identifier (2) ?##>");
				int num;
				try {
					num = Integer.parseInt(in.readLine());
					switch (num) {
					case 1: {
						logged=true;
						new ServiceInscription(this.client).run();
						break;
					}
					case 2: {
						logged=true;
						new ServiceIdentification(this.client).run();
						break;
					}
					default: {}
					}
				} catch (NumberFormatException e) {
				}
			}
			new ServiceBRi(this.client).run();

		}
		catch (IOException e) {
			//Fin du service
		} 
		try {client.close();} catch (IOException e2) {}
	}

	protected void finalize() throws Throwable {
		client.close(); 
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();		
	}

}
