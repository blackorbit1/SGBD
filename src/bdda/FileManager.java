package bdda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
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

	public List<HeapFile> getListe() {
		return listeHeapFiles;
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

				//TODO le print de debug qu'on utilisait depuis le début
                //System.out.println("Insertion dans la relation : " + iRelationName + " ID: "+ rid.getPageId().getFileIdx() +" page  n°: "+ rid.getPageId().getPageIdx() + " slot n°: " + rid.getSlotIdx() );

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
		boolean trouve = false;
		for (int i = 0; i < listeHeapFiles.size(); i++) {
			if (listeHeapFiles.get(i).getPointeur().getNom().equals(iRelationName)) {
				List<PageId> listePageId = new ArrayList<PageId>();
				listePageId = listeHeapFiles.get(i).getDataPagesIds();
				for (int j = 0; j < listePageId.size(); j++) {
					listeDeRecords.addAll(listeHeapFiles.get(i).getRecordsOnPage(listePageId.get(j)));
				}
				trouve = true;
			}
		}
		if(trouve){
			return listeDeRecords;
		} else {
			throw new SGBDException("Relation introuvable dans la liste des HeapFiles");
		}

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
		boolean trouve = false;
		for (int i = 0; i < listeHeapFiles.size(); i++) {
			// On cherche dans les heapfile qui correspond au nom de la relation
			if (listeHeapFiles.get(i).getPointeur().getNom().equals(iRelationName)) {
				// on recupere tous les rel
				List<RelDef> listeRelations = DBDef.getInstance().getListeDeRelDef();
				for (int j = 0; j < listeRelations.size(); j++) {
					// on regarde ou est ce qu'on a la relation
					if (listeHeapFiles.get(i).getPointeur().getNom() == listeRelations.get(j).getNom()) {
						List<PageId> listePageId = new ArrayList<PageId>();
						listePageId = listeHeapFiles.get(i).getDataPagesIds();
						for (int k = 0; k < listePageId.size(); k++) {
							ArrayList<Record> tampon = new ArrayList<>();
							tampon.addAll(listeHeapFiles.get(i).getRecordsOnPage(listePageId.get(k)));
							for(Record record : tampon){
								//System.out.println(record.getValues().get(j));
								if(record.getValues().get(iIdxCol).equals(iValeur)){
									allRecordsFilter.add(record);
								}
							}
						}
						trouve = true;
						/*
						else {
							throw new ReqException("La valeur de la colonne ne correspond pas à la valeur attendue");
						}
						*/
					}
					/*
					else {
						throw new SGBDException("La relation est introuvable dans la liste de relations de DBDef");
					}
					*/
				}
			}
			/*
			else {
				throw new SGBDException("Relation introuvable avec iRelationName dans la liste des HeapFiles");
			}
			*/
		}
		if(trouve){
			return allRecordsFilter;
		} else {
			throw new SGBDException("Relation introuvable dans la liste des HeapFiles");
		}
		//return allRecordsFilter;
	}

	public void reset(){
		listeHeapFiles = new ArrayList<>();
	}
}