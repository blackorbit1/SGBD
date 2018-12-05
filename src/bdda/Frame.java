package bdda;

import java.nio.ByteBuffer;
import java.util.Date;

public class Frame {
    private PageId pageId;
    private boolean dirty;
    private int pin_count;
    private Date unpinned; // la date à laquelle pin_count est passé à 0 (LRU)
    private ByteBuffer content; // contenu de la frame (du cadre qui contient la page)

    public Frame(PageId pageid){
        this.pageId = pageid;
        this.dirty = false;
        this.unpinned = new Date();
        this.pin_count = 1; // Car si on vient de la creer, y a forcément 1 personne qui travaille dessus
    }

    public void incrementerPinCount(){
        pin_count++;
    }

    // Getter

    public PageId getPageId() {
        return pageId;
    }

    public boolean isDirty() {
        return dirty;
    }

    public int getPin_count() {
        return pin_count;
    }

    public ByteBuffer getContent() { return content; }

    public Date getUnpinned() { return unpinned; }

    // Setter

    public void setPageId(PageId pageId) {
        this.pageId = pageId;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setPin_count(int pin_count) {
        this.pin_count = pin_count;
        if(pin_count == 0){
            unpinned = new Date();
        }
    }

    public void setContent(ByteBuffer content) { this.content = content; }
}
