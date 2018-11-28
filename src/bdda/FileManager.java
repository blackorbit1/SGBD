package bdda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import exception.ReqException;
import exception.SGBDException;

public class FileManager {

	private List<HeapFile> listeHeapFiles;
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

	/**
	 * fonction pour utiliser la méthode insertRecord() du HeapFile correspondant à
	 * la relation en question
	 *
	 * @param iRelationName
	 *            (le nom de la relation)
	 * @param iRecord
	 *            (le contenu du tuple à ajouter)
	 * @return (l'ID du tuple ajouté)
	 */
	Rid insertRecordInRelation(String iRelationName, Record iRecord) {
		// parcourir la liste des HeapFiles pour trouver celui qui correspond à la
		// relation en question, et ensuite appeler sa propre méthode InsertRecord
		Rid rid = null;
		for (int i = 0; i < listeHeapFiles.size(); i++) {
			if (listeHeapFiles.get(i).getPointeur().getNom().equals(iRelationName)) {
				rid = listeHeapFiles.get(i).insertRecord(iRecord);
			}
		}
		return rid;
	}
}