import java.io.*;
import java.util.Date;

public class EmailServer {
    public static String previousClientIPAddress = Globals.STR_NULL;    // the three variables are used to see if the current message comes from the same machine as the previous one
    public static long previousArrivalTime = 0;                         // a test so that students do not loop fast messages from the same machine
    public static long currentArrivalTime  = 0;
    
    public static void main(String[] args) {
	int error = Init.initializeSystem();

	if (error == Globals.PROCESS_OK) {
	    Message message  = null;
	    int recordNumber = -1;
	    TNode p = null;
	    TNode q = null;            
	    String identification = Globals.STR_NULL;            
	    char serverCommand = 0;
	    
	    System.out.println("Server: " + NetIO.myIPAddress());
	    do {
		System.out.println("Waiting...");
		String request = NetIO.receiveRequest();
		
		if (!request.equals(Globals.STR_NULL)) {
		    serverCommand = request.charAt(0);
		    switch (serverCommand) {
			case Globals.SEND_MESSAGE :         // entire email expected
			    if (correctSendMessageSyntax(request)) {
				request = Utils.setReceiverAccountNumber(request);  // inserts the student number from the given receiver
				request = Utils.setReceivingTime(request);          // stamps message with server's time

				message = new Message(request);
				recordNumber = message.writeToMessagesFile();

    System.out.println("Request after entering SEND_MESSAGE case: " + request.substring(0, Globals.DATE_TIME_POS) +
								      "DATETIME" +
								      request.substring(Globals.FIRST_RECORD_MARKER_POS));
    System.out.println("LENGTH : " + request.length());
    System.out.println("INDEX @: " + request.indexOf(Globals.END_OF_SUBJECT_MARKER));

    System.out.println("======================================");
    System.out.println("Message from: " + Globals.clientIPAddress);
    message.printFromMessagesFile(recordNumber);
    System.out.println("======================================");

				identification = message.getIdSenderFirst();                            
				p = new TNode(identification, recordNumber, null, null, null);
				Globals.senderIndex.insertNode(p);

				identification = message.getIdReceiverFirst();
				p = new TNode(identification, recordNumber, null, null, null);
				Globals.receiverIndex.insertNode(p);
			    }

			    break;

			case Globals.IN_BOX :            // command + 9 digit identification expected
			    identification = request.substring(Globals.CLIENT_POS);

			    p = Globals.receiverIndex.findNode(identification, 0);
			    if (p != null) {
			       System.out.print("First in tree with this partialKey: ");
			       recordNumber = p.getRecordNumber();
			       System.out.println("Record number: " + recordNumber);
			       message  = new Message();
			       message.printFromMessagesFile(recordNumber);
			    }
			    else
			       System.out.println("***identification not found");

			    q = Globals.receiverIndex.findNode(identification, 1);
			    if (q != null) {
			       System.out.print("Last in tree with this partialKey: ");
			       recordNumber = q.getRecordNumber();
			       System.out.println("Record number: " + recordNumber);
			       message  = new Message();
			       message.printFromMessagesFile(recordNumber);
			    }
			    else
			       System.out.println("***identification not found");

    //System.out.println("============Only printing locally");
    //Globals.receiverIndex.printTree(p, q);
    //System.out.println("============Sending over net");
			    Globals.receiverIndex.prepareTransmissionString(p, q);
			    Globals.transmissionString = Globals.transmissionString + Utils.intToBytesStr(Globals.END_OF_MESSAGES_TRANSMISSION);
			    error = NetIO.sendRequest(Globals.transmissionString, Globals.clientIPAddress);
			    Globals.transmissionString = Globals.STR_NULL;
			    break;

			case Globals.OUT_BOX :            // command + 9 digit identification expected
			    identification = request.substring(Globals.CLIENT_POS);

			    p = Globals.senderIndex.findNode(identification, 0);
			    if (p != null) {
			       System.out.print("First in tree with this partialKey: ");
			       recordNumber = p.getRecordNumber();
			       System.out.println("Record number: " + recordNumber);
			       message  = new Message();
			       message.printFromMessagesFile(recordNumber);
			    }
			    else
			       System.out.println("***identification not found");

			    q = Globals.senderIndex.findNode(identification,  1);
			    if (q != null) {
			       System.out.print("Last in tree with this partialKey: ");
			       recordNumber = q.getRecordNumber();
			       System.out.println("Record number: " + recordNumber);
			       message  = new Message();
			       message.printFromMessagesFile(recordNumber);
			    }
			    else
			       System.out.println("***identification not found");

    //System.out.println("============Only printing locally");
    //Globals.senderIndex.printTree(p, q);
    //System.out.println("============Sending over net");
			    Globals.senderIndex.prepareTransmissionString(p, q);
			    Globals.transmissionString = Globals.transmissionString + Utils.intToBytesStr(Globals.END_OF_MESSAGES_TRANSMISSION);
			    error = NetIO.sendRequest(Globals.transmissionString, Globals.clientIPAddress);
			    Globals.transmissionString = Globals.STR_NULL;
			    break;

			case Globals.DELETE_MESSAGE :
			    // we do not need to setReceiverAccountNumber(request) because the number is picked automatically from user
			    message = new Message(request);

    if (Globals.DEBUG_ON) {
	System.out.println(message);
    }

			    // we use the subject field as a holder to determine who sent the request
			    // this is done in the client end
			    //
			    // if the request comes from sender of message then                 (if delete from OUTBOX)
			    //      find in senderTree
			    //      if there is no matching entry in the receiverTree then      (it means that receiver already deleted message)
			    //          delete message from messages file
			    //      end if
			    // elseif the request comes from the receiver of message then       (if delete from INBOX)           
			    //      find in receiverTree
			    //      if there is no matching entry in the senderTree then        (it means that sender already deleted message)
			    //          delete message from messages file
			    //      end if
			    // end if


if (Globals.DEBUG_ON) {
    System.out.println(message.getSubject());
}

			    if (message.getSubject().equals("" + Globals.SENDER_ID)) {          // OUTBOX
				identification = message.getIdSenderFirst();  
if (Globals.DEBUG_ON) {
    System.out.println(identification);
}
				p = Globals.senderIndex.findNode(identification);
				recordNumber = p.getRecordNumber();
				Globals.senderIndex.deleteNode(p);
				identification = message.getIdReceiverFirst();
				if (Globals.receiverIndex.findNode(identification) == null) {
				    message.deleteFromMessagesFile(recordNumber);
				    System.out.println("Message in " + p.getRecordNumber() + " deleted");
				}
				else {
				    System.out.println("Deleted only the node in sender tree");
				}
			    }
			    else { // INBOX
				identification = message.getIdReceiverFirst();
				p = Globals.receiverIndex.findNode(identification);
				recordNumber = p.getRecordNumber();
				Globals.receiverIndex.deleteNode(p);
				identification = message.getIdSenderFirst();
				if (Globals.senderIndex.findNode(identification) == null) {
				    message.deleteFromMessagesFile(recordNumber);
				    System.out.println("Message in " + p.getRecordNumber() + " deleted");
				}
				else {
				    System.out.println("Deleted only the node in receiver tree");
				}
			    }

			    break;

			case Globals.SERVER_SHUTDOWN :
			    System.out.println("Closing files...server shutdown");
			    break;

			default :
			    System.out.println("Unknown request: " + request);
			    break;
		    }
		}
		else {
		    System.out.println("Null request from " + Globals.clientIPAddress);
		}
	    } while (serverCommand != Globals.SERVER_SHUTDOWN);     

	    error = FileIO.saveAvailableList(Globals.AVAILABLE_LIST_FILE);
	    error = FileIO.saveIndex(Globals.SENDER_INDEX_FILE);
	    error = FileIO.saveIndex(Globals.RECEIVER_INDEX_FILE);
	    error = FileIO.closeMessagesFile();
	}
	else {
	    ErrorReport.report(0);
	}
	System.out.println("End of program.");
    }

