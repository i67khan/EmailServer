public class TNode {
    private String id = "";
    private int recordNumber = -1;
    private TNode left   = null;
    private TNode right  = null;
    private TNode parent = null;
    private int height = 0;
    
    public TNode() {
	id    = "";
	recordNumber = -1;
	left   = null;
	right  = null;
	parent = null;
	height = 1;
    }
    
    public TNode(String k, int rn, TNode l, TNode r, TNode p) {
	id    = k;
	recordNumber = rn;
	left   = l;
	right  = r;
	parent = p;
	height = 1;
    }
    
    public String getId() {
	return id;
    }
    
    public int getRecordNumber() {
	return recordNumber;
    }
    
    public TNode getLeft() {
	return left;
    }
    
    public TNode getRight() {
	return right;
    }
    
    public TNode getParent() {
	return parent;
    }
    
    public void setId(String k) {
	id = k;
    }
    
    public void setRecordNumber(int rn) {
	recordNumber = rn;
    }
    
    public void setLeft(TNode l) {
	left = l;
    }
    
    public void setRight(TNode r) {
	right = r;
    }
    
    public void setParent(TNode p) {
	parent = p;
    }
    
    public int getHeight() {
	return height;
    }
    
    public void setHeight(int h) {
	height = h;
    }

    public String toString() {
	if (this == null)
	    return "null";
	else
	    return "Id: " + id + " Record number: " + recordNumber;
    }
}
