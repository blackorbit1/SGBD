package bdda;

import java.util.ArrayList;

public class RelDef {
	private String nom;
	private int nbColonne;
	private ArrayList<String> type;
	private int recordSize;
	private int slotCount;
	
	private static int compteurDeRelDef = 0;

	public RelDef() {
		this.nom = "";
		this.nbColonne = 0;
		this.type = new ArrayList<String>();
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getNbColonne() {
		return nbColonne;
	}

	public void setNbColonne(int nbColonne) {
		this.nbColonne = nbColonne;
	}

	public ArrayList<String> getType() {
		return type;
	}

	public void setType(ArrayList<String> type) {
		this.type = type;
	}

}
