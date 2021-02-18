package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import serveur.BRiLaunch;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partagée en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	private static ArrayList<Class<?>> servicesDesactivé;
	private static ArrayList<Class<?>> servicesProg;
	private static ArrayList<Class<?>> servicesAma;
	private static HashMap<Integer, ArrayList<Class<?>>> servicesClass;
	
	static {
		servicesDesactivé = new ArrayList<Class<?>>();
		servicesProg = new ArrayList<Class<?>>();
		servicesAma = new ArrayList<Class<?>>();
		servicesClass = new HashMap<Integer, ArrayList<Class<?>>>();
		servicesClass.put(BRiLaunch.getPortAma(), servicesAma);
		servicesClass.put(BRiLaunch.getPortProg(), servicesProg);
	}

	// ajoute une classe de service après contrôle de la norme BLTi
	public static void addService(Class<?> c, int port) throws Exception  {
		// vérifier la conformité par introspection
		// si non conforme --> exception avec message clair
		// si conforme, ajout au vector
		boolean implement = false;
		for(Class<?> cl : c.getInterfaces()) {
			if(cl.equals(Service.class)) {
				implement=true;
				break;
			}
		}
		if(!implement)
			throw new Exception("*La classe n'implémente pas \"bri.Service\"*");
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
		servicesClass.get(port).add(c);
		System.out.println(servicesClass);
		System.out.println(servicesClass.get(4000));
		
		System.out.println("*Le Service"+c.getSimpleName()+" a bien été ajouté*");
	}


	// renvoie la classe de service (numService -1)	
	public static Class<?> getServiceClass(int port, int numService) {
		synchronized (servicesClass) {
			return servicesClass.get(port).get(numService-1);
		}
	}

	// désactive le service (numService -1)	
	public void desactiver(int numService) {
		synchronized (ServiceRegistry.class) {
			Class<?> s = servicesProg.get(numService - 1);
			servicesAma.remove(s);
			servicesDesactivé.add(s);
		}
	}
	
	// active le service (numService -1)	
		public void activer(int numService) {
			synchronized (ServiceRegistry.class) {
				Class<?> s = servicesDesactivé.get(numService - 1);
				servicesDesactivé.remove(s);
				servicesAma.add(s);
			}
		}
		
	// renvoie la liste des services desactivables
	public String getServiceNonActif() {
		String result = new String("");
		result = "Liste des Services non-actif:##";
		int index = 1;
		for(Class<?> c : servicesDesactivé) {
			result+=index+" "+c.toString()+"##";
			index++;
		}
		return result;
	}
	// renvoie la liste des services activables
	public String getServiceActif() {
		String result = new String("");
		result = "Liste des Services actif:##";
		int index = 1;
		for(Class<?> c : servicesAma){
			result+=index+" "+c.toString()+"##";
			index++;
		}
		return result;
	}

	// liste les services présents
	public static String toStringue(int port) {
		String result = "Menu:##";
		int index = 1;
		for(Class<?> c : servicesClass.get(port)) {
			result+=index+" "+c.toString()+"##";
			index++;
		}
		return result;
	}
}
