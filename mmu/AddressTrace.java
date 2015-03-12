package mmu;

public class AddressTrace {
    
    public enum Op{
	R, W, I
    };
    
    public Op op;
    public int pid;
    public long v_address;
    
    public AddressTrace(int pid, char op, long v_address){
    
    	if(op == 'R') this.op = Op.R;
    	if(op == 'W') this.op = Op.W;
    	if(op == 'I') this.op = Op.I;
    	this.pid = pid;
    	this.v_address = v_address;
    }
    
    public String toString(){
    	return "PID=" + pid + " Op=" + op + " Addr=" + Utils.getHex(v_address);
    }
}
