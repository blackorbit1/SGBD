package bdda;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import exception.ReqException;
import exception.SGBDException;

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
		FileManager.getInstance().init();
	}
	
	/**
 	 * fonction qui execute la commande
 	 * @param commande
 	 */
	public void processCommand(String commande) throws ReqException, SGBDException {
		StringTokenizer st = new StringTokenizer(commande);
		String action = st.nextToken();
		String nomRelation = "";
		
		switch(action) {
			case "create":
				int nombreColonnes = 0;
				ArrayList<String> typesDesColonnes = new ArrayList<>();
				
				nomRelation = st.nextToken();
				
				try { // On regarde si le nombre de colonnes indique est bien un nombre
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
			case "insert":
				nomRelation = st.nextToken();
				ArrayList<String> contenuDesColonnes = new ArrayList<>();
				for(int i = 0; st.hasMoreTokens(); i++) {
					contenuDesColonnes.add(st.nextToken());
				}
				// Aucune gestion d'erreur, comme demande dans la consigne
				break;
			case "fill":
				nomRelation = st.nextToken();
				String nom_fichier = st.nextToken();
				fill(nomRelation, nom_fichier);
				break;
			case "debug": /// /// commande de debug poour tester des fonctions du SGBD /// ///
				switch(st.nextToken()){
					case "createfile": /// /// commande qui cree une fichier vide dans le classpath /// ///
						String nomFichier = st.nextToken();
						File fichier = new File(Constantes.pathName + nomFichier);
						try {
							fichier.createNewFile();
						} catch (IOException e) {
							throw new SGBDException("impossible de creer un fichier dans "+ Constantes.pathName);
						}
						break;
					default:
						break;
				}
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

	public void fill(String nomRelation, String nomFichier) throws SGBDException {
		File fichier = new File(Constantes.pathName + nomFichier);
		if(!fichier.exists()){
			throw new SGBDException("le fichier que vous demandez n'existe pas");
		}
		try(
				FileInputStream is = new FileInputStream(fichier);
				DataInputStream dis = new DataInputStream(is)){
			System.out.println(dis.readLine());
			System.out.println(dis.readLine());
			System.out.println(dis.readLine());
			StringTokenizer stFile = new StringTokenizer(dis.readLine(), "\n");
			while(stFile.countTokens() > 0){
				StringTokenizer stRow = new StringTokenizer(stFile.nextToken(), ",");
				System.out.println(stRow.toString());
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw new SGBDException("impossible de lire le fichier fourni");
		}
	}

	/** la fonction pour inserer un tuple dans une relation
	 *
	 * @param nomRelation (nom de la relation)
	 * @param contenuDesColonnes (contenu du tuple)
	 */
	public void insertRecord(String nomRelation, ArrayList<String> contenuDesColonnes){
		Record record = new Record();
		record.setValues(contenuDesColonnes);
		FileManager.getInstance().insertRecordInRelation(nomRelation, record);

	}
	
	/** la fonction pour creer la relation
	 * 
	 * @param nomRelation (nom de la relation)
	 * @param nombreColonnes (le nombre de colonnes)
	 * @param typesDesColonnes (un tableau avec le types de chaque colonnes)
	 */
	public void createRelation (String nomRelation, int nombreColonnes, ArrayList<String>typesDesColonnes) throws ReqException {


		int recordSize = 0;
		for(int i = 0; i<typesDesColonnes.size(); i++){
			switch(typesDesColonnes.get(i)){
				case "int":
					recordSize += Constantes.recordSize_int;
					break;
				case "float":
					recordSize += Constantes.recordSize_float;
					break;
				default:
					if(typesDesColonnes.get(i).substring(0, 5).equals("string")){
						try{
							int nb_chiffres = 0; // compteur qui va compter le nombre de chiffres dans le nombre
							for(int j = 0; j<4; j++){
								try{
									Integer.parseInt(typesDesColonnes.get(i).substring(6+j));
									nb_chiffres++;
								}catch(Exception e){
									// rien a faire, ça veut juste dire qu'il n'y a pas plus de 3 chiffres dans x
								}
							}
							int x = Integer.parseInt(typesDesColonnes.get(i).substring(6, 6+nb_chiffres));
							if(x <= 1000 && x>0){
								recordSize += x * Constantes.recordSize_stringx;
							} else {
								throw new ReqException("La taille d'une colonne de type string est incorrecte (min: 1, max: 1000)");
							}
						} catch (Exception e){
							throw new ReqException("Une colonne de type string est mal declaree");
						}
					}
			}
		}
		if(Constantes.pageSize / recordSize == 0){
			throw new ReqException("La relation que vous tentez de creer prend trop de place par rapport a la taille max d'une page");
		}

		RelDef relation = new RelDef();
		relation.setNom(nomRelation);
		relation.setNbColonne(nombreColonnes);
		relation.setType(typesDesColonnes);
		relation.setRecordSize(recordSize);
		relation.setSlotCount(Constantes.pageSize / recordSize);
		relation.setFileIdx(dbDef.getCompteurRel());

		dbDef.addRelation(relation);

		FileManager.getInstance().createNewHeapFile(relation);
	}
}
