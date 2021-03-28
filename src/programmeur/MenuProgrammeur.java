package programmeur;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;

import bdd.Programmeur;

public class MenuProgrammeur {
	// cette classe est un registre de services
	private static ArrayList<Class<?>> services;

	static {
		services = new ArrayList<Class<?>>();
	}

	private MenuProgrammeur() {}
	// ajoute une classe de service (utilisé au lancement du programme) pour les programmeurs
	public static void addService(Class<?> c) throws Exception  {
		try {
			c.getMethod("toString");
			services.add(c);
			System.out.println("Ajout du service : "+c.getSimpleName()+ " au RegistreProgrammeur.");
		} catch (NoSuchMethodException | SecurityException e) {
			System.out.println("");
		}
	}

	// liste les services présents
	public static String toStringue() {
		String result = "Voici la liste des services :##";
		int index = 1;
		for(Class<?  > c : services) {
			try {
				result+=index+" : "+ classStringue(c) +"##";
			} catch (Exception e) {
				System.out.println(e);
			}
			index++;
		}
		return result;
	}

	public static Class<?> getService(int numService){
		synchronized (MenuProgrammeur.class) {
			return services.get(numService-1);
		}

	}
	private static String classStringue(Class<?> cl) {
		String s = new String();
		try {
			Constructor<?> c = cl.getConstructor(Socket.class, Programmeur.class);
			Method m = cl.getMethod("toStringue");
			s = (String) m.invoke(c.newInstance(null,null));

		} catch (Exception e) {
			s+="Ce service \""+ cl.getSimpleName() +"\" ne prévoit pas d'affichage descriptif";
		}
		return s;
	}

}
