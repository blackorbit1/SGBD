package tests;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import bdda.DBDef;
import exception.SGBDException;

class DBDefTest {

	@Test
	void test() {
		DBDef test = DBDef.getInstance();
		try {
			test.init();
			File fichier = new File("Catalog.def");
			System.out.println(fichier.exists());
			Assertions.assertTrue(fichier.exists());
			
			
		} catch (SGBDException e) {
			e.printStackTrace();
		}
		
	}

}
