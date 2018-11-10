package bdda;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import exception.ReqException;
import sun.font.CreatedFontTracker;

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

			ByteBuffer bf = ByteBuffer.wrap(tab);
			// On stocke le numero de la page dans le buffer ( les 4 premiers octets de la
			// page)

			bf.putInt(0, oPageId.getPageIdx());
			// On a la taille entiere de la page en bytes avec length
			// On sait que une page vaut 4ko donc on divise le tout par 4ko et on trouve le
			// nombre de page
			// Exemple si ya que 3 pages de 4ko alors faut ecrire la nouvelle page a la
			// position 3
			// Autre exemple si dans la page y'a rien on a 0/4ko => 0 donc on met direct la
			// nouvelle page
			// a la postion 0
			// long position = (rf.length() / Constantes.pageSize);

			// seek permet de connaitre la derniere position du fichier
			rf.seek(rf.length());

			// On écrit le contenu du buffer
			rf.write(bf.array());
		}
		// Postionner à la fin de la dernière page
		// rf.seek(pos);
		// rf.write(data.wrap(tab).getInt());
		// On indique a PageId dans quel fichier on ajoute la nouvelle page
		oPageId.setFileIdx(iFileIdx);
	}

	/**
	 * Permet de lire une page (input)
	 * 
	 * @param iPageId
	 * @param oBuffer
	 * @throws IOException
	 */
	public ByteBuffer readPage(PageId iPageId, ByteBuffer iBuffer) throws IOException {
		try (RandomAccessFile readFile = new RandomAccessFile(
				Constantes.pathName + "Data_" + iPageId.getFileIdx() + ".rf", "r")) {

			FileChannel fc = readFile.getChannel();
			long position = 0;
			readFile.seek(position);

			// Tant qu'on a pas atteint la fin du fichier
			while (!(readFile.length() == position)) {
				// Si le numéro de la page( entier sur 4 octects) correspond à la page recherché
				// On remplit le buffer avec le contenu de la page

				int num = readFile.readInt();
				if (num == iPageId.getPageIdx()) {
					// Optimisation retirer les 4 premiers octects car inutile
					fc.position(position);
					fc.read(iBuffer);
					return iBuffer;

				}
				// Prochaine page
				readFile.seek(position += Constantes.pageSize);
				System.out.println(position);

			}
			return null; // Si on a pas réussi à lire la page
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
		RandomAccessFile writeFile = new RandomAccessFile(Constantes.pathName + iPageId.getFileIdx() + ".rf", "w");
	}

	public static void main(String[] args) throws IOException, ReqException {
		DiskManager dm = DiskManager.getInstance();
		dm.createFile(2);
		PageId pid = new PageId();
		PageId pid2 = new PageId();
		PageId pid3 = new PageId();

		pid.setPageIdx(1);
		pid2.setPageIdx(2);
		pid3.setPageIdx(3);

		dm.addPage(2, pid);
		dm.addPage(2, pid2);
		dm.addPage(2, pid3);

		ByteBuffer bf = ByteBuffer.allocate(Constantes.pageSize);

		// test1

		// on lit la page avec l'id 1
		bf = dm.readPage(pid, bf);
		// on récupère le pageid qui est stocker dans le prem
		int n = bf.getInt(0);
		if (n == pid.getPageIdx()) {
			System.out.println("ok: " + n + " = " + pid.getPageIdx());
		}

		// test2
		bf.position(0);
		bf = dm.readPage(pid3, bf);
		n = bf.getInt(0);
		if (n == pid3.getPageIdx()) {
			System.out.println("ok: " + n + " = " + pid3.getPageIdx());
		}

		// test3
		bf.position(0);
		bf = dm.readPage(pid2, bf);
		n = bf.getInt(0);
		if (n == pid2.getPageIdx()) {
			System.out.println("ok: " + n + " = " + pid2.getPageIdx());
		}

	}
}
