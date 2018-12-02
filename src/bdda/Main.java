package bdda;

import java.util.Scanner;

import exception.ReqException;
import exception.SGBDException;

public class Main {
	public static void main(String[] args) {
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
				sc.close();
				db.afficher();
			} catch (ReqException e) {
				System.out.println(e.getMessage());
			} catch (SGBDException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} while (!commande.equals("exit"));
	}

}
