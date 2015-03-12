package mmu;

import java.util.HashMap;



public class LRUStruct {


	private RawLinkedList lru_list = new RawLinkedList();
	
	//Used to lookup nodes, given frame indices
	private HashMap<Integer, RawLinkedList.Node> frame_map = new HashMap<Integer, RawLinkedList.Node>();
	
	public LRUStruct(){
	}
	
	//Returns the LRU node
	public RawLinkedList.Node getLRUNode(){
		return lru_list.head;
	}
	
	//Deletes the LRU node and returns it
	public RawLinkedList.Node removeLRUNode(){
		RawLinkedList.Node node = lru_list.head;
		lru_list.deleteHead();
		return node;
	}
	
	public void useNode(int frame_index){
		System.out.println(frame_map.get(frame_index));
		frame_map.get(frame_index).moveToEnd();
	}
	
	//Returns a reference to the newly created node
	public void addNode(int val){
		frame_map.put(val, lru_list.addNode(val));
	}
	
	public String toString(){
		return lru_list.toString();
	}
	
	
}
