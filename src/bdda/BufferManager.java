	package bdda;

import exception.SGBDException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;

public class BufferManager {
    private ArrayList<Frame> frames;

    //Pour avoir une unique instance de BufferManager
    private static final BufferManager instance = new BufferManager();

    /** Constructeur de cette classe qui cree un tableau de frames
     */
    private BufferManager() {
        this.frames = new ArrayList<Frame>();
        for(int i = 0; i<Constantes.frameCount; i++){
            this.frames.add(new Frame(null));
        }
    }

    public static BufferManager getInstance() {
        return instance;
    }


    // il faut toujours incrementer le pin_count

    /** Retourne un des buffers qui stockent le contenu d’une page dans une des cases
     * @param iPageId l'ID de la page en question
     *
     * @return un des buffer qui stockent le contenu de la page
     */
    public ByteBuffer getPage(PageId iPageId) throws SGBDException {// verifier si la page est deja chargee
        //ByteBuffer bf = ByteBuffer.allocateDirect(Constantes.pageSize);
        String chaine;


        // On verifie si la page est pas deja dans une frame de notre tableau
        for(int i = 0; i<frames.size();i++){
            if(frames.get(i).getPageId() == null) {
                // si la frame ne contient pas de page, on passe
            } else if(frames.get(i).getPageId().getPageIdx() == iPageId.getPageIdx()){
                //System.out.println("La page est déjà dans une frame");
                // TODO est ce qu'il ne faut pas incrémenter le pin_count de la page ?
                return(frames.get(i).getContent());
            }
        }

        /* Si elle l'est pas, on verifie qu'il reste de la place pour la mettre en memoire si oui c'est bon
         * sinon on applique la politique de remplacement, on l'ajoute en memoire et on la retourne.
         */

        // On regarde si il reste de la place
        int indexPlace = 0;
        boolean isPlein = true;

        for(int i = 0; i<frames.size(); i++){
            if(frames.get(i).getContent() == null){
                indexPlace = i;
                isPlein = false;
            }
        }

        if(!isPlein){ // Si il reste de la place
            //System.out.println("Il reste de la place");
            // On cree une instance de ByteBuffer
            ByteBuffer bf = ByteBuffer.allocateDirect(Constantes.pageSize);

            // On passe notre instance de ByteBuffer par reference pour que Diskmanager nous mette le contenu qu'il faut dedans
            try {
                DiskManager.getInstance().readPage(iPageId, bf);
            } catch (IOException e) {
                e.printStackTrace();
                throw new SGBDException("Erreur d'I/O dans la methode .readPage() de DiskManager");
            }

            // On met le contenu de la page demandee dans la frame
            frames.get(indexPlace).setContent(bf);
            frames.get(indexPlace).setPageId(iPageId);


            return bf;
        } else {
            //System.out.println("Il ne reste pas de place");
            int indexOldestDate = 0; // l'index de la frame dont le pin count est passe a 0 le moins recemment
            Date oldestDate = new Date(); // la date de la frame dont le pin count est passe a 0 le moins recemment

            // On cherche la frame dont le pin count est passe a 0 le moins recemment
            for(int i = 0; i<frames.size(); i++){
                if(frames.get(i).getUnpinned().getTime() < oldestDate.getTime()){
                    indexOldestDate = i;
                    oldestDate = frames.get(i).getUnpinned();
                    // On enregistre les modifications faites a la page associee a la frame qu'on va utiliser pour notre nouvelle page
                    freeFrame(i, frames.get(i).isDirty());
                }
            }

            Frame instanceFrameTraitee = frames.get(indexOldestDate); // Creation d'un pointeur vers la frame a changer pour simplifier le code

            // Installation de la nouvelle page dans la frame
            instanceFrameTraitee.setPageId(iPageId);
            instanceFrameTraitee.setPin_count(1);
            try {
                DiskManager.getInstance().readPage(iPageId, instanceFrameTraitee.getContent());
                return instanceFrameTraitee.getContent();
            } catch (IOException e) {
                e.printStackTrace();
                throw new SGBDException("Erreur d'I/O dans la methode .readPage() de DiskManager");
            }
        }
        //throw new SGBDException("Impossible de trouver ou charger le buffer (BufferManager.getPage())");
    }

    /** Liberer une page
     * @param indexFrame l'index de la frame qui contient la page a virer
     * @param iIsDirty TRUE pour change FALSE pour inchange
     */
    private void freeFrame(int indexFrame, boolean iIsDirty) throws SGBDException {
        if(iIsDirty){
            try {
                DiskManager.getInstance().writePage(frames.get(indexFrame).getPageId(), frames.get(indexFrame).getContent());
            } catch (IOException e) {
                e.printStackTrace();
                throw new SGBDException("Erreur d'I/O dans la methode .writePage() de DiskManager");
            }
        }
        frames.get(indexFrame).setPin_count(0);
        frames.get(indexFrame).setPageId(null);
        frames.get(indexFrame).setContent(ByteBuffer.allocateDirect(Constantes.pageSize));
        frames.get(indexFrame).setDirty(false);
    }

    /** Liberer toutes les pages du tableau de frames
     */
    public void flushAll() throws SGBDException {
        for (int i = 0; i<frames.size(); i++){
            freePage(frames.get(i).getPageId(), frames.get(i).isDirty());
        }
    }

    /** Liberer une page
     * @param pageId l'index de la frame qui contient la page a virer
     * @param iIsDirty TRUE pour change FALSE pour inchange
     */
    public void freePage(PageId pageId, boolean iIsDirty) throws SGBDException {
        for(int i = 0; i<frames.size(); i++){
            if(frames.get(i).getPageId() == null) {
                // si on est sur une frame vide, on passe
            } else if(frames.get(i).getPageId().getPageIdx() == pageId.getPageIdx()) {
                if (frames.get(i).getPin_count() > 0) { // Si pin_count est superieur a 0, on le decremente
                    frames.get(i).setPin_count(frames.get(i).getPin_count() - 1);
                }
                frames.get(i).setDirty(iIsDirty);
            }
        }
    }
}
