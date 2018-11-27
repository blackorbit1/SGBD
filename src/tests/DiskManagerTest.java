package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import bdda.Constantes;
import bdda.DiskManager;
import bdda.PageId;
import exception.ReqException;

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
			
			dm.writePage(pid, ByteBuffer.allocate(Constantes.pageSize));
			dm.readPage(pid, ByteBuffer.allocate(Constantes.pageSize));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ReqException e) {
			e.printStackTrace();
		}
		
	}

}
