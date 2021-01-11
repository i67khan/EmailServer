public class Init {
    public static int initializeSystem() {
	int error = Globals.PROCESS_ERROR;
	
	error = FileIO.openMessagesFile(Globals.MESSAGES_FILE);
	if (error == Globals.PROCESS_OK) {
	    
	    error = FileIO.retrieveAvailableList(Globals.AVAILABLE_LIST_FILE);
	    if (error == Globals.PROCESS_OK) {
		
		Globals.accounts = FileIO.retrieveAccounts(Globals.ACCOUNTS_FILE);
		if (Globals.accounts != null) {
		    
		    // this code for the indices is here temporarily until the retrieval index methods are implemented
		    
		    Globals.senderIndex   = new Tree();
		    Globals.receiverIndex = new Tree();
		    Globals.senderIndex.buildFromMessagesFile(Globals.SENDER_ID);
		    Globals.receiverIndex.buildFromMessagesFile(Globals.RECEIVER_ID);
		    
		    //error = FileIO.retrieveIndex(Globals.SENDER_INDEX_FILE);
		    if (error == Globals.PROCESS_OK) {
			
			//error = FileIO.retrieveIndex(Globals.RECEIVER_INDEX_FILE);
			if (error == Globals.PROCESS_OK) {
			    
			    // system has initialized successfully
			    
			}
			else {
			    ErrorReport.report(5);
			}
		    }
		    else {
			ErrorReport.report(4);
		    }
		}
		else {
		    ErrorReport.report(3);
		}   
	    }
	    else {
		ErrorReport.report(2);
	    }
	}        
	else {
	    ErrorReport.report(1);
	}
	return error;
   }    
}
