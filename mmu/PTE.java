package mmu;

public class PTE {
	
	boolean present;
	boolean valid;
	boolean modified;
	int page_num;
	int frame_num;
	int pid;
	
	public PTE(){
		present = true;
		valid = false;
		modified = false;
	}
	
	public void setTranslation(int page_num, int frame_num, int pid){
		this.valid = true;
		this.page_num = page_num;
		this.frame_num = frame_num;
		this.pid = pid;
	}
	
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("pid=" + pid);
		str.append("; present=" + (present ? "1" : "0"));
		str.append("; valid=" + (valid ? "1" : "0"));
		str.append("; modified=" + (modified ? "1" : "0"));
		str.append("; " + Utils.getHex(page_num) + "=>" + Utils.getHex(frame_num));
		return str.toString();
	}
}
