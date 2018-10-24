package bdda;

import java.util.Scanner;

public class Main {
	public static void main(String [] args) {
		DBManager db = DBManager.getInstance();
		db.init();
		String commande = "";
		do {
			
			System.out.println("Veuillez entrer votre commande");
			Scanner sc = new Scanner(System.in);
			commande = sc.nextLine();
			//sc.close();
			
			switch (commande) {
				case "exit": db.finish(); break;
				default: db.processCommand(commande); break;
			}
			db.afficher();
		} while(!commande.equals("exit"));
	}
	
}
