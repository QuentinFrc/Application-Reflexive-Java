package serveur;

public abstract class  ServiceServ implements Runnable {
	public final void start() {
		new Thread(this).start();
	}
}
