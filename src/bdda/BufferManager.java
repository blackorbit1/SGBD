package bdda;

import exception.SGBDException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;

public class BufferManager {
    private ArrayList<Frame> frames = new ArrayList<>();

	//Pour avoir une unique instance de BufferManager
	private static final BufferManager instance = new BufferManager();
	private BufferManager() {}
	public static BufferManager getInstance() {
		return instance;
	}


	// il faut toujours incrémenter le pin_count

	/** Retourne un des buffers qui stockent le contenu d’une page dans une des cases
	 * @param iPageId l'ID de la page en question
	 *
	 * @return un des buffer qui stockent le contenu de la page
	 */
	public ByteBuffer getPage(PageId iPageId) throws SGBDException {// vérifier si la page est déjà chargée
	    //ByteBuffer bf = ByteBuffer.allocateDirect(Constantes.pageSize);
        String chaine;


        // On vérifie si la page est pas déjà dans une frame de notre tableau.
        for(int i = 0; i<frames.size();i++){
            if(frames.get(i).getPageId().getPageIdx() == iPageId.getPageIdx()){
            	return(frames.get(i).getContent());
            }
        }

        /* Si elle l'est pas, on vérifie qu'il reste de la place pour la mettre en mémoire si oui c'est bon
         * sinon on applique la politique de remplacement, on l'ajoute en mémoire et on la retourne.
         */
        if(frames.size() < Constantes.frameCount){ // Il reste de la place
        	// On crée une instance de frame qui va contenir notre page
        	Frame page = new Frame(iPageId);

        	// On crée une instance de ByteBuffer
			ByteBuffer bf = ByteBuffer.allocateDirect(Constantes.pageSize);

			// On passe notre instance de ByteBuffer par référence pour que Diskmanager nous mette le contenu qu'il faut dedans
			DiskManager.getInstance().readPage(iPageId.getPageIdx(), bf);

			// On met le contenu de la page demandée dans la frame
        	page.setContent(bf);

        	// On une référence à notre nouvelle frame dans le tableau des frames
        	frames.add(page);
		} else if(frames.size() == Constantes.frameCount){ // Il n'y a plus de places
        	int indexOldestDate = 0; // l'index de la frame dont le pin count est passé à 0 le moins recemment
			Date oldestDate = new Date(); // la date de la frame dont le pin count est passé à 0 le moins recemment

			// On cherche la frame dont le pin count est passé à 0 le moins recemment
        	for(int i = 0; i<frames.size(); i++){
        		if(frames.get(i).getUnpinned().getTime() < oldestDate.getTime()){
        			indexOldestDate = i;
        			oldestDate = frames.get(i).getUnpinned();

        			// On enregistre les modifications faites à la page associée à la frame qu'on va utiliser pour notre nouvelle page
        			freePage(i, frames.get(i).isDirty());
				}
			}

			Frame instanceFrameTraitee = frames.get(indexOldestDate); // Création d'un pointeur vers la frame à changer pour simplifier le code

			// Installation de la nouvelle page dans la frame
        	instanceFrameTraitee.setPageId(iPageId);
        	instanceFrameTraitee.setPin_count(1);
			DiskManager.getInstance().readPage(iPageId.getPageIdx(), instanceFrameTraitee.getContent());
		} else {
			throw new SGBDException("Le tableau des frames a plus de frames que le maximum autorisé");
		}

		return null;
	}

	/** Libérer une page
	 * @param indexFrame l'index de la frame qui contient la page à virer
	 * @param iIsDirty TRUE pour changé FALSE pour inchangé
	 */
	public void freePage(int indexFrame, boolean iIsDirty){
		if(iIsDirty){
			// TODO ==> Il manque une méthode .writePage() ou .savePage() dans le DiskManager
		}
		frames.get(indexFrame).setPin_count(0);
		frames.get(indexFrame).setPageId(new PageId()/* TODO qu'est ce qu'on met ? */);
		frames.get(indexFrame).setContent(ByteBuffer.allocateDirect(Constantes.pageSize));
		frames.get(indexFrame).setDirty(false);



	}

	/** Libérer toutes les pages du tableau de frames
	 */
	public void flushAll(){
		for (int i = 0; i<frames.size(); i++){
			freePage(i, frames.get(i).isDirty());
		}
	}
}
