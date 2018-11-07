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

	public void createFile(int iFileIdx) throws IOException {
		File file = new File("\\DB\\Data_" + iFileIdx + ".rf");
		file.createNewFile();
	}

	public void addPage(int iFileIdx, PageId oPageId) throws IOException {
		// ajouter la page
		// ByteBuffer
		RandomAccessFile rf = new RandomAccessFile("\\DB\\Data_" + Integer.toString(oPageId.getPageIdx()) + ".rf", "r");
		data = ByteBuffer.allocateDirect(Constantes.pageSize);
		byte[] tab = new byte[Constantes.pageSize];
		for (Byte b : tab) {
			b = 0;
		}
		// Postionner à la fin de la dernière page
		rf.seek(pos);

		rf.write(data.wrap(tab).getInt());
		rf.close();

		oPageId.setFileIdx(iFileIdx);

	}

	public void readPage(PageId iPageId, ByteBuffer oBuffer) throws FileNotFoundException {
		RandomAccessFile readFile = new RandomAccessFile("\\DB\\Data_" + Integer.toString(iPageId.getPageIdx()) + ".rf",
				"r");

	}

	public void writePage(PageId iPageId, ByteBuffer iBuffer) throws FileNotFoundException {
		RandomAccessFile writeFile = new RandomAccessFile(
				"\\DB\\Data_" + Integer.toString(iPageId.getPageIdx()) + ".rf", "w");

	}
}
