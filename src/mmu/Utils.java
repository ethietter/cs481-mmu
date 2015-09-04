package mmu;

public final class Utils {

	public static String getHex(int val){
		return String.format("0x%08X", val);
	}
	
	public static String getHex(long val){
		return String.format("0x%08X", val);
	}
	
    public static int getPage(long address){
    	return (int) (address >>> (Settings.address_size - Settings.frame_bits));
    }
    
    public static int getOffset(long address){
    	return (int) (address & Settings.offset_mask);
    }
}
