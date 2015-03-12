package mmu;


class RawLinkedList {
	
	public Node head;
	public Node tail;
	private int _size;
	
	public RawLinkedList(){
		
	}
	
	//Adds a node to the end of the list
	public Node addNode(int val){
		if(_size == 0){
			head = new Node(val, this);
			tail = head;
			_size++;
			return head;
		}
		else{
			Node new_node = new Node(val, this);
			new_node.prev = tail;
			tail.next = new_node;
			tail = new_node;
			_size++;
			return new_node;
		}
	}
	
	public void deleteHead(){
		if(head == null) return;
		_size--;
		head = head.next;
		head.prev = null;
	}
	
	public int size(){
		return _size;
	}
	
	public String toString(){
		Node node = head;
		StringBuilder str = new StringBuilder("[");
		while(node != null){
			if(node != head){
				str.append(", ");
			}
			str.append(node.val);
			node = node.next;
		}
		str.append("]");
		return str.toString();
	}
	
	class Node {
		private Node next;
		private Node prev;
		private RawLinkedList parent;
		public int val;
		
		public Node(int val, RawLinkedList parent){
			this.val = val;
			this.parent = parent;
		}
		
		public Node(){ }
		
		public void moveToEnd(){
			if(this == parent.tail){
				//Already at the end of the list
				return;
			}
			//"this.next.prev" is NOT the same as "this" (they refer to the same node, but they are different "pointers")
			this.next.prev = this.prev;
			
			if(this != parent.head){ //Not at the front of the list
				this.prev.next = this.next;
			}
			else{ //At the front of the list, so update parent.head
				parent.head = this.next;
				this.next.prev = null;
			}
			
			//Move this node to the end; update references
			parent.tail.next = this;
			this.next = null;
			this.prev = parent.tail;
			parent.tail = this;
		}
		
		public String toString(){
			Integer prev_val = null;
			Integer next_val = null;
			if(prev != null) prev_val = prev.val;
			if(next != null) next_val = next.val;
			return "This=" + this.val + " Prev=" + prev_val + " Next=" + next_val;
		}
	}
}

