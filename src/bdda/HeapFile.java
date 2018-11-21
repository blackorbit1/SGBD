package bdda;

import java.io.IOException;
import java.nio.ByteBuffer;

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
	 * Gerer la création du fichier disque correspondant et le rajout d’une
	 * HeaderPage « vide » à ce fichier
	 * 
	 * @throws ReqException,SGBDException
	 * @throws IOException
	 */
	public void createNewOnDisk(PageId pageId) throws IOException, ReqException, SGBDException {
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
			throw new SGBDException("Erreur au niveau de la création du fichier sur le disque");
		}

	}

	/**
	 * Cette methode doit remplir l'argument oPageId avec l'identifiant d'une page
	 * de donnees sur laquelle il reste des cases disponibles. Si cela n'est pas le
	 * cas, la methode gere le rajout d'une page (libre) et l'actualisation des
	 * informations de la Header Page.
	 */
	public void getFreePageId(PageId oPageId) throws SGBDException {
		// oPageId.setFileIdx(pointeur.fileIdx);
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
				headerPageI.getListePages().add(new DataPage(oPageId.getPageIdx(), pointeur.getSlotCount()));

				headerPageI.writeToBuffer(bufferHeaderPage);
				BufferManager.getInstance().freePage(headerpage, true);

				ByteBuffer nouvellePage = BufferManager.getInstance().getPage(newpid);

				// Ecrire une sequence de 0 au debut de la page (bytemap) pour signifier que
				// toutes les cases sont vides
				nouvellePage.position(0);
				for (int i = 0; i < pointeur.getSlotCount(); i++) {
					nouvellePage.putInt(0);
				}

				BufferManager.getInstance().freePage(newpid, true);

			}
		} catch (IOException e) {
			throw new SGBDException("Erreur d'I/O lors de la création d'une page");
		}
	}

	/**
	 *  actualise les informations dans la Header Page suite à l’occupation d’une des cases disponible sur une page
	 * @param iPageId de la page a modifier
	 * @throws SGBDException 
	 */
	public void updateHeaderWithTakenSlot(PageId iPageId) throws SGBDException {

		PageId headerPage = new PageId(pointeur.getFileIdx(), 0);
		try {
			ByteBuffer bufferHeaderPage = BufferManager.getInstance().getPage(headerPage) ;
			HeaderPageInfo hpi = new HeaderPageInfo();
			hpi.readFromBuffer(bufferHeaderPage); 
			boolean pageTrouver=false;
			for (DataPage d : hpi.getListePages()) {
				if (d.getPageIdx() == iPageId.getPageIdx()) {
					d.setPageIdx(iPageId.getPageIdx());
					d.setFreeSlots((d.getFreeSlots()+1));
					pageTrouver = true;
					break;
				}
			}
			if(pageTrouver){
				hpi.writeToBuffer(bufferHeaderPage);
				BufferManager.getInstance().freePage(headerPage, true);
			}
			
		} catch (SGBDException e) {
			throw new SGBDException("Erreur d'I/O lors de la creation d'une page");
		}

	}

}