    private static void printRequestSyntaxError(String request, String message) {
	String senderId   = request.substring(Globals.COMMAND_LEN, Globals.COMMAND_LEN + Globals.CLIENT_ID_LEN);
	String senderName = SearchAndSort.getPersonNameFromAccountNumber(senderId);
	String receiverName = request.substring(Globals.COMMAND_LEN + Globals.CLIENT_ID_LEN, 
						Globals.COMMAND_LEN + 2 * Globals.CLIENT_ID_LEN); 
	
	System.out.println("****************** Incorrect Request From Client ******************");        
	System.out.print  ("Sender Account Name   : " + senderName);
	System.out.println(senderName.equals(Globals.UNLISTED) ? ", Account Id: " + senderId : Globals.STR_NULL);
	System.out.println("Sender IP Address     : " + (Globals.clientIPAddress.equals(Globals.STR_NULL) ? Globals.UNKNOWN_IP_ADDRESS : Globals.clientIPAddress));
	System.out.println("Receiver Account Name : " + receiverName);
	System.out.println("Error                 : " + message);
	System.out.println("Action                : Request ignored by server");
	System.out.println("*******************************************************************");
    }
   
    public static boolean correctSendMessageSyntax(String request) {
	boolean result = false;        
	currentArrivalTime = System.currentTimeMillis();
	if (request.length() >= Globals.COMMAND_LEN + 
				Globals.SENDER_LEN +
				Globals.RECEIVER_LEN +
				Globals.DATE_TIME_LEN + 
				Globals.FIRST_RECORD_MARKER_LEN +
				Globals.END_OF_SUBJECT_MARKER_LEN) {
	    
	    if (request.charAt(0) == Globals.SEND_MESSAGE ||
		request.charAt(0) == Globals.IN_BOX  ||
		request.charAt(0) == Globals.OUT_BOX ||
		request.charAt(0) == Globals.DELETE_MESSAGE ||
		request.charAt(0) == Globals.SERVER_SHUTDOWN) {
	    
		if (request.indexOf(Globals.FIRST_RECORD_MARKER) == Globals.FIRST_RECORD_MARKER_POS) {
		    if (request.indexOf(Globals.END_OF_SUBJECT_MARKER) > Globals.FIRST_RECORD_MARKER_POS) {
			String senderAccountNumber = request.substring(Globals.COMMAND_LEN, Globals.COMMAND_LEN + Globals.CLIENT_ID_LEN);
			if (SearchAndSort.accountNumberIsListed(senderAccountNumber)) {
			    String receiverAccountName = request.substring(Globals.COMMAND_LEN + Globals.CLIENT_ID_LEN, 
									   Globals.COMMAND_LEN + 2 * Globals.CLIENT_ID_LEN);
			    if (SearchAndSort.accountNameIsListed(receiverAccountName)) {
				if (previousClientIPAddress.equals(Globals.clientIPAddress)) {
				    if (currentArrivalTime - previousArrivalTime < Globals.MINIMUM_TIME_BETWEEN_REQUESTS_OF_SAME_MACHINE) {                                       
					printRequestSyntaxError(request, "Sender looping messages. Wait " + (Globals.MINIMUM_TIME_BETWEEN_REQUESTS_OF_SAME_MACHINE / 1000) + " seconds.");
				    }
				    else {
					result = true;
				    }
				}
				else {
				    result = true;
				}
			    }
			    else {
				printRequestSyntaxError(request, "Receiver account name not listed");
			    }
			}
			else {
			    printRequestSyntaxError(request, "Sender account number not listed");
			}
		    }
		    else {
			printRequestSyntaxError(request, "End of subject marker missing or in the wrong position");
		    }
		}
		else {
		    printRequestSyntaxError(request, "Message first record marker missing");
		}
	    }
	    else {
		printRequestSyntaxError(request, "Unknown server command");
	    }
	    
	}
	else {
	    printRequestSyntaxError(request, "Incorrect request length");
	}
	
	previousArrivalTime = currentArrivalTime;
	previousClientIPAddress = Globals.clientIPAddress;
	    
	return result;
    }
}

