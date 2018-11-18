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
	 * Gerer la création du fichier disque correspondant et le rajout d’une
	 * HeaderPage « vide » à ce fichier
	 */
	public void createNewOnDisk() {

	}

	/*
	 * Cette m�thode doit remplir l�argument oPageId avec l�identifiant d�une page
	 * de donn�es sur laquelle il reste des cases disponibles. Si cela n�est pas le
	 * cas, la m�thode g�re le rajout d�une page (libre) et l�actualisation des
	 * informations de la Header Page.
	 */
	public void getFreePageId(PageId oPageId) {
		// oPageId.setFileIdx(pointeur.fileIdx);
		try {
			PageId headerpage = new PageId(pointeur.fileIdx, 0);
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
				DiskManager.getInstance().addPage(pointeur.fileIdx, newpid);
				oPageId.setPageIdx(newpid.getPageIdx());
				headerPageI.getListePages().add(new DataPage(oPageId.getPageIdx(), pointeur.getSlotCount()));

				headerPageI.writeToBuffer(bufferHeaderPage);
				BufferManager.getInstance().freePage(headerpage, true);

				ByteBuffer nouvellePage = BufferManager.getInstance().getPage(newpid);

				// �crire une s�quence de 0 au d�but de la page (bytemap) pour signifier que
				// toutes les cases sont vides
				nouvellePage.position(0);
				for (int i = 0; i < pointeur.getSlotCount(); i++) {
					nouvellePage.putInt(0);
				}

				BufferManager.getInstance().freePage(newpid, true);

			}

		} catch (SGBDException e) {

			e.printStackTrace();
		}
	}

	public void updateHeaderWithTakenSlot(PageId iPageId) {

	}

}
