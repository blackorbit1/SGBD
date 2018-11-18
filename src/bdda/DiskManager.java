package bdda;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import exception.ReqException;

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
	 * @throws ReqException
	 */
	public void createFile(int iFileIdx) throws IOException, ReqException {
		if (iFileIdx < 0)
			throw new ReqException("L'id du fichier doit être supérieur à 0");

		File file = new File(Constantes.pathName + "Data_" + iFileIdx + ".rf");
		System.out.println(file.getAbsolutePath());
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	/**
	 * Cette fonction ajoute une nouvelle page de taille 4ko à la fin du fichier (
	 * pour l'instant)
	 * 
	 * @param iFileIdx identifiant du fichier
	 * @param oPageId  de type PageId qui comprend le numero de la page et
	 *                 l'identifant du fichier
	 * @throws IOException
	 */
	public void addPage(int iFileIdx, PageId oPageId) throws IOException {
		// ajouter la page
		try (RandomAccessFile rf = new RandomAccessFile(Constantes.pathName + "Data_" + iFileIdx + ".rf", "rw")) {
			byte[] tab = new byte[Constantes.pageSize];
			for (Byte b : tab) {
				b = 0;
			}

			ByteBuffer bf = ByteBuffer.wrap(tab);
			// On stocke le numero de la page dans le buffer ( les 4 premiers octets de la
			// page)

			// bf.putInt(0, oPageId.getPageIdx());
			// On a la taille entiere de la page en bytes avec length
			// On sait que une page vaut 4ko donc on divise le tout par 4ko et on trouve le
			// nombre de page
			// Exemple si ya que 3 pages de 4ko alors faut ecrire la nouvelle page a la
			// position 3
			// Autre exemple si dans la page y'a rien on a 0/4ko => 0 donc on met direct la
			// nouvelle page
			// a la postion 0
			// long position = ((rf.length() / Constantes.pageSize);

			// seek permet de connaitre la derniere position du fichier
			rf.seek(rf.length());

			// On écrit le contenu du buffer
			rf.write(bf.array());

			oPageId.setFileIdx(iFileIdx);
			oPageId.setPageIdx((int) (rf.length() / Constantes.pageSize));
		}

	}

	/**
	 * Permet de lire une page (input)
	 * 
	 * @param iPageId
	 * @param oBuffer
	 * @throws IOException
	 */
	public void readPage(PageId iPageId, ByteBuffer iBuffer) throws IOException {
		try (RandomAccessFile readFile = new RandomAccessFile(
				Constantes.pathName + "Data_" + iPageId.getFileIdx() + ".rf", "r")) {

			FileChannel fc = readFile.getChannel();
			long position = iPageId.getPageIdx() * Constantes.pageSize;
			fc.position(position);
			fc.read(iBuffer);
		}

	}

	/**
	 * Permet d'ecrire dans une page (output)
	 * 
	 * @param iPageId
	 * @param oBuffer
	 * @throws IOException
	 */
	public void writePage(PageId iPageId, ByteBuffer oBuffer) throws IOException {
		// On recupere le fichier qui contient la page qu'on veut modifier

		try (RandomAccessFile writeFile = new RandomAccessFile(Constantes.pathName + iPageId.getFileIdx() + ".rf",
				"w")) {

			FileChannel fc = writeFile.getChannel();
			long position = iPageId.getPageIdx() * Constantes.pageSize;
			fc.position(position);
			fc.write(oBuffer);
		}
	}

	public static void main(String[] args) throws IOException, ReqException {
		DiskManager dm = DiskManager.getInstance();
		dm.createFile(2);
		
		PageId pid = new PageId();
		PageId pid2 = new PageId();
		PageId pid3 = new PageId();

		dm.addPage(2, pid);
		dm.addPage(2, pid2);
		dm.addPage(2, pid3);

	}
}
