package bdda;

import java.util.ArrayList;

public class DBDef {
	private ArrayList<RelDef> listeDeRelDef;
	private int compteurRel;
	
	private static final DBDef dbdef = new DBDef();
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
	
	public void init() {
		
	}
	
	public void finish() {
		
	}
}
