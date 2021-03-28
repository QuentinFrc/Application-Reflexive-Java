package programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bdd.Programmeur;
import bri.ServiceRegistry;

public class ServiceUninstall implements Service{
	private final Socket client;
	private final Programmeur current;

	static {
		try {
			MenuProgrammeur.addService(ServiceUninstall.class);
		} catch (Exception e) {
			System.out.println("le Service ServiceUninstall n'a pas pu être ajouté au registre.");
		}
	}

	public ServiceUninstall(Socket client, Programmeur prog) {
		this.client=client;
		this.current = prog;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			String msg = "Bienvenue dans le service de désinstallation,##";
			while(true) {
				out.println(msg + "Saisissez le numéro de service à supprimer ou \"exit\" pour quitter##"
						+ ServiceRegistry.servicesNonActifs()
						+ "NB : Pour des raisons de sécurité, seuls les services désactivés peuvent être supprimé##>");
				String saisie = in.readLine();
				String choix = (saisie==null) ? "exit": saisie;
				if(choix.contentEquals("exit")) {
					break;	
				}
				else
				{
					try {
						Class<?> serv = ServiceRegistry.getService(Integer.parseInt(choix));
						ServiceRegistry.deleteService(serv);
						System.out.println(this.current.getUsername() +" a supprimé le service : "+serv.getSimpleName());
					} catch (NumberFormatException e) {
						msg = "Numéro de service invalide. ##";
					}
				}
			}
			out.println("Merci d'entrenir les services de notre application !##Pressez une touche pour sortir...");
			//on vide l'input
			in.readLine();
		} catch(IOException e) {/* Fin du service */ }

	}
	public static String toStringue() {
		return "Service de désinstallation de service de l'application";
	}

}
