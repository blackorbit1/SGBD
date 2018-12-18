package bdda;

import java.util.Scanner;

import exception.ReqException;
import exception.SGBDException;

public class Main {
	public static void main(String[] args) throws SGBDException {
		DBManager db = DBManager.getInstance();
		db.init();
		String commande = "";
		do {

			try {
				System.out.println("Veuillez entrer votre commande");
				Scanner sc = new Scanner(System.in);
				commande = sc.nextLine();

				switch (commande) {
				case "exit":
					db.finish();
					break;
				default:
					db.processCommand(commande);
					break;
				}
				db.afficher();
			} catch (ReqException e) {
				e.printStackTrace();
			} catch (SGBDException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} while (!commande.equals("exit"));
	}

}
