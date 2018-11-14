package bdda;

import exception.SGBDException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;

public class BufferManager {
    private ArrayList<Frame> frames = new ArrayList<>();

    //Pour avoir une unique instance de BufferManager
    private static final BufferManager instance = new BufferManager();

    /** Constructeur de ctte classe qui crée un tableau de frames
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
            try {
                DiskManager.getInstance().readPage(iPageId, bf);
            } catch (IOException e) {
                e.printStackTrace();
                throw new SGBDException("Erreur d'I/O dans la méthode .readPage() de DiskManager");
            }

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
                    freeFrame(i, frames.get(i).isDirty());
                }
            }

            Frame instanceFrameTraitee = frames.get(indexOldestDate); // Création d'un pointeur vers la frame à changer pour simplifier le code

            // Installation de la nouvelle page dans la frame
            instanceFrameTraitee.setPageId(iPageId);
            instanceFrameTraitee.setPin_count(1);
            try {
                DiskManager.getInstance().readPage(iPageId, instanceFrameTraitee.getContent());
            } catch (IOException e) {
                e.printStackTrace();
                throw new SGBDException("Erreur d'I/O dans la méthode .readPage() de DiskManager");
            }
        } else {
            throw new SGBDException("Le tableau des frames a plus de frames que le maximum autorisé");
        }

        return null;
    }

    /** Libérer une page
     * @param indexFrame l'index de la frame qui contient la page à virer
     * @param iIsDirty TRUE pour changé FALSE pour inchangé
     */
    private void freeFrame(int indexFrame, boolean iIsDirty) throws SGBDException {
        if(iIsDirty){
            try {
                DiskManager.getInstance().writePage(frames.get(indexFrame).getPageId(), frames.get(indexFrame).getContent());
            } catch (IOException e) {
                e.printStackTrace();
                throw new SGBDException("Erreur d'I/O dans la méthode .writePage() de DiskManager");
            }
        }
        frames.get(indexFrame).setPin_count(0);
        frames.get(indexFrame).setPageId(null);
        frames.get(indexFrame).setContent(ByteBuffer.allocateDirect(Constantes.pageSize));
        frames.get(indexFrame).setDirty(false);
    }

    /** Libérer toutes les pages du tableau de frames
     */
    public void flushAll() throws SGBDException {
        for (int i = 0; i<frames.size(); i++){
            freePage(frames.get(i).getPageId(), frames.get(i).isDirty());
        }
    }

    /** Libérer une page
     * @param pageId l'index de la frame qui contient la page à virer
     * @param iIsDirty TRUE pour changé FALSE pour inchangé
     */
    public void freePage(PageId pageId, boolean iIsDirty) throws SGBDException {
        for(int i = 0; i<frames.size(); i++){
            if(frames.get(i).getPageId().getPageIdx() == pageId.getPageIdx()) {
                if (frames.get(i).getPin_count() > 0) { // Si pin_count est supérieur à 0, on le décrémente
                    frames.get(i).setPin_count(frames.get(i).getPin_count() - 1);
                }
                // On déclare la page comme ayant été potentiellement changée
                frames.get(i).setDirty(true);
            }
        }
    }
}
