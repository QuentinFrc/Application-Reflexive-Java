package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.ArrayList;



public class ServiceRegistry {
	// cette classe est un registre de services
	// partagée en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	private static ArrayList<Class<?>> désactivés;
	private static ArrayList<Class<?>> services;

	static {
		services = new ArrayList<Class<?>>();
		désactivés = new ArrayList<Class<?>>();
	}
	
	private ServiceRegistry() {}

	// ajoute une classe de service après contrôle de la norme BLTi pour les Amateurs
	public static void addService(Class<?> c) throws Exception  {
		// vérifier la conformité par introspection
		// si non conforme --> exception avec message clair
		// si conforme, ajout au vector
		boolean implement = false;
		for(Class<?> cl : c.getInterfaces()) {
			if(cl.equals(bri.Service.class)) {
				implement=true;
				break;
			}
		}
		if(!implement)
			throw new Exception("*La classe n'implémente pas \"amateur.Service\"*");
		if(Modifier.isAbstract(c.getModifiers()))
			throw new Exception("*La classe ne doit pas être abstraite*");
		if(!Modifier.isPublic(c.getModifiers()))
			throw new Exception("*La classe doit être publique*");
		try {
			Constructor <?> cons = c.getConstructor(Socket.class);
			if(!(cons.getAnnotatedExceptionTypes().length==0)) {
				throw new Exception();
			}
		} catch (Exception e) {
			throw new Exception("*La classe doit avoir un constructeur sans exception qui prend un socket en paramètre*");
		} 
		Field[] fields = c.getDeclaredFields();
		boolean attr = false;
		for(Field f : fields)
			if(f.getType().equals(Socket.class))
				if(!(Modifier.isPrivate(f.getModifiers())&&Modifier.isFinal(f.getModifiers())))
					throw new Exception("*L'attribut Socket de la classe"+c.getSimpleName()+" n'est pas \"final\" et \"private\"*");
				else
				{
					attr=true;
					break;
				}
		if(!attr)
			throw new Exception("*La classe "+c.getSimpleName()+" ne possède pas d'atttribut Socket*");
		try {
			Method m = c.getMethod("toStringue");
			if(!Modifier.isPublic(m.getModifiers()))
				throw new Exception("*La méthode toStringue "+c.getSimpleName()+" n'est pas publique*");
			if(!Modifier.isStatic(m.getModifiers()))
				throw new Exception("*La méthode toStringue "+c.getSimpleName()+" n'est pas statique*");
			if(!(m.getAnnotatedExceptionTypes().length==0))
				throw new Exception("*La méthode toStringue "+c.getSimpleName()+" ne doit pas lever d'exception*");
			if(!(m.getReturnType()==String.class))
				throw new Exception("*La méthode toStringue \"+c.getSimpleName()+\" doit retourner un String*");
		} catch(NoSuchMethodException e) {
			throw new Exception("*La classe "+c.getSimpleName()+" ne possede pas de méthode \"toStringue\"*");
		}
		services.add(c);
		System.out.println("*Le Service "+c.getSimpleName()+" a été ajouté*");
	}


	//retire un service de la liste des services disponibles 
	public static void deleteService(Class<?> c) {
		synchronized (ServiceRegistry.class) {
			services.remove(c);
			désactivés.remove(c);
		}
	}

	// renvoie la classe de service (numService -1)	
	public static Class<?> getService(int numService) {
		synchronized (ServiceRegistry.class) {
			return services.get(numService-1);
		}
	}

	// change le statut du service (numService -1)	
	public static void changeStatut(Class<?> serv) {
		synchronized (ServiceRegistry.class) {
			if(désactivés.contains(serv))
				désactivés.remove(serv);
			else
				désactivés.add(serv);
		}
	}

	public static String servicesActifs() {
		String activé = "Voici les services actifs dans l'appli:##";
		int index = 1;
		for(Class<?> cl : services) {
			if (!désactivés.contains(cl)) {
				activé += index + " : (actif) " + cl.toString() + "##";
			}
			index++;
		}
		return activé;
	}


	// liste les services présents
	public static String toStringue() {
		String activé = new String("");
		String désactivé = new String("");
		int index = 1;
		for(Class<?> cl : services) {
			if (!désactivés.contains(cl)) {
				activé += index + " : (actif)" + classStringue(cl) + "##";
			}
			else
			{
				désactivé += index + " : (non-actif) " + classStringue(cl) + "##";
			}
			index++;
		}
		return "Voici les services présents dans l'appli:##"+ activé + désactivé;
	}

	private static String classStringue(Class<?> cl) {
		String s = new String();
		try {
			Constructor<?> c = cl.getConstructor(Socket.class);
			Method m = cl.getMethod("toStringue");
			s = (String) m.invoke(c.newInstance(null, null));

		} catch (Exception e) {
			s+="Ce service \""+ cl.getSimpleName() +"\" ne prévoit pas d'affichage descriptif"+e;
		}
		return s;
	}	

	public static void maj(Class<?> cl) throws Exception {
		synchronized(ServiceRegistry.class) {
			if(!services.contains(cl)) 
				throw new Exception("Le service ne peut pas être mis à jour, il n'est pas implémenté dans le registre.");
			services.remove(cl);
			services.add(cl);
			if(désactivés.contains(cl)) {
				désactivés.remove(cl);
				désactivés.add(cl);
			}
		}
	}

	public static boolean isActif(Class<?> cl) {
		return (services.contains(cl)&&!désactivés.contains(cl));
	}


	public static String servicesNonActifs() {
		String désactivé = "Voici les services non-actifs dans l'appli:##";
		int index = 1;
		for(Class<?> cl : services) {
			if (désactivés.contains(cl)) {
				;
				désactivé += index + " : (actif) " + cl.toString() + "##";
			}
			index++;
		}
		return désactivé;
	}
}
