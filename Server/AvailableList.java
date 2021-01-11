public class AvailableList {
    private Available head = null;
    private Available tail = null;
    
    public AvailableList() {
        head = null;
        tail = null;
    }
    
    public AvailableList(Available h, Available t) {
        head = h;
        tail = t;
    }
    
    public Available getHead() {
        return head;
    }
    
    public Available getTail() {
        return tail;
    }
    
    public void setHead(Available p) {
        head = p;
    }
    
    public void setTail(Available p) {
        tail = p;
    }
    
    public void buildFromMessagesFile() {
	int recordNumber = 0;
	// rebuild the available list; almost same code as delete records method. they could be simplified
	Record record = new Record();
	for (recordNumber = 0; recordNumber < Globals.totalRecordsInMessagesFile; recordNumber++) {
	    record.readFromMessagesFile(recordNumber);
	    if (record.getData().charAt(0) == Globals.DELETED) {
                addRecord(recordNumber);
	    }
	}
    }
    
    // return the next available spot or EMPTY_AVAILABLE_LIST (-1)
    // remove from linked list
    
    public int getNextRecord() {
        int record = Globals.EMPTY_AVAILABLE_LIST;
        if (head != null) {
            record = head.getRecordNumber();
            
            if (head == tail) 
                tail = null;
            
            head = head.getNext();
        }
        return record;
    }
    
    public void addRecord(int recordNumber) {
        if (head == null) {
            head = new Available(recordNumber);
            tail = head;
        }
        else {
            Available p = new Available(recordNumber);
            tail.setNext(p);
            tail = p;
        }
    } 
    
    public String toString() {
        String s = Globals.STR_NULL;
        for (Available p = head; p != null; p = p.getNext()) {
            s = s + "node: " + p.getRecordNumber() + "\n";
        }
        return s.equals(Globals.STR_NULL) ? "empty" : s;
    }
}
