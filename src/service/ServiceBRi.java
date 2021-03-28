package service;


import java.io.*;
import java.lang.reflect.Constructor;
import java.net.*;

import bri.ServiceRegistry;
import bri.Service;
import serveur.ServiceServ;


public class ServiceBRi extends ServiceServ{

	private Socket client;

	public ServiceBRi(Socket socket) {
		client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter (client.getOutputStream(), true);
			out.println(ServiceRegistry.servicesActifs() + "##Tapez le numéro de service désiré##>");
			
				/*String tmp = in.readLine();
				String choix = tmp==null ? "exit" : tmp ;
				if(choix.contentEquals("exit")) {
					out.println("Sortie de l'application...");
					break;
				}*/
				try {
					String choix = in.readLine();
					int numService = Integer.parseInt(choix);
					Class<?> c = ServiceRegistry.getService(numService);
					Constructor<?> cons = c.getConstructor(Socket.class);
					Service ser = (Service) cons.newInstance(this.client);
					System.out.println(ser);
					ser.run();
				} catch ( NumberFormatException | IndexOutOfBoundsException e) {
					System.out.println(e);
					out.println("Numéro invalide, veuillez ré-essayer##>");
				}
		}
		catch (IOException e) {
			//Fin du service
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		try {client.close();} catch (IOException e2) {}
	}

	protected void finalize() throws Throwable {
		client.close(); 
	}
}
