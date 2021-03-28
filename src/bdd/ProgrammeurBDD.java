package bdd;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProgrammeurBDD {
	private static ArrayList<Programmeur> programmeurs;
	private final static String fileDir ="src/log/";
	private final static String fileName ="log";
	private final static String fileExt =".JSON";
	private static int version;

	private ProgrammeurBDD(){}

	static {
		version = 0;
		programmeurs = new ArrayList<Programmeur>();
		init();
		System.out.println("La base de données à été initialisé");
	}

	public static int currentVersion() {
		return version;
	}
	public static ArrayList<Programmeur> getProgrammeurs() {
		return programmeurs;
	}
	public static void addProgrammeur(Programmeur prog) throws Exception {
		synchronized (programmeurs) {
			programmeurs.add(prog);
			miseAJour();
		}
	}

	//on modifie l'adresse d'un programme on stock donc une nouvelle version de la base dans les fichiers
	public static void changeAdresse(Programmeur programmeur, String adresse) throws Exception {
		int length = programmeur.getUsername().length();
		String paquetage = adresse.substring(adresse.length()-length, adresse.length());
		if(paquetage.contentEquals(programmeur.getUsername())) {
			programmeur.setAdresseFTP(adresse);
			miseAJour();
		}
		else
			throw new Exception("L'adresse FTP est mal défini, elle doit pointer vers votre paquetage de développement");
	}
	public static boolean alReadyExist(String username) {
		synchronized (programmeurs) {
			for (Programmeur p : programmeurs) {
				if (p.getUsername().contentEquals(username))
					return true;
			}
			return false;
		}
	}
	public static Programmeur isSignIn(String username, String password) throws Exception {
		for(Programmeur pr : programmeurs) {
			if(pr.match(username, password)) {
				return pr;
			}
		}
		throw new Exception("Aucune correspondance... impossible de trouver "+username);
	}

	//A chaque fois qu'on opère sur la liste, on met a jour le JSON, ArrayList --> JSON
	private static void miseAJour() {
		System.out.println("Mise à jour de la base...");
		synchronized (programmeurs) {
			version++;
			final GsonBuilder builder = new GsonBuilder();
			final Gson gson = builder.create();
			String json = "\n";
			for(int i=0;  i<=programmeurs.size()-1;i++) {
				json += gson.toJson(programmeurs.get(i));
				if(i!=programmeurs.size()-1)
					json +=",";
				json+="\n";
			}
			try {
				String path = fileDir+fileName+currentVersion()+fileExt;
				FileWriter fw = new FileWriter(path, false);
				fw.write("[" + json + "]");
				fw.close();
				System.out.println("...Base à jour");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Echec du stockage interne de la base de données");
			}
		}
	}
	//Au lancement du programme JSON --> ArrayList
	private static void init() {
		try {
			JSONParser parser = new JSONParser();
			String path = fileDir+fileName+currentVersion()+fileExt;
			JSONArray progs = (JSONArray) parser.parse(new FileReader(path));
			for (Object o : progs) {
				JSONObject prog = (JSONObject) o;
				String userName = (String) prog.get("username");
				String password = (String) prog.get("password");
				String adresse = (String) prog.get("adresseFTP");
				Programmeur programmeur = new Programmeur(userName, password, adresse);
				programmeurs.add(programmeur);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("/!\\ La base de données n'à pas été chargé correctement /!\\");
		}
	}
	public static boolean passwordValid(String password) throws Exception {
		if(password.length()<=1)
			throw new Exception("Le mot de passe doit avoir une taille minimum de 8 caractères.");
		return true;
	}
	
	public static boolean adresseFTPValid(String adresse) throws Exception {
		if(!adresse.startsWith("ftp://"))
			throw new Exception("L'adresse doit suivre le protocole FTP est être de la forme \"ftp://...\"");
		return true;
	}
}
