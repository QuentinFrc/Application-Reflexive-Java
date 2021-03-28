package programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bdd.Programmeur;
import bdd.ProgrammeurBDD;

public class ServiceChangAdresse implements Service{
	private final Socket client;
	private Programmeur current;

	static {
		try {
			MenuProgrammeur.addService(ServiceChangAdresse.class);
		} catch (Exception e) {
			System.out.println("le Service ServiceChangAdresse n'a pas pu être ajouté au registre.");
		}
	}

	public ServiceChangAdresse(Socket socket, Programmeur user) {
		client = socket;
		this.current = user;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			String precision = "- Précision: vous devez bien entendu inclure votre package de développement \"/"+this.current.getUsername()+"\" -##>";
			out.println("Bienvenue dans le Service de changement d'adresse "+ this.current.getUsername()+",##"
					+ "Veuillez saisir votre adresse FTP actuelle pour confirmer votre identité"
					+ precision);
			String adresse;
			String inputAdresse;
			while (true) {
				inputAdresse = in.readLine();
				adresse = this.current.getAdresseFTP();
				if(adresse.contentEquals(inputAdresse)) {
					break;
				}
				out.println("Les adresses ne correspondent pas, Veuillez ré-essayer##>");
			}
			out.println("Parfait, saisissez votre nouvelle adresse FTP##"
					+ precision);
			while (true) {
				while (true) {
					adresse = in.readLine();
					out.println("confirmez votre nouvelle adresse##>");
					String adresse2 = in.readLine();
					if (adresse.contentEquals(adresse2))
						break;
					else
						out.println("Les adresses ne correspondent pas. Veuillez ré-essayer##"
								+ precision);
				}
				try{
					ProgrammeurBDD.changeAdresse(this.current, adresse);
					System.out.println(this.current.getUsername() +" a changé d'adresse ftp : "+this.current.getAdresseFTP());
					break;
				} catch(Exception e) {
					out.println(e+"##Veuillez ré-essayer avec une adresse FTP valide##>");
				}
			}
			out.println("Votre nouvelle adresse FTP :\""+this.current.getAdresseFTP()+"\" est configuré##Pressez une touche pour sortir...");
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


	public String toStringue() {
		// TODO Auto-generated method stub
		return "Service de changement d'adresse FTP du programmeur";
	}

}

