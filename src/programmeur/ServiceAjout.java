package programmeur;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import bdd.Programmeur;
import bri.ServiceRegistry;

//import bri.Service;

public class ServiceAjout implements Service{
	private final Socket client;
	private Programmeur current;
	private static String fileDest= "tmp/";

	static {
		File dir = new File(fileDest);
		dir.mkdirs();
		fileDest = dir.getAbsolutePath()+"\\";
		try {
			MenuProgrammeur.addService(ServiceAjout.class);
		} catch (Exception e) {
			System.out.println("Le Service ServiceAjout n'a pas pu être ajouté au registre.");
		}
	}

	public ServiceAjout(Socket socket, Programmeur user) {
		this.client = socket;
		this.current = user;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			out.println("Bienvenue dans le Service d'ajout "+ this.current.getUsername()+",##"
					+ "Saisissez le nom du service que vous souhaitez charger sinon \"exit\" pour quitter ##Url : \""+this.current.getAdresseFTP()+"\" ?##"
					+ "##(Pour l'import d'une bibliothèque, le nom de celle ci doit être le nom de la classe service principale)##>");
			String adresseFTP = this.current.getAdresseFTP();
			while (true) {
				try {
					String saisie = in.readLine();
					String main = (saisie==null) ? "exit": saisie;
					if(main.contentEquals("exit"))
						break;
					out.println("Saisissez le format du fichier à charger.##"
							+ "Les formats acceptés sont:  \".zip\" et \".class\"##>");
					saisie = in.readLine();
					String ext = (saisie==null) ? "null": saisie;
					if(ext.contentEquals(".class")) {
						URL[] urls = {new URL(adresseFTP)};
						URLClassLoader classLoader = new URLClassLoader(urls);
						Class<? extends bri.Service> s = classLoader.loadClass(main).asSubclass(bri.Service.class);
						ServiceRegistry.addService(s);
						classLoader.close();
						break;
					}
					else if(ext.contentEquals(".zip")) {
						URL[] urls = {new URL("file:"+fileDest)};
						URLClassLoader classLoader = new URLClassLoader(urls);
						ArrayList<String> classes = decompress(new URL(adresseFTP+main+ext));
						// à partir d'ici les fichiers ont été ajouté dans le fichier tmp de l'application
						for(String classe : classes) {
							Class <?> cl = classLoader.loadClass(classe.replaceAll("/","."));
							if(classe.contentEquals(main))
								ServiceRegistry.addService(cl);
						}
						classLoader.close();
						break;
					}
					else
					{
						out.println("Seuls les formats \".zip\" et \".class\" sont supportés, veuillez ré-essayer##"
								+ "Quel est le nom du service à ajouter ?##>");
					}
				} catch (Exception e) {
					e.printStackTrace();
					out.println("##Erreur: Le service n'a pas pu étre ajouté.##" + e+"##Veuillez re-saisir le nom de votre classe##>");
				}
			}
			out.println("Merci d'enrichir les services disponibles de notre application !##Pressez une touche pour sortir...");
			//on vide l'input
			in.readLine();
		}
		catch (IOException e) {System.out.println("BUG\n"+e);} 
	}

	protected void finalize() throws Throwable {
		client.close(); 
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();		
	}

	private ArrayList<String> decompress(URL urlFile) throws IOException {
		ArrayList<String> noms = new ArrayList<>();
		String url = urlFile.toString();
		String file = url.substring(url.lastIndexOf("/"), url.lastIndexOf("."));
		new File(fileDest+file).mkdirs();
		String extension = ".class";
		InputStream input = urlFile.openStream();
		ZipInputStream zipIn = new ZipInputStream(input);
		ZipEntry entry = zipIn.getNextEntry();
		while (entry != null) {
			String filePath = fileDest + entry.getName();
			if(entry.isDirectory()) {
				File dir = new File(filePath);
				dir.mkdirs();
			}
			else {
				if(entry.getName().endsWith(extension)) {
					noms.add(entry.getName().substring(0, entry.getName().length()-extension.length()));
					extractFile(zipIn, filePath);
				}
			}
			entry = zipIn.getNextEntry();

		}
		return noms;
	}

	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[4096];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}


	public String toStringue() {
		return "Service d'ajout de service via le serveur FTP du programmeur";
	}
}