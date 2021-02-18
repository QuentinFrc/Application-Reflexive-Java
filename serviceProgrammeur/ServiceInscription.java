package serviceProgrammeur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bdd.Programmeur;
import bdd.ProgrammeurBDD;

public class ServiceInscription implements Runnable{
	private Socket client;

	public ServiceInscription(Socket socket) {
		this.client = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(this.client.getOutputStream(), true);
			boolean valide = false;
			String username = new String();
			String password = new String();
			String adresse = new String();
			out.println("Entrez un identifiant: ##>");
			while (!valide) {
				username = in.readLine();
				if(ProgrammeurBDD.alReadyExist(username))
					out.println("Cet identifiant est déjà utilisé, veuillez ressayer ##>");
				else
					valide = true;
			}
			valide = false;
			out.println("Entrez un mot de passe: ##>");
			password = in.readLine();
			out.println("Entrez l'adresse de votre serveur ftp: ##>");
			adresse = in.readLine();
			AjoutProgBDD(username, password, adresse);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void AjoutProgBDD(String username, String password, String adress) {
		try {
			final Programmeur prog = new Programmeur(username, password, adress);
			ProgrammeurBDD.addProgrammeur(prog);
		}catch (Exception e)
		{
			System.out.println("bug");
		}
		System.out.println("Inscription du programmeur "+username);
	}

}
