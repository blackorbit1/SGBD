package bdda;

import java.util.ArrayList;
import java.util.List;

public class FileManager {
	
	private List<HeapFile> liste;
	private static final FileManager fileManager = new FileManager();
	
	private FileManager() {
		this.liste=liste;
	}
	
	public static FileManager getInstance() {
		return fileManager;
	}
	
	public void init() {
		ArrayList<RelDef> list = DBDef.getInstance().getListeDeRelDef();
		for(int i = 0; i<list.size();i++) {
			RelDef relDef = (RelDef) list.get(i);
			HeapFile heapFile = new HeapFile();
			heapFile.setPointeur(relDef);
			//Creer une liste de heapfile ? ajouter au mm indice
			// que le relDef associé ?
		}
	}

	public void createNewHeapFile() {
		
	}
}
