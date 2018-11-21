package bdda;

import exception.SGBDException;

import java.io.*;
import java.util.ArrayList;

public class DBDef {
	private ArrayList<RelDef> listeDeRelDef;
	private int compteurRel;
	
	private static DBDef dbdef = new DBDef();
	private DBDef() {
		listeDeRelDef = new ArrayList<RelDef>();
	}
	
	public static DBDef getInstance() {
		return dbdef;
	}
	
	/** Pour ajouter une relation
	 * 
	 * @param relation (la relation à ajouter)
	 */
	public void addRelation(RelDef relation) {
		listeDeRelDef.add(relation);
		compteurRel++;
	}

	public ArrayList<RelDef> getListeDeRelDef() {
		return listeDeRelDef;
	}

	
	public void setListeDeRelDef(ArrayList<RelDef> listeDeRelDef) {
		this.listeDeRelDef = listeDeRelDef;
	}

	public int getCompteurRel() {
		return compteurRel;
	}

	public void setCompteurRel(int compteurRel) {
		this.compteurRel = compteurRel;
	}

	/** Initialiser la classe lorsque le programme démarre à partir d'un fichier Catalog.def
	 * */
	public void init() throws SGBDException {
		File fichier = new File(Constantes.pathName + "Catalog.def");
		try(FileInputStream fis = new FileInputStream(fichier); ObjectInputStream ois = new ObjectInputStream(fis)){
			dbdef = (DBDef) ois.readObject();
		} catch (FileNotFoundException e) { // si le fichier Catalog.def n'existe pas
			dbdef = new DBDef(); // on initialise la classe sans rien en plus
			try {
				fichier.createNewFile(); // On crée le fichier
			} catch (IOException ioe) {
				throw new SGBDException("Impossible de creer un fichier");
			}
		} catch (IOException e) { // S'il y a une autre erreurr d'I/O
			throw new SGBDException("Erreur lors de la lecture de l'objet DBDef dans le fichier Catalog.def");
		} catch (ClassNotFoundException e) {
			throw new SGBDException("Euh c'est bizarre là, la classe DBDef ne trouve pas la classe DBDef");
		}
	}
	
	public void finish() {
		
	}
}
