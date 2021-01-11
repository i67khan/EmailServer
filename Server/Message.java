/************************************************************************************************/
/* This class takes the starting startRecordNumber and 
/* number links to build an entire message for a
/* particular client
/*
/* Length of messages are always a multiple of Globals.DATA_LEN
/* The message can be trimmed later during transmisison if needed
/* 
/*   Fields:
/*       command for server      (also gets marked in each record when record is deleted)
/*       message sender          (1st part of identifier)
/*       message receiver        (2nd part of identifier)
/*       date and time           (3rd part of identifier) Eight byte long codification - milliseconds)
/*       marker                  (determines if record is the first of the message; needed if indices ever need to be rebuilt)
/*       text                    message text    
/************************************************************************************************/

import java.io.IOException;
import java.util.Date;
public class Message {
    private char command    = Globals.NULL;
    private String sender   = Globals.STR_NULL;
    private String receiver = Globals.STR_NULL;
    private String dateTime = Globals.STR_NULL;
    private char marker     = Globals.NULL;
    private String subject  = Globals.STR_NULL;
    private char eosMarker  = Globals.NULL;
    private String text     = Globals.STR_NULL;
    
    public Message() {
	command    = Globals.NULL;
	sender     = Globals.STR_NULL;
	receiver   = Globals.STR_NULL;
	dateTime   = Globals.STR_NULL;
	marker     = Globals.NULL;
	subject    = Globals.STR_NULL;
	eosMarker  = Globals.NULL;
	text       = Globals.STR_NULL;
    }

    public Message(String s) {
	setMessage(s);
    }
    
    public void setMessage(String s) {
	command    = s.charAt(Globals.COMMAND_POS);
	sender     = s.substring(Globals.SENDER_POS, Globals.RECEIVER_POS);
	receiver   = s.substring(Globals.RECEIVER_POS, Globals.DATE_TIME_POS);
	dateTime   = s.substring(Globals.DATE_TIME_POS, Globals.FIRST_RECORD_MARKER_POS);
	marker     = s.charAt(Globals.FIRST_RECORD_MARKER_POS);
        
        // we need to skip over the stamped date and time when retrieving the subject because the END_OF_SUBJECT_MARKER could be a bit pattern
        // in the 8-byte representation of the date and the position of the END_OF_SUBJECT_MARKER could be calculated incorrectly causing an out of bounds error
        subject    = s.substring(Globals.FIRST_RECORD_MARKER_POS + 1, 
                     Globals.FIRST_RECORD_MARKER_POS + s.substring(Globals.FIRST_RECORD_MARKER_POS + 1).indexOf(Globals.END_OF_SUBJECT_MARKER) + 1);
                
	eosMarker  = Globals.END_OF_SUBJECT_MARKER;

        // we also need to skip over the stamped date and time when retrieving the text for the same reasons as above with the subject
        //text       = s.substring(s.indexOf(Globals.END_OF_SUBJECT_MARKER) + 1);
        
        text       = s.substring(Globals.FIRST_RECORD_MARKER_POS + 1);
        text       = text.substring(text.indexOf(Globals.END_OF_SUBJECT_MARKER) + 1);
    }
    
    public String getMessage() {
	return command + sender + receiver + dateTime + marker + subject + eosMarker + text;
    }
	
    public char getCommand() {
	return command;
    }
    
    public String getSender() {
	return sender;
    }
    
    public String getReceiver() {
	return receiver;
    }
    
    public String getDateTime() {
	return dateTime;
    }
    
    public char getMarker() {
	return marker;
    }
    
    public String getSubject() {
	return subject;
    }
    
    public char getEOSMarker() {
	return eosMarker;
    }
    
    public String getText() {
	return text;
    }
    
    public void setCommand(char c) {
	command = c;
    }
    
    public void setSender(String s) {
	sender = s;
    }
    
    public void setReceiver(String r) {
	receiver = r;
    }
    
    public void setDateTime(String dt) {
	dateTime = dt;
    }
    
    public void setMarker(char m) {
	marker = m;
    }
    
    public void setSubject(String s) {
	subject = s;
    }
    
    public void setEOSMarker(char e) {
	eosMarker = e;
    }
    
    public void setText(String t) {
	text = t;
    }
    
    public String getIdSenderFirst() {
	return sender + receiver + dateTime;
    }

    public String getIdReceiverFirst() {
	return receiver + sender + dateTime;
    }

    // recordNumber is the starting record position for the entire message
    
