package programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

import bdd.Programmeur;
import bri.ServiceRegistry;

public class ServiceMaJ implements Service{
	private final Socket client;
	private Programmeur current;

	static {
		try {
			MenuProgrammeur.addService(ServiceMaJ.class);
		} catch (Exception e) {
			System.out.println("le Service ServiceMaJ n'a pas pu être ajouté au registre.");
		}
	}

	public ServiceMaJ(Socket socket, Programmeur user) {
		client = socket;
		this.current = user;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			out.println("Bienvenue dans le Service de Mise à jour des Services "+ this.current.getUsername()+",##"
					+ "Saisissez le nom de la classe que vous souhaitez mettre à jour ou \"exit\" pour quitter ##Url : \""+this.current.getAdresseFTP()+"\" ?##>");
			String adresse = this.current.getAdresseFTP();
			URL adresseFTP = new URL(adresse);
			URL[] URLs = {adresseFTP};
			while (true) {
				try {
					String choix = in.readLine();
					if(choix.contentEquals("exit"))
						break;
					URLClassLoader classLoader = new URLClassLoader(URLs);
					Class<?> cl = classLoader.loadClass(choix).asSubclass(Service.class);

					ServiceRegistry.maj(cl);
					out.println("Le service à été mis à jour avec succès. Merci !");
					System.out.println(this.current.getUsername() +" a mis à jour le service : "+ cl.getSimpleName());
					classLoader.close();
					break;
				} catch (Exception e) {
					out.println("Erreur: Le service n'a pas pu étre ajouté.##" + e);
				}
			}
			out.println("Merci de mettre à jour les services disponibles de notre application !##Pressez une touche pour sortir...");
			//on vide l'input
			in.readLine();
		}
		catch (IOException e) {
			//Fin du service
		} 
	}

	protected void finalize() throws Throwable {
		client.close(); 
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();		
	}

	public String toStringue() {
		// TODO Auto-generated method stub
		return "Service de mise à jour de service via le serveur FTP du programmeur";
	}

}
