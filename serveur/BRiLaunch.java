package serveur;

import bdd.ProgrammeurBDD;
import bri.*;

public class BRiLaunch {
	private static int PORT_AMA = 3000;
	private static int PORT_PROG = 4000;
	
	public final static int getPortAma() {return PORT_AMA;}
	public final static int getPortProg() {return PORT_PROG;}

	public static void main(String[] args) {
			System.out.println("Lancement du Serveur...");
			//new Thread(new ServeurBRi(PORT_AMA)).start();
			try {
				Class.forName("service.ProgrammeurBDD");
				Class.forName("service.ServiceAjout");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new Thread(new ServeurBRi(PORT_PROG)).start();
	}
}