package bdda;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import exception.ReqException;
import exception.SGBDException;

public class HeapFile {
	private RelDef pointeur;

	public RelDef getPointeur() {
		return pointeur;
	}

	public void setPointeur(RelDef pointeur) {
		this.pointeur = pointeur;
	}

	/**
	 * Gerer la creation du fichier disque correspondant et le rajout d’une
	 * HeaderPage « vide » a ce fichier
	 * 
	 * @throws ReqException,SGBDException
	 * @throws IOException
	 */
	public void createNewOnDisk() throws IOException, ReqException, SGBDException {
		// On creer un nouveau fichier qui correspond a l'id donner par le pointeur(qui
		// correspond la relation concerne)
		try {
			DiskManager.getInstance().createFile(pointeur.getFileIdx());
			PageId newHeaderPage = new PageId(pointeur.getFileIdx(), 0);
			// Pour ajouter une page on a besoin d'une nouvelle page qui correspond a la
			// HeaderPage
			// l'identifiant de la headerpage sera toujours 0 car il s'agit
			// de la premiere page du fichier
			DiskManager.getInstance().addPage(pointeur.getFileIdx(), newHeaderPage);
			ByteBuffer bufferNewHeaderPage = BufferManager.getInstance().getPage(newHeaderPage);
			HeaderPageInfo headerPageInfo = new HeaderPageInfo();
			headerPageInfo.setDataPageCount(0);
			headerPageInfo.writeToBuffer(bufferNewHeaderPage);
			// Pas plutot mettre le type dirty en int ?
			// Comme dans les exercices vu en amphi ?
			BufferManager.getInstance().freePage(newHeaderPage, true);
		} catch (IOException e) {
			throw new SGBDException("Erreur au niveau de la creation du fichier sur le disque (HeapFile)");
		}

	}

	/**
	 * Cette methode doit remplir l'argument oPageId avec l'identifiant d'une page
	 * de donnees sur laquelle il reste des cases disponibles. Si cela n'est pas le
	 * cas, la methode gere le rajout d'une page (libre) et l'actualisation des
	 * informations de la Header Page.
	 */
	private void getFreePageId(PageId oPageId) throws SGBDException {
		oPageId.setFileIdx(pointeur.getFileIdx());
		try {
			PageId headerpage = new PageId(pointeur.getFileIdx(), 0);
			ByteBuffer bufferHeaderPage = BufferManager.getInstance().getPage(headerpage);
			HeaderPageInfo headerPageI = new HeaderPageInfo();
			headerPageI.readFromBuffer(bufferHeaderPage);

			boolean slotDisponible = false;
			for (DataPage d : headerPageI.getListePages()) {
				if (d.getFreeSlots() > 0) {
					oPageId.setPageIdx(d.getPageIdx());
					BufferManager.getInstance().freePage(headerpage, false);
					slotDisponible = true;
					break;
				}
			}
			if (!(slotDisponible)) {

				PageId newpid = new PageId();
				DiskManager.getInstance().addPage(pointeur.getFileIdx(), newpid);
				oPageId.setPageIdx(newpid.getPageIdx());
				headerPageI.addDataPage(new DataPage(oPageId.getPageIdx(), pointeur.getSlotCount()));

				headerPageI.writeToBuffer(bufferHeaderPage);
				BufferManager.getInstance().freePage(headerpage, true);

				ByteBuffer nouvellePage = BufferManager.getInstance().getPage(newpid);

				// Ecrire une sequence de 0 au debut de la page (bytemap) pour signifier que
				// toutes les cases sont vides
				nouvellePage.position(0);
				for (int i = 0; i < pointeur.getSlotCount(); i++) {
					nouvellePage.put((byte) 0);
				}

				BufferManager.getInstance().freePage(newpid, true);

			}
		} catch (IOException e) {
			throw new SGBDException("Erreur d'I/O lors de la creation d'une page (HeapFile)");
		}
	}

	/**
	 * actualise les informations dans la Header Page suite a l’occupation d’une des
	 * cases disponible sur une page
	 * 
	 * @param iPageId
	 *            de la page a modifier
	 * @throws SGBDException
	 */
	private void updateHeaderWithTakenSlot(PageId iPageId) throws SGBDException {

		PageId headerPage = new PageId(pointeur.getFileIdx(), 0);
		try {
			ByteBuffer bufferHeaderPage = BufferManager.getInstance().getPage(headerPage);
			HeaderPageInfo hpi = new HeaderPageInfo();
			hpi.readFromBuffer(bufferHeaderPage);
			boolean pageTrouver = false;
			for (DataPage d : hpi.getListePages()) {
				if (d.getPageIdx() == iPageId.getPageIdx()) {
					// d.setPageIdx(iPageId.getPageIdx());
					// d.setFreeSlots((d.getFreeSlots() + 1)); -Faux
					d.setFreeSlots((d.getFreeSlots() - 1));
					pageTrouver = true;
					break;
				}
			}
			if (pageTrouver) {
				hpi.writeToBuffer(bufferHeaderPage);
				BufferManager.getInstance().freePage(headerPage, true);
			}

		} catch (SGBDException e) {
			throw new SGBDException("Erreur d'I/O lors de la creation d'une page (HeapFile)");
		}

	}

