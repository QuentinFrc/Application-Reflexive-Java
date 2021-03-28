package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * Ce client se connecte à un serveur dont le protocole est 
 * menu-choix-question-réponse client-réponse service
 * il n'y a qu'un échange (pas de boucle)
 * la réponse est saisie au clavier en String
 * 
 * Le protocole d'échange est suffisant pour le tp4 avec ServiceInversion 
 * ainsi que tout service qui pose une question, traite la donnée du client et envoie sa réponse 
 * mais est bien sur susceptible de (nombreuses) améliorations
 */
public class AppAmateur {
		private final static int PORT_SERVICE = 7000;
		private final static String HOST = "localhost"; 
	
	public static void main(String[] args) {
		Socket s = null;		
		try {
			s = new Socket(HOST, PORT_SERVICE);

			BufferedReader sin = new BufferedReader (new InputStreamReader(s.getInputStream ( )));
			PrintWriter sout = new PrintWriter (s.getOutputStream ( ), true);
			BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));			
		
			System.out.println("Connecté au serveur " + s.getInetAddress() + ":"+ s.getPort());
			
			String line;
		while (true) {
			// menu et choix du service
			line = sin.readLine();
			System.out.println(line.replaceAll("##", "\n"));
			// saisie/envoie du choix
			sout.println(clavier.readLine());
		}
		
				
			
		}
		catch (IOException e) { System.err.println("Fin de la connexion"); }
		// Refermer dans tous les cas la socket
		try { if (s != null) s.close(); } 
		catch (IOException e2) { ; }		
	}
}
