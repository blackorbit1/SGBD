package bdda;

import java.nio.ByteBuffer;

public class BufferManager {
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
	public ByteBuffer getPage(int iPageId) {// vérifier si la page est déjà chargée
		return();
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