/*
import java.util.Scanner;
import java.io.*;
import java.util.Date;
import hsa.*;

public class EmailServer {
    public static String previousClientIPAddress = Globals.STR_NULL;    // the three variables are used to see if the current message comes from the same machine as the previous one
    public static long previousArrivalTime = 0;                         // a test so that students do not loop fast messages from the same machine
    public static long currentArrivalTime  = 0;
    
    public static void main(String[] args) {
	int error = Init.initializeSystem();
	Scanner in = new Scanner(System.in);
      
	if (error == Globals.PROCESS_OK) {
	    Message message  = new Message();
	    String sender    = "";
	    String receiver  = "";
	    String timeStamp = "";
	    String identification = "";
	    String subject   = "";
	    String text      = "";
	    String entireMessage = "";
	    int recordNumber = 0;
	
	    Tree senderIndex   = new Tree();
	    Tree receiverIndex = new Tree();
	    TNode p = null;
	
	    // this code is here temporarily until we can retrieve these trees
	    senderIndex.buildFromMessagesFile(Globals.SENDER_ID);
	    receiverIndex.buildFromMessagesFile(Globals.RECEIVER_ID);

	    // retrieve index with key sender
	    // retrieve index with key receiver
	    
	    Globals.accounts = FileIO.retrieveAccounts(Globals.ACCOUNTS_FILE);
	    
	    // retrieve the availabe list
	    error = FileIO.retrieveAvailableList(Globals.AVAILABLE_LIST_FILE);
	    if (error == Globals.PROCESS_OK) {
		System.out.println("Total records at start: " +  Globals.totalRecordsInMessageFile);

		recordNumber = 0;               
		do {
		    System.out.println("Available: ");
		    System.out.println(Globals.availableList);

		    
		    System.out.println("Options: ");
		    System.out.println(" 1. add");
		    System.out.println(" 2. delete");
		    System.out.println(" 3. print all messages");
		    System.out.println(" 4. find message from full id sender + receiver + dateTime");
		    System.out.println(" 5. find message from full id receiver + sender + dateTime");
		    System.out.println(" 6. find messages from partial id (sender index)");
		    System.out.println(" 7. rebuild available list");
		    System.out.println(" 8. rebuild indices");
		    System.out.println("50. add lots of messages for testing");
		    System.out.println("51. print breadth first sender and receiver trees");
		    System.out.print  ("99. quit: ");
		    System.out.println();
		    System.out.print  ("Option -> ");
		    String c  = in.nextLine();
		    command = Integer.parseInt(c);

		    switch(command) {
			case 1 : System.out.print("Sender id (9 byte account number): ");
				 sender = in.nextLine();

				 System.out.print("Receiver id (9 byte account number): ");
				 receiver = in.nextLine();

				 
				 System.out.print("Date/Time (8 bytes): ");
				 timeStamp = in.nextLine();
				 
				 //timeStamp = Utils.longToBytesStr(System.currentTimeMillis());

				 System.out.print("Subject: ");
				 subject = in.nextLine();

				 System.out.print("Message: ");
				 text = in.nextLine(); 

				 //String identification = sender + receiver + Utils.longToBytesStr(System.currentTimeMillis());
				 // then also one with receiver + sender + ...

				 identification = sender + receiver + timeStamp;
				 entireMessage = 'C' + 
						 identification +
						 Globals.FIRST_RECORD_OF_MESSAGE + 
						 subject + 
						 Globals.END_OF_SUBJECT_MARKER + 
						 text;

				 message.setMessage(entireMessage);
				 recordNumber = message.writeToMessagesFile();
				 message.printFromMessagesFile(recordNumber);

				 p = new TNode(identification, recordNumber, null, null, null);
				 senderIndex.insertNode(p);

				 identification = receiver + sender + timeStamp;
				 p = new TNode(identification, recordNumber, null, null, null);
				 receiverIndex.insertNode(p);

				 break;

			case 2 : System.out.print("Message identification (sender first): ");
				 identification = in.nextLine();

				 p = senderIndex.findNode(identification);
				 if (p != null) {
				     recordNumber = p.getRecordNumber();
				     message.deleteFromMessagesFile(recordNumber);
				     senderIndex.deleteNode(p);
				     
				     // May 5 2017: Deleting not working with this index
				     
				     //receiverIndex.deleteNode(p);
				     System.out.println("Message deleted. Id: " + identification + " at record number " + recordNumber);
				 }
				 else {
				     System.out.println("identification not found");
				 }
				 break;

			case 3 : message.printAllFromMessagesFile();
				 break;

			case 4 : System.out.print("Sender message identification: ");
				 identification = in.nextLine();

				 p = senderIndex.findNode(identification);
				 if (p != null) {
				    recordNumber = p.getRecordNumber();
				    System.out.println("Record number: " + recordNumber);
				    message.printFromMessagesFile(recordNumber);
				 }
				 else
				    System.out.println("***identification not found");
				 break;

			case 5 : System.out.print("Receiver message identification: ");
				 identification = in.nextLine();

				 p = receiverIndex.findNode(identification);
				 if (p != null) {
				    recordNumber = p.getRecordNumber();
				    System.out.println("Record number: " + recordNumber);
				    message.printFromMessagesFile(recordNumber);
				 }
				 else
				    System.out.println("***identification not found");
				 break;

			case 6 : System.out.print("Partial message identification: (sender)");
				 identification = in.nextLine();

				 p = senderIndex.findNode(identification, 0);
				 if (p != null) {
				    System.out.print("First in tree with this partialKey: ");
				    recordNumber = p.getRecordNumber();
				    System.out.println("Record number: " + recordNumber);
				    message.printFromMessagesFile(recordNumber);
				 }
				 else
				    System.out.println("***identification not found");
				 
				 TNode q = senderIndex.findNode(identification,  1);
				 if (q != null) {
				    System.out.print("Last in tree with this partialKey: ");
				    recordNumber = q.getRecordNumber();
				    System.out.println("Record number: " + recordNumber);
				    message.printFromMessagesFile(recordNumber);
				 }
				 else
				    System.out.println("***identification not found");
				 
				 senderIndex.printTree(p, q);
				 
				 
				 break;

			case 7 : // write statement to flush the file here
				 Globals.availableList = new AvailableList();
				 Globals.availableList.buildFromMessagesFile();
				 break;
			    
			case 8 : senderIndex   = new Tree();
				 receiverIndex = new Tree();
	
				 senderIndex.buildFromMessagesFile(Globals.SENDER_ID);
				 receiverIndex.buildFromMessagesFile(Globals.RECEIVER_ID);
				 break;

			case 50: // add lots of messages
				 for (int messageNumber = 0; messageNumber < 500; messageNumber++) {
				    // made up of 8 zeroes and the last digit of messageNumber
				    //sender = "" + messageNumber;
				    //sender = sender.substring(sender.length() - 1);
				    //sender = Utils.leftPad(sender, 9, '0');
				    System.out.println("Message number: " + messageNumber);
				    
				    switch (messageNumber % 3) {
					case 0 : sender = "333333333";
						 break;
					case 1 : sender = "000000000";
						 break;
					case 2 : sender = "111111111";
						 break;
				    }
				    
				    //receiver = "" + (messageNumber + 1);
				    //receiver = receiver.substring(receiver.length() - 1);
				    //receiver = Utils.leftPad(receiver, 9, '0');
				    
				    switch (messageNumber % 3) {
					case 0 : receiver = "000000000";
						 break;
					case 1 : receiver = "111111111";
						 break;
					case 2 : receiver = "222222222";
						 break;
				    }
				    
				    //Utils.delay(10);
				    //timeStamp = Utils.longToBytesStr(System.currentTimeMillis());
				    timeStamp = Utils.leftPad("" + messageNumber, 8, '0');
				    identification = sender + receiver + timeStamp;
						     
				    subject = "subject " + messageNumber;
				    text    = "Hello how are you today? This is a longer test so that it will take more than one record. Message:" + messageNumber;
				    
				    entireMessage = 'C' + 
						    identification +
						    Globals.FIRST_RECORD_OF_MESSAGE + 
						    subject + 
						    Globals.END_OF_SUBJECT_MARKER + 
						    text;
				    
				    message.setMessage(entireMessage);
				    recordNumber = message.writeToMessagesFile();

				    p = new TNode(identification, recordNumber, null, null, null);
				    senderIndex.insertNode(p);
				    identification = receiver + sender + timeStamp;
				    p = new TNode(identification, recordNumber, null, null, null);
				    receiverIndex.insertNode(p);
				 }
				 break;

			case 51: System.out.println("Sender Index");
				 System.out.println("------------");
				 senderIndex.breadthFirstPrint(); 
				 System.out.println("Receiver Index");
				 System.out.println("--------------");
				 receiverIndex.breadthFirstPrint(); 
				 break;   

		    }
		} while (command != 99);

		error = FileIO.saveAvailableList(Globals.AVAILABLE_LIST_FILE);
	    }
	    else {
		System.out.println("Error opening available list file");
	    }

	    error = FileIO.closeMessagesFile();
	    
	    //if (Globals.senderIndexRoot != null) Globals.senderIndexRoot.save(Globals.SENDER_TREE_FILE);
	    //if (Globals.recipientIndexRoot != null) Globals.recipientIndexRoot.save(Globals.RECIPIENT_TREE_FILE);
	}
	else {
	    System.out.println("Error opening messages file Messages.bci");
	}

    }
}
*/

