package bdda;

public class PageId {
	private int FileIdx;
	private int PageIdx;

	public PageId(int FileId, int PageId) {
		this.FileIdx = FileId;
		this.PageIdx = PageId;
	}

	public PageId() {
		this.FileIdx = 0;
		this.PageIdx = 0;
	}

	public int getFileIdx() {
		return FileIdx;
	}

	public void setFileIdx(int fileIdx) {
		FileIdx = fileIdx;
	}

	public int getPageIdx() {
		return PageIdx;
	}

	public void setPageIdx(int pageIdx) {
		PageIdx = pageIdx;
	}
}
