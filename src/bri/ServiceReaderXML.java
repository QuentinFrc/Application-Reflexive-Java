package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class ServiceReaderXML implements Service {
	private final Socket client;
	private static String email;
	private static String password;

	static {
		try {
			email = "AppReflex@hotmail.com";
			password= "BretteCorp";
			ServiceRegistry.addService(ServiceReaderXML.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Le Service ServiceInversion n'a pas pu être ajouté au registre.\n"+e);
		}
	}

	public ServiceReaderXML(Socket sock) {
		this.client = sock;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			out.println("Bienvenue dans le Service d'analyse de fichier XML, ##Veuillez entrer l'adresse FTP ou se situe le fichier##>");
			String adresseFTP = in.readLine();
			out.println("Veuillez maintenant entrer le nom du fichier XML à analyser##>");
			String fileName = in.readLine();
			out.println("Veuillez saisir votre adresse mail pour recevoir l'analyse de votre fichier##>");
			String mailDest = in.readLine();
			URL url = new URL(adresseFTP+"/"+fileName);
			InputStream read = url.openStream();
			String rendu = analyseFile(fileName, read);
			try {
				sendMail(fileName, mailDest, rendu);
				out.println("L'analyse du fichier à été envoyé par mail à l'adresse: "+mailDest+"##Bonne journée !##appuyer sur une touche...");
			} catch (Exception e) {
				out.println("Désolé, le mail n'a pas pu être envoyé correctement##"+e+"##appuyer sur une touche...");
			}
			read.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private void sendMail(String fichier, String destinataire, String rendu) throws Exception {
		String subject = "Analyse du fichier XML "+fichier+".";
		Email mail = new Email(email, password, destinataire, subject, rendu);
		mail.sendEmail();
	}

	private String analyseFile(String name, InputStream read) {
		XMLHandler handler = new XMLHandler(name);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(read, handler);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			return "Erreur lors du traitement du fichier\n"+e;
		}
		return handler.getResult();
	}
	
	public static String toStringue() {
		return "Service de d'analyse de fichier XML";
	}
}