package bdda;

import java.io.File;
import java.nio.ByteBuffer;


public class DiskManager {

	private static final DiskManager diskManager = new DiskManager();

	private DiskManager() {
	}

	public static DiskManager getInstance() {
		return diskManager;
	}

	public void createFile(int iFileIdx) {
		File file = new File("\\DB\\Data_" + iFileIdx + ".rf");
	}

	public void addPage(int iFileIdx, PageId oPageId) {
		//ajouter la page
		oPageId.setFileIdx(iFileIdx);
	}

	public void readPage(int iPageId, ByteBuffer oBuffer) {

	}

}
