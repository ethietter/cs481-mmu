package mmu;

import java.util.Iterator;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class SparseArray<T> extends LinkedList<T> {

	private LinkedList<SparseItem> contents;
	private int max_index = -1;
	
	public SparseArray(){
		contents = new LinkedList<SparseItem>();
	}
	
	public boolean add(T item){
		set(max_index + 1, item);
		return true;
	}
	
	public void add(int item_index, T item){
		set(item_index, item);
	}
	
	public T set(int item_index, T item){
		SparseItem new_item = new SparseItem(item_index, item);
		if(item_index > max_index){
			//Index is higher than all other indices - add item to end of list
			contents.add(new_item);
			max_index = item_index;
		}
		else{
			Iterator<SparseItem> it = contents.iterator();
			int it_index = 0;
			while(it.hasNext()){
				SparseItem s = it.next();
				if(s.index == item_index){
					//Replace the existing item, just like a normal array
					contents.set(it_index, new_item);
					break;
				}
				if(s.index > item_index){
					contents.add(it_index, new_item);
					break;
				}
				it_index++;
			}
		}
		return new_item.item;
	}
	
	public T get(int item_index){
		Iterator<SparseItem> it = contents.iterator();
		while(it.hasNext()){
			SparseItem s = it.next();
			if(s.index == item_index){
				return s.item;
			}
			if(s.index > item_index){
				//The item is not in the list, so return null
				return null;
			}
		}
		return null;
	}
	
	public int size(){
		return max_index + 1;
	}
	
	private class SparseItem {
		
		public int index;
		public T item;
		
		public SparseItem(int index, T item){
			this.index = index;
			this.item = item;
		}
	}
}