	/**
	 * Cette methode prend en argument un Record, un buffer (=une page en « format
	 * buffer ») et l’indice d’une case dans la page. Elle doit ecrire le Record
	 * dans le buffer a la position (offset) qui va bien – a vous de calculer cette
	 * position, en fonction de l’indice de la case, la taille du record, et sans
	 * oublier la presence de la bytemap au debut du buffer (rappel : la taille de
	 * la bytemap est donnee par la variable slotCount de la RelDef).
	 * 
	 * @param iRecord
	 * @param ioBuffer
	 * @param iSlotIdx
	 */
	private void writeRecordInBuffer(Record iRecord, ByteBuffer ioBuffer, int iSlotIdx) {

		long positionDebutByteMap = pointeur.getSlotCount();
		ioBuffer.position(pointeur.getSlotCount() + (iSlotIdx * pointeur.getRecordSize()));

		for (int i = 0; i < iRecord.getValues().size(); i++) {
			String valeur = iRecord.getValues().get(i);
			String type = pointeur.getType().get(i);

			if (type.equals("int")) {
				ioBuffer.putInt(Integer.parseInt(valeur));
			} else if (type.equals("float")) {
				ioBuffer.putFloat(Float.parseFloat(valeur));
			} else if (type.equals("string")) {
				int taille = Integer.parseInt(type.substring(6));
				for (int j = 0; j < taille; j++) {
					ioBuffer.putChar(valeur.charAt(j));
				}

			}

		}

	}

	/**
	 * Cette methode prend en argument un Record et un PageId et ecrit le record
	 * dans la page comme suit : - d’abord, le buffer de la page est obtenu via le
	 * BufferManager. - ensuite, nous cherchons, avec la bytemap, une case
	 * disponible ; nous supposons qu’une telle case existe a l’appel de cette
	 * methode ! - puis nous ecrivons le Record avec writeRecordInBuffer - puis nous
	 * actualisons la bytemap pour specifier que la case est « occupee » (octet de
	 * valeur 1) - enfin, nous liberons le buffer de la page aupres du BufferManager
	 * 
	 * @param iRecord
	 * @param iPageId
	 * @return Rid
	 * @throws SGBDException
	 */
	private Rid insertRecordInPage(Record iRecord, PageId iPageId) throws SGBDException {

		try {
			ByteBuffer bufferPage = BufferManager.getInstance().getPage(iPageId);
			bufferPage.position(0);
			int slotId = 0;
			for (int i = 0; i < pointeur.getSlotCount(); i++) {
				// TODO Modification ici
				if (bufferPage.get(i) == 0) {
					slotId = i;
					writeRecordInBuffer(iRecord, bufferPage, i);
					bufferPage.position(i);
					// Attention peut être faux byte en int
					bufferPage.put((byte) 1);
					// A verifier appel update
					updateHeaderWithTakenSlot(iPageId);
					BufferManager.getInstance().freePage(iPageId, true);
					break;
				}

			}
			return new Rid(iPageId, slotId);

		} catch (SGBDException e) {
			e.printStackTrace();
			throw new SGBDException("Erreur d'insertion du record : pas de place libre sur la page");
		}

	}

	public Record readRecordFromBuffer(ByteBuffer iBuffer, int iSlotIdx) {
		Record record = new Record();
		iBuffer.position(pointeur.getSlotCount() + (iSlotIdx * pointeur.getRecordSize()));

		for (int i = 0; i < pointeur.getNbColonne(); i++) {
			String type = pointeur.getType().get(i);

			if (type.equals("int")) {
				record.addValue(Integer.toString(iBuffer.getInt()));
			} else if (type.equals("float")) {
				record.addValue(Float.toString(iBuffer.getFloat()));
			} else if (type.equals("string")) {
				int taille = Integer.parseInt(type.substring(6));
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < taille; j++) {
					sb.append(iBuffer.getChar());
				}
				record.addValue(sb.toString());

			}

		}

		return record;

	}

	public ArrayList<Record> getRecordsOnPage(PageId iPageId) {
		ArrayList<Record> listRecord = new ArrayList<Record>();
		try {
			ByteBuffer bfPage = BufferManager.getInstance().getPage(iPageId);
			bfPage.position(0);
			for (int i = 0; i < pointeur.getSlotCount(); i++) {
				if (bfPage.get(i) == 1) {
					listRecord.add(readRecordFromBuffer(bfPage, i));
				}
			}
			BufferManager.getInstance().freePage(iPageId, false);
			return listRecord;
		} catch (SGBDException e) {
			e.printStackTrace();
		}
		return listRecord;

	}

	public ArrayList<PageId> getDataPagesIds() {
		ArrayList<PageId> listPageId = new ArrayList<PageId>();
		try {

			PageId headerpage = new PageId(pointeur.getFileIdx(), 0);
			ByteBuffer bufferHeaderPage;
			bufferHeaderPage = BufferManager.getInstance().getPage(headerpage);
			HeaderPageInfo headerPageI = new HeaderPageInfo();
			headerPageI.readFromBuffer(bufferHeaderPage);

			for (DataPage d : headerPageI.getListePages()) {
				listPageId.add(new PageId(pointeur.getFileIdx(), d.getPageIdx()));
			}

			return listPageId;

		} catch (SGBDException e) {
			e.printStackTrace();
		}

		return listPageId;

	}

	/**
	 * Insere un record dans une page
	 * 
	 * @param iRecord
	 * @return Rid
	 */
	public Rid insertRecord(Record iRecord) {
		PageId pid = new PageId();
		try {
			getFreePageId(pid);
			return insertRecordInPage(iRecord, pid);

		} catch (SGBDException e) {
			System.out.println(e);
		}
		return null;

	}

}
