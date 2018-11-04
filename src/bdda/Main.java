package bdda;

import java.util.Scanner;

import exception.ReqException;

public class Main {
	public static void main(String [] args) {
		DBManager db = DBManager.getInstance();
		db.init();
		String commande = "";
		do {
			
			try {
				System.out.println("Veuillez entrer votre commande");
				Scanner sc = new Scanner(System.in);
				commande = sc.nextLine();
				//sc.close();
				
				switch (commande) {
					case "exit": db.finish(); break;
					default: db.processCommand(commande); break;
				}
				db.afficher();
			} catch(ReqException e) {
				System.out.println(e.getMessage());
			} catch(Exception e) {
				System.out.println("bonjour");
			}
			
		} while(!commande.equals("exit"));
	}
	
}
