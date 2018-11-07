package bdda;

public class Frame {
    private int pageIdx;
    private boolean dirty;
    private int pin_count;

    public Frame(PageId pageid){
        this.pageIdx = pageid.getPageIdx();
    }

    // Getter

    public int getPageIdx() {
        return pageIdx;
    }

    public boolean isDirty() {
        return dirty;
    }

    public int getPin_count() {
        return pin_count;
    }

    // Setter

    public void setPageIdx(int pageIdx) {
        this.pageIdx = pageIdx;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setPin_count(int pin_count) {
        this.pin_count = pin_count;
    }
}
