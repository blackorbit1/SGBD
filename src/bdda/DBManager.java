package bdda;

import java.util.ArrayList;
import java.util.StringTokenizer;

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
	public void processCommand(String commande) {
		StringTokenizer st = new StringTokenizer(commande);
		String action = st.nextToken();
		
		switch(action) {
			case "create":
				String nomRelation = "";
				int nombreColonnes = 0;
				ArrayList<String> typesDesColonnes = new ArrayList<>();
				
				nomRelation = st.nextToken();
				nombreColonnes = Integer.parseInt(st.nextToken());
				
				for(int i = 0; i<nombreColonnes && st.hasMoreTokens(); i++) {
					typesDesColonnes.add(st.nextToken());
				}
				
				System.out.println(nomRelation);
				System.out.println(nombreColonnes);
				System.out.println(typesDesColonnes);
				createRelation(nomRelation, nombreColonnes, typesDesColonnes);
				break;
		}
	}
	
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
	 * @param typesDesColonnes (un tableau avec le type de chaque colonne)
	 */
	public void createRelation (String nomRelation, int nombreColonnes, ArrayList<String >typesDesColonnes) {
		RelDef relation = new RelDef();
		relation.setNom(nomRelation);
		relation.setNbColonne(nombreColonnes);
		relation.setType(typesDesColonnes);
		dbDef.addRelation(relation);
	}
}
