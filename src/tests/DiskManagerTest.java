package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import bdda.Constantes;
import bdda.DiskManager;
import bdda.PageId;
import exception.SGBDException;

class DiskManagerTest {

	@Test
	void test() {
		DiskManager dm = DiskManager.getInstance();
		try {
			dm.createFile(2);

			PageId pid = new PageId();
			PageId pid2 = new PageId();
			PageId pid3 = new PageId();

			dm.addPage(2, pid);
			dm.addPage(2, pid2);
			dm.addPage(2, pid3);

			ByteBuffer bf = ByteBuffer.allocate(Constantes.pageSize);
			while (bf.hasRemaining()) {
				bf.putInt(1);
			}

			ByteBuffer copie = ByteBuffer.allocate(Constantes.pageSize);
			dm.writePage(pid, bf);
			dm.readPage(pid, copie);

			while (bf.hasRemaining()) {
				assertEquals(bf.getInt(), copie.getInt());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SGBDException e) {
			e.printStackTrace();
		}

	}

}
