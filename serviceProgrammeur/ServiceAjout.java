package serviceProgrammeur;


import java.io.*;
import java.net.*;

import bri.Service;
import bri.ServiceRegistry;
import serveur.BRiLaunch;


public class ServiceAjout implements Service {
	private final Socket client;

	static {
		try {
			ServiceRegistry.addService(ServiceAjout.class, BRiLaunch.getPortProg());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ServiceAjout(Socket socket) {
		client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			out.println("yo");

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

	public static String toStringue() {
		// TODO Auto-generated method stub
		return "Service d'ajout de service via le serveur FTP du programmeur";
	}

}
