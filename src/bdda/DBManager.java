package bdda;

import java.util.ArrayList;
import java.util.StringTokenizer;

import exception.ReqException;

public class DBManager {
	private DBDef dbDef ;
	
	private static final DBManager db = new DBManager();
	private DBManager() {
		
	}
	
	public static DBManager getInstance() {
		return db;
	}
	
	public void init() {
		dbDef = DBDef.getInstance();
	}
	
	/**
 	 * fonction qui execute la commande
 	 * @param commande
 	 */
	public void processCommand(String commande) throws ReqException {
		StringTokenizer st = new StringTokenizer(commande);
		String action = st.nextToken();
		
		switch(action) {
			case "create":
				String nomRelation = "";
				int nombreColonnes = 0;
				ArrayList<String> typesDesColonnes = new ArrayList<>();
				
				nomRelation = st.nextToken();
				
				try { // On regarde si le nombre de colonnes indiqué est bien un nombre
					nombreColonnes = Integer.parseInt(st.nextToken());
				} catch(Exception e) { // si non, on lance une exception
					throw new ReqException("Le nombre de colonnes n'est pas un entier");
				}
				
				
				if(nombreColonnes != st.countTokens()) {
					throw new ReqException("Le nombre de colonne ne correspond pas au nombre de types de colonnes");
				}
				
				
				for(int i = 0; i<nombreColonnes && st.hasMoreTokens(); i++) {
					typesDesColonnes.add(st.nextToken());
				}
				
				System.out.println(nomRelation);
				System.out.println(nombreColonnes);
				System.out.println(typesDesColonnes);
				createRelation(nomRelation, nombreColonnes, typesDesColonnes);
				break;
			default:
				throw new ReqException("Commande inconnue");
		}
	}
	
	/** 
	 * Fonction de debug
	 */
	public void afficher() {
		/*
		System.out.println(dbDef.getCompteurRel());
		System.out.println(dbDef.getListeDeRelDef());
		System.out.println(dbDef.getInstance());
		*/
		
	}
	
	public void finish() {
		
	}
	
	/** la fonction pour creer la relation
	 * 
	 * @param nomRelation (nom de la relation)
	 * @param nombreColonnes (le nombre de colonnes)
	 * @param typesDesColonnes (un tableau avec le types de chaque colonnes)
	 */
	public void createRelation (String nomRelation, int nombreColonnes, ArrayList<String >typesDesColonnes) {
		RelDef relation = new RelDef();
		relation.setNom(nomRelation);
		relation.setNbColonne(nombreColonnes);
		relation.setType(typesDesColonnes);
		dbDef.addRelation(relation);
	}
}
