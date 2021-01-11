import java.util.Date;

public class MailTransfers {
    public static int eMailClientRequestAllMail(int whatId) {
	int error = Globals.NET_RECEIVE_ERROR;
	String identification = NetIO.myUserName();

	identification = identification.length() == 0 ? "" : Utils.leftPad(identification, Globals.CLIENT_ID_LEN, '0');
	do {
	    char command = whatId == Globals.RECEIVER_ID ? Globals.IN_BOX: Globals.OUT_BOX;
	    error = NetIO.sendRequest(Globals.STR_NULL + command + identification, Globals.SERVER_IP_ADDRESS);
	    //System.out.println("hello");           
	    if (error == Globals.NET_OK) {
		String allMessages = NetIO.receiveRequest();

		int i = 0;
		while (!allMessages.equals(Utils.intToBytesStr(Globals.END_OF_MESSAGES_TRANSMISSION)) && i < Globals.MAX_CLIENT_MESSAGES) {
		    Globals.boxMessages[i] = allMessages.substring(0, allMessages.indexOf(Utils.intToBytesStr(Globals.END_OF_MESSAGE)));
		    allMessages = allMessages.substring(allMessages.indexOf(Utils.intToBytesStr(Globals.END_OF_MESSAGE)) + Globals.INT_LEN);
		    i++;
		}
	    }
	} while (error != Globals.NET_OK);      // consider a timeout condition here
	return error;
    }
    
    public static void main(String[] args) {
	// This is the code for Friday May 25, 2017 ICS3U
	
	for (int i = 0; i < Globals.boxMessages.length; i++) {
	    Globals.boxMessages[i] = Globals.EMPTY_CLIENT_MESSAGE;
	}
	
	eMailClientRequestAllMail(Globals.SENDER_ID);
	
	int i = 0;
	while (!Globals.boxMessages[i].equals(Globals.EMPTY_CLIENT_MESSAGE)) {
	    Date date = new Date(Utils.bytesStrToLong(Globals.boxMessages[i].substring(Globals.DATE_TIME_POS, Globals.FIRST_RECORD_MARKER_POS)));
	    System.out.println(Globals.boxMessages[i].substring(0, 19) +
			       date.toString() +
			       Globals.boxMessages[i].substring(27));
	    i++;
	}        
    }
}











