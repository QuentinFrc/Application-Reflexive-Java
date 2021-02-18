package serviceProgrammeur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bdd.ProgrammeurBDD;

public class ServiceIdentification implements Runnable {
	private Socket client;

	public ServiceIdentification(Socket socket) {
		this.client = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(this.client.getOutputStream(), true);
			boolean logged = false;

			out.println("Saisissez votre identifiant: ##>");
			while (!logged) {
				String username = new String();
				username = in.readLine();
				out.println("Saisissez votre mot de passe: ##>");
				String password = new String();
				password = in.readLine();
				if (ProgrammeurBDD.isSignIn(username, password)) {
					out.println("succès de l'authentification");
					logged = true;
				}
				else {
					out.println("erreur lors de l'authentification, veuillez saisir votre identifiant à nouveau##>");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}