    public void readFromMessagesFile(int recordNumber) {
	String data = Globals.STR_NULL;
	Record record = new Record();
	
	do {
	    record.readFromMessagesFile(recordNumber);  
	    data = data + record.getData();
	    recordNumber = record.getNext();
	} while (recordNumber != Globals.END_OF_MESSAGE); 
	setMessage(data);
    }
    
    // write the message in various records if necessary
    // returns the record number where the message starts
   
    public int writeToMessagesFile() {
	String s = getMessage();
	int recordNumber      = -1;
	int nextRecordNumber  = -1;
	int startRecordNumber = Globals.availableList.getHead() == null ? 
				Globals.totalRecordsInMessagesFile : 
				Globals.availableList.getHead().getRecordNumber();
	
	Record record = new Record();
	
	while (s.length() > 0) {
	    if (Globals.availableList.getHead() == null) {
		recordNumber = Globals.totalRecordsInMessagesFile;
		if (s.length() <= Globals.RECORD_DATA_LEN) {
		    record.setData(s, Globals.END_OF_MESSAGE);
		    record.writeToMessagesFile(recordNumber, Globals.APPEND);
		    s = "";  // forces out of loop
		}
		else {
		    nextRecordNumber = recordNumber + 1;
		    record.setData(s.substring(0, Globals.RECORD_DATA_LEN), nextRecordNumber);
		    record.writeToMessagesFile(recordNumber, Globals.APPEND);
		    s = s.substring(Globals.RECORD_DATA_LEN);                    
		}
	    }
	    else {
		recordNumber =  Globals.availableList.getNextRecord();
		if (s.length() <= Globals.RECORD_DATA_LEN) {
		    record.setData(s, Globals.END_OF_MESSAGE);
		    record.writeToMessagesFile(recordNumber, Globals.MODIFY);
		    s = "";  // forces out of loop
		}
		else {
		    nextRecordNumber = Globals.availableList.getHead() == null ? 
				       Globals.totalRecordsInMessagesFile : 
				       Globals.availableList.getHead().getRecordNumber();
		    
		    record.setData(s.substring(0, Globals.RECORD_DATA_LEN), nextRecordNumber);
		    record.writeToMessagesFile(recordNumber, Globals.MODIFY);
		    s = s.substring(Globals.RECORD_DATA_LEN);                       
		}
	    }
	}

	return startRecordNumber;
    }  
    
    // recordNumber is the starting record position for the entire message
    
    public void deleteFromMessagesFile(int recordNumber) {       
	Record record = new Record();
	
	while (recordNumber != Globals.END_OF_MESSAGE) {
	    record.deleteFromMessagesFile(recordNumber); 
	    recordNumber = record.getNext();    // information is still in the variable, so this is ok here after deleting                       
	}        
    }
    
    // recordNumber is the starting record position for the entire message
    public void printFromMessagesFile(int recordNumber) {
	readFromMessagesFile(recordNumber);
	System.out.println(this);
    }
    
    public void printAllFromMessagesFile() {
	for (int recordNumber = 0; recordNumber < Globals.totalRecordsInMessagesFile; recordNumber++) {
	    Record r = new Record();
	    r.readFromMessagesFile(recordNumber);
	    if (r.getData().indexOf(Globals.FIRST_RECORD_MARKER) != -1) {
		this.readFromMessagesFile(recordNumber);
		System.out.println("Record: " + recordNumber);
		System.out.println("-----------------------");
		System.out.println(this);
		System.out.println();
	    }
	}  
    }

    public String toString() {
	Date date = new Date(Utils.bytesStrToLong(dateTime));
        
	// account names are displayed
	return "Command     : " + command   + "\n" +
	       "Sender      : " + SearchAndSort.getPersonNameFromAccountNumber(sender)   + "\n" +
	       "Receiver    : " + SearchAndSort.getPersonNameFromAccountNumber(receiver) + "\n" +
	       "Date/Time   : " + date.toString() + "\n" +
	       "Marker      : " + marker    + "\n" +
	       "Subject     : " + subject   + "\n" +
	       "EOS Marker  : " + eosMarker + "\n" +
	       "Message text: " + text;

	// account numbers only - no names
/*
	return "Command     : " + command   + "\n" +
	       "Sender      : " + sender    + "\n" +
	       "Receiver    : " + receiver  + "\n" +
	       "Date/Time   : " + dateTime  + "\n" + // new Date(Utils.bytesStrToLong(dateTime)) + "\n" +
	       "Marker      : " + marker    + "\n" +
	       "Subject     : " + subject   + "\n" +
	       "EOS Marker  : " + eosMarker + "\n" +
	       "Message text: " + text;
*/
    }
}
