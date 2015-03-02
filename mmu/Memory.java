package mmu;

import java.util.LinkedList;

public class Memory {

	private static LinkedList<Frame> pages = new LinkedList<Frame>();
	private static int next_page = 0;
	private static int num_frames;
	
	public static void init(){
		num_frames = Settings.physical_size/Settings.frame_size;
	}
	
	public static int allocateFrame(){
		int page_index = next_page;
		if(next_page < num_frames){
			next_page++;
		}
		else{
			page_index = evictFrame();
		}
		return page_index;
	}
	
	private static int evictFrame(){
		return 0;
	}
	 
}
