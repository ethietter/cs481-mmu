package mmu;

public class PTE {
	
	boolean present;
	boolean valid;
	boolean modified;
	int page_num;
	int frame_num;
	
	public PTE(int page_num, int frame_num){
		present = true;
		valid = true;
		modified = false;
		this.page_num = page_num;
		this.frame_num = frame_num;
	}
	
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("present=" + (present ? "1" : "0"));
		str.append("; valid=" + (valid ? "1" : "0"));
		str.append("; modified=" + (modified ? "1" : "0"));
		str.append("; " + String.format("0x%08X", page_num) + "=>" + String.format("0x%08X", frame_num));
		return str.toString();
	}
}
