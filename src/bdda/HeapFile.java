package bdda;

public class HeapFile {
	private RelDef pointeur;


	public RelDef getPointeur() {
		return pointeur;
	}
	public void setPointeur(RelDef pointeur) {
		this.pointeur = pointeur;
	}
	
	/**
	 * Gerer la création du fichier disque correspondant et le rajout d’une HeaderPage « vide » à ce fichier
	 */
	public void createNewOnDisk () {
		
	}
	
	public void getFreePageId(PageId oPageId) {
		
	}
	
	public void updateHeaderWithTakenSlot(PageId iPageId) {
		
	}

}
