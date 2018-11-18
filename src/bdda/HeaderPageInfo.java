package bdda;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class HeaderPageInfo {

	private int dataPageCount;
	private ArrayList<DataPage> listePages;

	public int getDataPageCount() {
		return dataPageCount;
	}

	public void setDataPageCount(int dataPageCount) {
		this.dataPageCount = dataPageCount;
	}

	public ArrayList<DataPage> getListePages() {
		return listePages;
	}

	public void setListePages(ArrayList<DataPage> listePages) {
		this.listePages = listePages;
	}

	public HeaderPageInfo() {
		this.dataPageCount = 0;
		this.listePages = new ArrayList<>();
	}

	public void readFromBuffer(ByteBuffer buffer) {
		buffer.position(0);
		this.dataPageCount = buffer.getInt();
		for (int i = 0; i < this.dataPageCount; i++) {
			int Idx_page_données = buffer.getInt();
			int NbSlotsRestantDisponiblesSurLaPage = buffer.getInt();
			listePages.add(new DataPage(Idx_page_données, NbSlotsRestantDisponiblesSurLaPage));
		}

	}

	public void writeToBuffer(ByteBuffer buffer) {
		buffer.position(0);
		buffer.putInt(dataPageCount);

		for (DataPage d : listePages) {
			buffer.putInt(d.getPageIdx());
			buffer.putInt(d.getFreeSlots());

		}
	}

}
