package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceInversion implements Service{
	private final Socket client;
	
	
	static {
		try {
			ServiceRegistry.addService(ServiceInversion.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Le Service ServiceInversion n'a pas pu être ajouté au registre.");
			System.out.println(e);
		}
	}

	public ServiceInversion(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			System.out.println("on est dedans ");
			out.println("Bienvenue dans le Service d'inversion, ##Veuillez entrer du texte à inverser##>");
			String inverse = in.readLine();
			String rendu = reverse(inverse);
			out.println("Voici votre texte inversé :##" + rendu);
			
		} catch (IOException e) { /*Fin du service*/}
	}

	private String reverse(String saisie) {
		String rendu = new String();
		char[] temp = saisie.toCharArray();
		for(int i = temp.length-1; i>=0; i--)
			rendu += temp[i];
		return rendu;
	}
	
	public static String toStringue() {
		return "Service d'inversion de texte saisie par l'utilisateur";
	}

}
