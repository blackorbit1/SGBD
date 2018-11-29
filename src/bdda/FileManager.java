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
		try {
			heapFile.createNewOnDisk();
		} catch (IOException | ReqException | SGBDException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<Record> getAllRecords(String iRelationName) throws SGBDException{
		// code
		//
		//  /!\ ne pas oublier de throw une SGBDException si la relation demandée ne se trouve pas dans la listeHeapFiles
		//
		return new ArrayList<Record>();
	}

	public ArrayList<Record> getAllRecordsWithFilter(String iRelationName, int iIdxCol, String iValeur) throws SGBDException, ReqException{
		// code
		//
		//  /!\ ne pas oublier de throw une SGBDException si la relation demandée ne se trouve pas dans la listeHeapFiles
		// 	/!\ ne pas oublier de throw une ReqException si la iValeur en argument est de type différent de celui de la colonne demandée
		//
		return new ArrayList<Record>();
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
		for (int i = 0; i < listeHeapFiles.size(); i++) {
			if (listeHeapFiles.get(i).getPointeur().getNom().equals(iRelationName)) {
				rid = listeHeapFiles.get(i).insertRecord(iRecord);
				found = true;
			}
		}
		if(found){
			return rid;
		} else {
			throw new SGBDException("La relation dans laquelle vous voulez insérer votre tuple n'est pas dans la liste des HeapFiles");
		}

	}
}
