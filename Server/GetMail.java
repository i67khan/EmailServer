public class GetMail {
    public static final char IN_BOX = 'I';
    public static final char OUT_BOX = 'O';
    public static final int  END_OF_MESSAGES_TRANSMISSION = -2;
    public static final int  END_OF_MESSAGE = -1;
    public static final int  INT_LEN = 4;
    
    public static final int  DATE_TIME_POS = 19;
    public static final int  FIRST_RECORD_MARKER_POS = 26;
    
    public static void main(String[] args) {
	String request = IN_BOX + 
			 Utils.leftZeroPad(NetIO.myUserName(), Globals.RECEIVER_LEN);
	System.out.println("Client request: " + request);
	System.out.println();
			 
	int errorCode = NetIO.sendRequest(request, Globals.SERVER_IP_ADDRESS);
	if (errorCode == Globals.NET_OK) {
	    String allMessages = NetIO.receiveRequest();
	    
	    System.out.println("Returned from server: " + allMessages);
	    System.out.println("Length of returned from server: " + allMessages.length());
	    System.out.println(); 
	    
	    int i = 0;
	    while (!allMessages.equals(Utils.intToBytesStr(END_OF_MESSAGES_TRANSMISSION))) {
		//System.out.println(allMessages.substring(0, allMessages.indexOf(Utils.intToBytesStr(END_OF_MESSAGE))));
		String oneMessage = allMessages.substring(0, DATE_TIME_POS) + "DATETIME" + 
				    allMessages.substring(DATE_TIME_POS + 8, allMessages.indexOf(Utils.intToBytesStr(END_OF_MESSAGE)));

		System.out.print("Message " + i + ": ");                
		System.out.println(oneMessage);
		System.out.println("Length   : " + oneMessage.length());
		System.out.println();

		allMessages = allMessages.substring(allMessages.indexOf(Utils.intToBytesStr(END_OF_MESSAGE)) + INT_LEN);
		i++;
	    }
	}
	else {
	    System.out.println("Request not received by server");
	}
    }
}
