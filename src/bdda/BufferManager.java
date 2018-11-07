package bdda;

import java.nio.ByteBuffer;
import java.util.ArrayList;

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
	public ByteBuffer getPage(PageId iPageId) {// vérifier si la page est déjà chargée
	    ByteBuffer bf = ByteBuffer.allocateDirect(Constantes.pageSize);
        String chaine;
        for(int i = 0; i<frames.size();i++){
            if(frames.get(i).getPageIdx() == iPageId.getPageIdx()){
                int dirty = frames.get(i).isDirty()?1:0;
                chaine += "///frame@pid=" + frames.get(i).getPageIdx() + "@d=" + dirty + "@pc=" + frames.get(i).getPin_count();
            }
        }
		return(bf.put(chaine));
	}

	/** Libérer une page
	 * @param iPageId l'ID de la page en question
	 * @param iIsDirty TRUE pour changé FALSE pour inchangé
	 */
	public void freePage(PageId iPageId, boolean iIsDirty){

	}

	/** Tout enregistrer sur le disque et mettre les dirty en false
	 *
	 */
	public void flushAll(){

	}
}
