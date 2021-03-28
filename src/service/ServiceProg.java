package service;


import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

import bdd.Programmeur;
import bdd.ProgrammeurBDD;
import programmeur.MenuProgrammeur;
import programmeur.Service;
import serveur.ServiceServ;


public class ServiceProg extends ServiceServ{

	private Socket client;
	private Programmeur current;

	public ServiceProg(Socket socket) {
		client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			out.println("Salut à toi jeune programmeur ! Connecte toi ou inscris toi (c/i)##>");
			boolean repeat=true;
			while (repeat) {
				char choix = in.readLine().charAt(0);
				switch (choix) {
				case 'c': {
					String username = new String();
					String password = new String();
					out.println("Service de connexion : Saisissez votre identifiant##>");
					while (true) {
						username = in.readLine();
						out.println("Service de connexion : Saisissez votre mot de passe##>");
						password = in.readLine();
						try{
							this.current = ProgrammeurBDD.isSignIn(username, password);
							repeat =false;
							break;
						}
						catch(Exception e) {
							out.println("Erreur lors de l'authentification, veuillez saisir votre identifiant à nouveau##>");
						}
					}
					break;
				}
				case 'i': {
					String username = new String();
					String password = new String();
					String adresse = new String();
					out.println("Service d'inscription : Entrez un identifiant##>");
					while (true) {
						while (true) {
							username = in.readLine();
							if (ProgrammeurBDD.alReadyExist(username))
								out.println("Cet identifiant est déjà utilisé, veuillez ré-essayer##>");
							else
								break;
						}
						out.println("Service d'inscription : Entrez un mot de passe##>");
						while (true) {
							try {
								password = in.readLine();
								ProgrammeurBDD.passwordValid(password);
								System.out.println("Connexion du programmeur "+this.current.getUsername());
								break;
							}
							catch(Exception e) {
								out.println("Service d'inscription : Le mot de passe n'est pas valide##"+ e +"##Veuillez ré-essayer##>");
							}
						}
						out.println("Service d'inscription : Entrez l'adresse de votre serveur ftp##>");
						while (true) {
							try {
								adresse = in.readLine();
								ProgrammeurBDD.adresseFTPValid(adresse);
								break;
							} catch (Exception e) {
								out.println("Service d'inscription : L'adresse FTP n'est pas valide##"+ e +" Veuillez ré-essayer##>");
							}
						}
						try {
							this.current = new Programmeur(username, password, adresse);
							ProgrammeurBDD.addProgrammeur(current);
							System.out.println("Inscription du programmeur " + this.current.getUsername());	
							repeat = false;
							break;
						} catch (Exception e) {
							out.println("Impossible d'ajouter l'utilisateur à la BDD.##Nous vous invitons a ré-essayer, si le problème persiste contactez un le support.##"
									+ "Service d'inscription : Entrez un identifiant:##>");
						} 
					}
					break;
				}
				default: {out.println("Saisie 'c' pour te connecter ou 'i' pour t'inscrire !##>");}
				}
			}
			while (true) {
				out.println("Bienvenue " + this.current.getUsername() + ", "+MenuProgrammeur.toStringue() 
				+ "Tapez le numéro de service désiré ou exit pour quitter##>");
				String tmp = in.readLine();
				String choix = tmp==null ? "exit" : tmp ;
				if(choix.contentEquals("exit")) {
					out.println("Sortie de l'application...");
					break;
				}
				while (true) {
					try {
						int numService = Integer.parseInt(choix);
						Class<?> c = MenuProgrammeur.getService(numService);
						Constructor<?> cons = c.getConstructor(Socket.class, Programmeur.class);
						Service s = (Service) cons.newInstance(this.client, this.current);
						s.run();
					} catch ( NumberFormatException | IndexOutOfBoundsException e) {
						out.println("Numéro invalide, Appuyer sur une touche...##>");
					} catch ( InstantiationException | IllegalAccessException | IllegalArgumentException 
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						out.println("Ce service est momentanément indisponible, Appuyer sur une touche...##>");
					}
					break;
				}
			}
			out.println("Sortie de l'application...");
		}
		catch (IOException e) { /*Fin du service*/ } 
		
		try {client.close();} catch (IOException e2) {}
	}

	protected void finalize() throws Throwable {
		client.close(); 
	}
}
