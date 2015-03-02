package mmu;

public final class Utils {

	public static String getHex(int val){
		return String.format("0x%08X", val);
	}
	
    public static int getPage(int address){
    	return address >> Settings.frame_bits;
    }
}
