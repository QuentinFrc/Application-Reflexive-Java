package bri;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler{
	private HashMap<String, Integer> content;
	private String balise;
	private int nbOuvrante;
	private int nbfermé;
	private String fileName;

	public XMLHandler(String name) {
		super();
		this.fileName = name;
	}

	/*cette méthode est invoquée à chaque fois que parser rencontre
      une balise ouvrante '<' */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		this.balise = qName;
		this.nbOuvrante++;
	}

	/*cette méthode est invoquée à chaque fois que parser rencontre une balise fermante '>' */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		this.balise = null;
		this.nbfermé++;
	}
	/*imprime les données stockées entre '<' et '>' */
	public void characters(char ch[], int start, int length) throws SAXException { 
		// On compte le nombre d'objet de ce type (un int qu'on associe a une valeur)
		if(this.content.get(balise)==null)
			this.content.put(balise, 1); 
		else {
			int time = this.content.get(balise);
			time++;
			this.content.replace(balise, time);
		}
	}
	public String getResult() {
		String s = new String("...Début de l'analyse du fichier");
		int n =  this.content.size();
		if(this.nbfermé==this.nbOuvrante) {
			s+="Le fichier "+this.fileName+" est valide.\n"
					+ "Le fichier comporte autant de balises ouvrantes que fermantes.\n"
					+ "Le fichier comporte "+n+" balise(s) différente(s).\n";
		}
		s+="\n";
		for(Map.Entry<String, Integer> entry : this.content.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			s+=" - La balise "+key+" est utilisé "+value+" fois dans le fichier.\n";
		}
		s+="Fin de l'analyse du fichier...";
		return s;
	}
}
