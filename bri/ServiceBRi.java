package bri;


import java.io.*;
import java.lang.reflect.Constructor;
import java.net.*;


public class ServiceBRi implements Runnable {
	
	private Socket client;
	
	public ServiceBRi(Socket socket) {
		client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			out.println(ServiceRegistry.toStringue(client.getPort())+"##Tapez le numéro de service désiré :");
			int choix = Integer.parseInt(in.readLine());
			Class<?> c = ServiceRegistry.getServiceClass(this.client.getPort(),choix);
			Constructor<?> cons = c.getConstructor(Socket.class);
			Service s = (Service) cons.newInstance(this.client);
			s.run();
			// instancier le service numéro "choix" en lui passant la socket "client"
			// invoquer run() pour cette instance ou la lancer dans un thread à part 	
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

	// lancement du service
	public void start() {
		(new Thread(this)).start();		
	}

}
