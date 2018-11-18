package bdda;

import java.nio.ByteBuffer;

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
	 * Gerer la crÃ©ation du fichier disque correspondant et le rajout dâ€™une
	 * HeaderPage Â« vide Â» Ã  ce fichier
	 */
	public void createNewOnDisk() {

	}

	public void getFreePageId(PageId oPageId) {
		// oPageId.setFileIdx(pointeur.fileIdx);
		try {
			PageId headerpage = new PageId(pointeur.fileIdx, 0);
			ByteBuffer bufferHeaderPage = BufferManager.getInstance().getPage(headerpage);
			HeaderPageInfo headerPageI = new HeaderPageInfo();
			headerPageI.readFromBuffer(bufferHeaderPage);

			for (DataPage d : headerPageI.getListePages()) {
				if (d.getFreeSlots() > 0) {
					oPageId.setPageIdx(d.getPageIdx());
					BufferManager.getInstance().freePage(headerpage, false);
				} else {
					PageId newpid = new PageId();
					DiskManager.getInstance().addPage(pointeur.fileIdx, newpid);
					oPageId.setPageIdx(newpid.getPageIdx());
					headerPageI.getListePages().add(new DataPage(oPageId.getPageIdx(), pointeur.getSlotCount()));

					headerPageI.writeToBuffer(bufferHeaderPage);
					BufferManager.getInstance().freePage(headerpage, true);

					ByteBuffer nouvellePage = BufferManager.getInstance().getPage(newpid);
					// écrire une séquence de 0 au début de la page (bytemap)
					//
					//

					BufferManager.getInstance().freePage(newpid, true);

				}
			}

		} catch (SGBDException e) {

			e.printStackTrace();
		}
	}

	public void updateHeaderWithTakenSlot(PageId iPageId) {

	}

}
