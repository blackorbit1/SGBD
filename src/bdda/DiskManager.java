package bdda;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DiskManager {

	private static final DiskManager diskManager = new DiskManager();
	private ByteBuffer data;

	private DiskManager() {
	}

	public static DiskManager getInstance() {
		return diskManager;
	}

	/**
	 * Cette fonction permet de creer un niveau fichier dont le nom est :
	 * "Data_(identifiantDuFichier).rf"
	 * 
	 * @param iFileIdx identifiant du fichier qu'on creer
	 * @throws IOException
	 */
	public void createFile(int iFileIdx) throws IOException {
		File file = new File(Constantes.pathName + iFileIdx + ".rf");
		file.createNewFile();
	}

	/**
	 * Cette fonction ajoute une nouvelle page de taille 4ko
	 * @param iFileIdx identifiant du fichier
	 * @param oPageId de type PageId qui comprend le numero de la page et l'identifant du fichier
	 * @throws IOException
	 */
	public void addPage(int iFileIdx, PageId oPageId) throws IOException {
		// ajouter la page
		try (RandomAccessFile rf = new RandomAccessFile(Constantes.pathName + iFileIdx + ".rf", "r")) {
			byte[] tab = new byte[Constantes.pageSize];
			//On a la taille entiere de la page en bytes avec length
			//On sait que une page vaut 4ko donc on divise le tout par 4ko et on trouve le nombre de page
			//Exemple si ya que 3 pages de 4ko alors faut ecrire la nouvelle page a la position 3
			//Autre exemple si dans la page y'a rien on a 0/4ko => 0 donc on met direct la nouvelle page 
			//a la postion 0
			long position = (rf.length()/Constantes.pageSize);
			//seek permet de connaitre la derniere position du fichier
			rf.seek(position);
			for (Byte b : tab) {
				rf.writeByte(0);
			}
		}
		// Postionner à la fin de la dernière page
		// rf.seek(pos);
		// rf.write(data.wrap(tab).getInt());
		//On indique a PageId dans quel fichier on ajoute la nouvelle page
		oPageId.setFileIdx(iFileIdx);
	}

	/**
	 * Permet de lire une page (input)
	 * @param iPageId
	 * @param oBuffer
	 * @throws FileNotFoundException
	 */
	public void readPage(PageId iPageId, ByteBuffer iBuffer) throws FileNotFoundException {
		RandomAccessFile readFile = new RandomAccessFile(Constantes.pathName + iPageId.getFileIdx() + ".rf", "r");

	}

	/**
	 * Permet d'ecrire sur dans une page (output)
	 * @param iPageId
	 * @param oBuffer
	 * @throws IOException 
	 */
	public void writePage(PageId iPageId, ByteBuffer oBuffer) throws IOException {
		//On recupere le fichier qui contient la page qu'on veut modifier
		RandomAccessFile writeFile = new RandomAccessFile(Constantes.pathName + iPageId.getFileIdx() + ".rf", "w");
	}
}
