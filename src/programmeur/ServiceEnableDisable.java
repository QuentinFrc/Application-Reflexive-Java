package programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bdd.Programmeur;
import bri.ServiceRegistry;

public class ServiceEnableDisable implements Service{
	private Socket client;
	private Programmeur current;

	static {
		try {
			MenuProgrammeur.addService(ServiceEnableDisable.class);
		} catch (Exception e) {
			System.out.println("le Service ServiceEnableDisable n'a pas pu être ajouté au registre.");
		}
	}

	public ServiceEnableDisable(Socket s, Programmeur p) {
		this.client = s;
		this.current = p;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			String msg = "Bienvenue dans le Service de maintenance des services "+ this.current.getUsername()+",##";
			String choiceSentence = "Saisissez un numéro du service pour changer son statut ou exit pour quitter##>";
			while (true) {
				out.println(msg +ServiceRegistry.toStringue()+ choiceSentence);
				try {
					String choix = in.readLine();
					if(choix.contentEquals("exit"))
						break;
					else {
						Class<?> serv = ServiceRegistry.getService(Integer.parseInt(choix));
						ServiceRegistry.changeStatut(serv);
						System.out.println(this.current.getUsername() +" a supprimé le service : "+serv.getSimpleName());
					}
				}
				catch(Exception e) {
					msg = "Erreur, numéro de service invalide##";
				}
			}
			out.println("Merci de la maintenance que vous apportez aux services. ##Pressez une touche pour sortir...");
			//on vide l'input
			in.readLine();
		}
		catch (IOException e) {
			//Fin du service
		} 
	}

	public String toStringue() {
		return "Service d'activation et désactivation de service de l'application";
	}

}
