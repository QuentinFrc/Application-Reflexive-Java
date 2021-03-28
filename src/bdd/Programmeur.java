package bdd;

public class Programmeur {
	private String username;
	private String password;
	private String adresseFTP;

	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getAdresseFTP() {
		return adresseFTP;
	}

	public Programmeur(String us, String pass, String adresseFTP) {
		this.username = us;
		this.password = pass;
		this.adresseFTP = adresseFTP;
	}

	public boolean match(String username, String password) {
		return this.username.contentEquals(username) && this.password.contentEquals(password);
	}

	public String toString() {
		return username+" "+password+" "+ adresseFTP;
	}
	public void setAdresseFTP(String adresse) {
		this.adresseFTP = adresse;
	}
}
