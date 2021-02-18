package bdd;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProgrammeurBDD {
	private static ArrayList<Programmeur> programmeurs;
	private final static String fileName ="src/log/log.json";

	static {
		programmeurs = new ArrayList<Programmeur>();
		init();
		System.out.println("La base de données à été initialisé");
	}

	public static String getFileName() {
		return fileName;
	}
	public static ArrayList<Programmeur> getProgrammeurs() {
		return programmeurs;
	}
	public static void addProgrammeur(Programmeur prog) throws Exception {
		if(programmeurs.contains(prog))
			return;
		else {
			synchronized (programmeurs) {
				programmeurs.add(prog);
				final GsonBuilder builder = new GsonBuilder();
				final Gson gson = builder.create();
				final String json = gson.toJson(prog);
				//------ supprimé le "]" de fin de fichier et le remettre ensuite
				RandomAccessFile f = new RandomAccessFile(fileName, "rw");
				long length = f.length() - 1;
				byte b = 0;
				do {
					length -= 1;
					f.seek(length);
					b = f.readByte();
				} while (b != 10);
				f.setLength(length + 1);
				f.close();
				//------
				FileWriter fw = new FileWriter(fileName, true);
				fw.write(",\n" + json + "\n]");
				fw.close();
			}
		}
	}

	public static boolean alReadyExist(String username) {
		synchronized (programmeurs) {
			//on ne met a jour la base interne avec le fichier que lorsqu'on utilise la BDD
			miseAJour();
			for (Programmeur p : programmeurs) {
				if (p.getUsername() == username)
					return true;
			}
			return false;
		}
	}
	public static boolean isSignIn(String username, String password) {
		miseAJour();
		for(Programmeur p : programmeurs) {
			if(p.match(username, password))
				System.out.println(p.getUsername()+"//"+username+ " "+p.getPassword()+"//"+password);
				return true;
		}
		return false;
	}
	
	private static void miseAJour() {
		synchronized (programmeurs) {
			System.out.println("La base de données à été mise à jour");
			programmeurs.clear();
			init();
		}
	}
	private static void init() {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		JSONArray progs;
		try {
			progs = (JSONArray) parser.parse(new FileReader(fileName));
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
			System.out.println("La base de données n'à pas été chargé correctement");
		}
	}

}
