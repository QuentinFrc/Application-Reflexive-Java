package serveur;


public class BRiLaunch {
	private static int PORT_AMA = 7000;
	private static int PORT_PROG = 4000;

	public final static int getPortAma() {return PORT_AMA;}
	public final static int getPortProg() {return PORT_PROG;}

	public static void main(String[] args) {
		System.out.println("Lancement du Serveur..."); 
		BRiLaunch.initService();
		BRiLaunch.initServeur();
	}

	private static void initServeur() {
		new Thread(new ServeurService(PORT_AMA, service.ServiceBRi.class)).start();
		new Thread(new ServeurService(PORT_PROG, service.ServiceProg.class)).start();
	}
	
	public static void initService() {
		try {
			Class.forName("bri.ServiceRegistry");
		}catch (ClassNotFoundException e) {
			System.out.println(e);
		}
		try {
			Class.forName("programmeur.MenuProgrammeur");
		}catch (ClassNotFoundException e) {
			System.out.println(e);
		}

		try {
			Class.forName("bdd.ProgrammeurBDD");
		}catch (ClassNotFoundException e) {
			System.out.println(e);
		}	
		try {
			Class.forName("programmeur.ServiceAjout"); //1
		}catch (ClassNotFoundException e) {
			System.out.println(e);
		}
		try {
			Class.forName("programmeur.ServiceMaJ"); //2
		}catch (ClassNotFoundException e) {
			System.out.println(e);
		}
		try {
			Class.forName("programmeur.ServiceChangAdresse"); //3
		}catch (ClassNotFoundException e) {
			System.out.println(e);
		}
		try {
			Class.forName("programmeur.ServiceEnableDisable"); //4
		}catch (ClassNotFoundException e) {
			System.out.println(e);
		}
		try {
			Class.forName("programmeur.ServiceUninstall"); //5
		} catch (ClassNotFoundException e) {
			System.out.println("");
		}	/*
		try {
			Class.forName("bri.ServiceInversion");
		}
		catch(ClassNotFoundException e) {
			System.out.println(e);
		}*/
		/*try {
			Class.forName("bri.ServiceReaderXML"); 
		}
		catch(ClassNotFoundException e) {
			System.out.println(e);
		}*/
	}
}