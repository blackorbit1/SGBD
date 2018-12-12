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
            this.frames.add(new Frame());
        }
    }

    public static BufferManager getInstance() {
        return instance;
    }

    /** Methode creee pour comprendre ce qu'il se passe et corriger des bugs
     *
     * @return (liste de toutes les frames)
     */
    public ArrayList<Frame> getFrames() {
        return frames;
    }

    // il faut toujours incrementer le pin_count

    /** Retourne un des buffers qui stockent le contenu d’une page dans une des cases
     * @param iPageId l'ID de la page en question
     *
     * @return un des buffer qui stockent le contenu de la page
     */
    public ByteBuffer getPage(PageId iPageId) throws SGBDException {
        // on regarde si la page recherchee n'est pas déjà dans le tableau de frames
        for(Frame frame: frames) {
            if((frame.getPageId() != null) && (frame.getPageId().getPageIdx() == iPageId.getPageIdx()) && (frame.getPageId().getFileIdx() == iPageId.getFileIdx())){
                frame.incrementerPinCount();
                return frame.getContent();
            }
        }

        // si non, on cherche la page dans les fichiers et on la met dans le tableau
        // si il reste de la place
        for(Frame frame: frames){
            if(frame.getPageId() == null){
                frame.setPageId(iPageId);
                frame.incrementerPinCount();
                try {
                    DiskManager.getInstance().readPage(iPageId, frame.getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new SGBDException("erreur d'E/S à la lecture du contenu d'une page");
                }
                return frame.getContent();
            }
        }



        // s'il n'en reste pas, LRU
        int indexOldestDate = -1;
        Date oldestDate = new Date();
        /*
        System.out.println("--- --- ---");
        System.out.println("Demandé:");
        System.out.println("FileID : " + iPageId.getFileIdx());
        System.out.println("PageID : " + iPageId.getPageIdx());
        System.out.println("--- --- ---");
        */
        for(int i = 0; i<frames.size(); i++){
            /*
            System.out.println("FileID : " + frames.get(i).getPageId().getFileIdx());
            System.out.println("PageID : " + frames.get(i).getPageId().getPageIdx());
            System.out.println("pin_count : " + frames.get(i).getPin_count());
            System.out.println("unpinned frame dans le tableau: " + frames.get(i).getUnpinned().getTime());
            System.out.println("oldest date: " + oldestDate.getTime());
            */
            if((frames.get(i).getPin_count() == 0) && ((indexOldestDate == -1) || (frames.get(i).getUnpinned().getTime() < oldestDate.getTime()))){
                indexOldestDate = i;
                oldestDate = frames.get(i).getUnpinned();
            }
            /*
            System.out.println("--- --- ---");
            */
        }

        /*
        int indexOldestDate = -1;
        boolean fin = false;
        while(true) {
            for (int i = 0; i < Constantes.frameCount; i++) {
                if ((this.frames.get(i).getPin_count() == 0) && (this.frames.get(i).getRef_bit() == 1)) {
                    this.frames.get(i).setRef_bit(0);
                    this.frames.get(i).setRef_bit(0);
                    this.frames.get(i).setRef_bit(0);
                    this.frames.get(i).setRef_bit(0);
                    this.frames.get(i).setRef_bit(0);
                    this.frames.get(i).setRef_bit(0);
                    frames.get(i).ref_bit = 0;
                    frames.get(i).ref_bit = 0;
                    frames.get(i).ref_bit = 0;
                    frames.get(i).ref_bit = 0;
                    //System.out.println(this.frames.get(i).getRef_bit());
                } else if ((this.frames.get(i).getPin_count() == 0) && (this.frames.get(i).getRef_bit() == 0)) {
                    indexOldestDate = i;
                    fin = true;
                    break;
                } else {
                    System.out.println("enorme erreur");
                }
            }
            if(fin) break;
            //break;
        }
        */



        if(indexOldestDate >= 0){
            if(frames.get(indexOldestDate).isDirty()){
                try {
                    DiskManager.getInstance().writePage(frames.get(indexOldestDate).getPageId(), frames.get(indexOldestDate).getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new SGBDException("erreur d'E/S à l'ecriture d'une page");
                }
            }

            frames.get(indexOldestDate).resetFrame();
            frames.get(indexOldestDate).setPageId(iPageId);
            frames.get(indexOldestDate).incrementerPinCount();
            try {
                DiskManager.getInstance().readPage(iPageId, frames.get(indexOldestDate).getContent());
            } catch (IOException e) {
                e.printStackTrace();
                throw new SGBDException("erreur d'E/S à la lecture du contenu d'une page");
            }

            return frames.get(indexOldestDate).getContent();

        } else {
            throw new SGBDException("erreur sur l'algo de LRU");
        }
    }


    /** Liberer toutes les pages du tableau de frames
     */
    public void flushAll() throws SGBDException {
        for(Frame frame: frames){
            if(frame.getPageId() != null){
                frame.setPin_count(0);
            }
        }
    }

    /** Liberer une page
     * @param pageId l'index de la frame qui contient la page a virer
     * @param iIsDirty TRUE pour change FALSE pour inchange
     */
    public void freePage(PageId pageId, boolean iIsDirty) throws SGBDException {
        for(Frame frame: frames){
            if((frame.getPageId() != null) && (frame.getPageId().getPageIdx() == pageId.getPageIdx()) && (frame.getPageId().getFileIdx() == pageId.getFileIdx())){
                frame.addDirty(iIsDirty);
                frame.decrementerPinCount();
            }
        }
    }
}
