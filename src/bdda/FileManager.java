package bdda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import exception.ReqException;
import exception.SGBDException;

public class FileManager {

	private List<HeapFile> listeHeapFiles = new ArrayList<>();
	private static final FileManager fileManager = new FileManager();

	private FileManager() {
	}

	public static FileManager getInstance() {
		return fileManager;
	}

	public void init() {
		ArrayList<RelDef> list = DBDef.getInstance().getListeDeRelDef();
		for (int i = 0; i < list.size(); i++) {
			HeapFile heapFile = new HeapFile();
			heapFile.setPointeur(list.get(i));
			listeHeapFiles.add(heapFile);
		}
	}

	public void createNewHeapFile(RelDef iRelDel) {
		HeapFile heapFile = new HeapFile();
		heapFile.setPointeur(iRelDel);
		listeHeapFiles.add(heapFile); // TODO j'ai ajouté ça (Enzo)
		try {
			heapFile.createNewOnDisk();
		} catch (IOException | ReqException | SGBDException e) {
			//TODO ça c'est un peu bof bof x)
			e.printStackTrace();
		}

	}

	/**
	 * fonction pour utiliser la methode insertRecord() du HeapFile correspondant a
	 * la relation en question
	 *
	 * @param iRelationName
	 *            (le nom de la relation)
	 * @param iRecord
	 *            (le contenu du tuple a ajouter)
	 * @return (l'ID du tuple ajoute)
	 */
	Rid insertRecordInRelation(String iRelationName, Record iRecord) throws SGBDException {
		// parcourir la liste des HeapFiles pour trouver celui qui correspond a la
		// relation en question, et ensuite appeler sa propre methode InsertRecord
		Rid rid = null;
		boolean found = false;
		//System.out.println(listeHeapFiles.size());
		for (int i = 0; i < listeHeapFiles.size(); i++) {
			//System.out.println(listeHeapFiles.get(i).getPointeur().getNom());
			if (listeHeapFiles.get(i).getPointeur().getNom().equals(iRelationName)) {
				rid = listeHeapFiles.get(i).insertRecord(iRecord);
				found = true;
			}
		}
		if (found) {
			return rid;
		} else {
			throw new SGBDException("La relation dans laquelle vous voulez insérer votre tuple n'est pas dans la liste des HeapFiles");
		}

	}

	public ArrayList<Record> getAllRecords(String iRelationName) throws SGBDException {
		ArrayList<Record> listeDeRecords = new ArrayList<Record>();
		for (int i = 0; i < listeHeapFiles.size(); i++) {
			if (listeHeapFiles.get(i).getPointeur().getNom().equals(iRelationName)) {
				List<PageId> listePageId = new ArrayList<PageId>();
				listePageId = listeHeapFiles.get(i).getDataPagesIds();
				for (int j = 0; j < listePageId.size(); j++) {
					listeDeRecords.addAll(listeHeapFiles.get(i).getRecordsOnPage(listePageId.get(j)));
				}
			} else {
				throw new SGBDException("Relation introuvable dans la liste des HeapFiles");
			}
		}
		return listeDeRecords;
	}

	/**
	 * Fonction qui renvoie une liste contenant tous les records si la valeur retourné par iIdxCol correspond bien
	 * au String donné en paramètre
	 * @throws ReqException 
	 * @throws SGBDException 
	 **/
	//Mais à quoi correspond le string exactement ?
	public ArrayList<Record> getAllRecordsWithFilter(String iRelationName, int iIdxCol, String iValeur) throws ReqException, SGBDException {
		ArrayList<Record> allRecordsFilter = new ArrayList<Record>();
		for (int i = 0; i < listeHeapFiles.size(); i++) {
			// On cherche dans les heapfile qui correspond au nom de la relation
			if (listeHeapFiles.get(i).getPointeur().getNom().equals(iRelationName)) {
				// on recupere tous les rel
				List<RelDef> listeRelations = DBDef.getInstance().getListeDeRelDef();
				for (int j = 0; j < listeRelations.size(); j++) {
					// on regarde ou est ce qu'on a la relation
					if (listeHeapFiles.get(i).getPointeur() == listeRelations.get(j)) {
						// on recupere le nombre de colonne de cette relation
						int nbColonnes = listeRelations.get(j).getNbColonne();
						ArrayList<String> typeColonne = new ArrayList<String>();
						// on recupere sa liste de type de colonnes
						typeColonne = listeRelations.get(j).getType();
						// on regarde si la valeur de la colonne correspond a la valeur entré en
						// parametre
						if (typeColonne.get(iIdxCol) == iValeur) {
							// si tout est bon alors on ajoute ses records
							// donc on reprend le mm principe que la fonction de getAllRecords
							//Mais on cherche a récupérer les records de la colonne concerné
							List<PageId> listePageId = new ArrayList<PageId>();
							listePageId = listeHeapFiles.get(i).getDataPagesIds();
							//On recupere la colonne du record
							//Je n'ai pas très bien compris à quoi correspondait exactement le String en parametre
							//Donc pour le moment j'ai fais ca mais c'est vraiment pas sur sur sur suuuuuur
							for (int k = 0; k < listePageId.size(); k++) {
								allRecordsFilter.add(listeHeapFiles.get(i).getRecordsOnPage(listePageId.get(k)).get(iIdxCol));
							}
						}
						else {
							throw new ReqException("La valeur de la colonne ne correspond pas à la valeur attendue");
						}
					}
					else {
						throw new SGBDException("La relation est introuvable dans la liste de relations de DBDef");
					}
				}
			}
			else {
				throw new SGBDException("Relation introuvable avec iRelationName dans la liste des HeapFiles");
			}
		}
		return allRecordsFilter;
	}
}