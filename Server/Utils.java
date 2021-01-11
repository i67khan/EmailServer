import java.util.Calendar;
import java.util.Date;
import java.net.*;

public class Utils {
    
    // implemens a substring with text, starting position, length of characters
    // indices must be within bounds
    public static String mySubstring(String text, int start, int length) {
	return text.substring(start, start + length);
    }

    public static String generateRandomCode(int len) {
	String code = "";
	for (int i = 0; i < len; i++) {
	    code = code + (int) (Math.random() * 10);
	}
	return code;
    }

    public static void delay(int ms) {
	try {
	    Thread.sleep (ms);
	}
	catch (InterruptedException ex) {
	    Thread.currentThread ().interrupt ();
	    if (Globals.DEBUG_ON)
		System.out.println (ex);
	}
    }

    public static boolean isANumber(String s) {
	boolean itIsANumber = true;
	for (int i = 0 ; i < s.length() && itIsANumber; i++)
	    itIsANumber = Character.isDigit(s.charAt (i));
	return (itIsANumber && s.length() > 0);
    }

    /* This method pads text to the left with the paddingItem as many times as
       necessary so that the padding plus the text will have the desiredLength.
       If the text.length is larger than desiredLength then the method returns
       text unchanged. If text.length() is zero, then a padding of desiredLength
       will be returned
    */
    public static String leftPad(String text, int desiredLength, char paddingItem) {
	String padding = new String(Globals.STR_NULL);
	for (int i = 0; i < desiredLength - text.length(); i++)
	    padding = padding + paddingItem;
	return padding + text;
    }
    
    public static String leftZeroPad(String text, int desiredLength) {
	return leftPad(text, desiredLength, Globals.CHAR_ZERO);
    }

    // this method removes all characters c from text
    public static String removeChars(String text, char c) {
	String result = new String(Globals.STR_NULL);
	for (int i = 0; i < text.length(); i++) {
	    if (text.charAt(i) != c)
		result = result + text.charAt(i);
	}
	return result;   
    }

    // this method takes a signed int and converts it into four bytes and returns as a String
    public static String intToBytesStr(int num)  {
	    return "" + (char)(num >> 24) +
			(char)((num & 0xFF0000) >> 16) +
			(char)((num & 0xFF00) >> 8) +
			(char)((num & 0xFF));
    }

    // inverse method of intToBytes; a string of 4 bytes is expected
    public static int bytesStrToInt(String str)  {
	    return ((int) (str.charAt(0)) << 24) |
		   ((int) (str.charAt(1)) << 16) |
		   ((int) (str.charAt(2)) << 8)  |
		   ((int) (str.charAt(3)));
    }
    
    // this method takes a long and converts it into eight bytes and returns as a String
    public static String longToBytesStr(long num)  {
	    return "" + (char)(num >> 56) +
			(char)((num & 0xFF000000000000L) >> 48) +
			(char)((num & 0xFF0000000000L) >> 40) +
			(char)((num & 0xFF00000000L) >> 32) +
			(char)((num & 0xFF000000L) >> 24) +
			(char)((num & 0xFF0000L) >> 16) +
			(char)((num & 0xFF00L) >> 8) +
			(char)((num & 0xFFL));
    }

    // inverse method of longToBytes; a string of 8 bytes is expected
    public static long bytesStrToLong(String str)  {
	    return ((long) (str.charAt(0)) << 56) | 
		   ((long) (str.charAt(1)) << 48) | 
		   ((long) (str.charAt(2)) << 40) | 
		   ((long) (str.charAt(3)) << 32) |
		   ((long) (str.charAt(4)) << 24) |
		   ((long) (str.charAt(5)) << 16) |
		   ((long) (str.charAt(6)) << 8)  |
		   ((long) (str.charAt(7)));
    }

    // convert an array of bytes to a string
    public static String bytesToStr(byte[] buffer) {
	String s = "";
	for (int i = 0; i < buffer.length; i++) {
	    s = s + (char) ((buffer[i] + 256) % 256);   // byte is signed in Java [-128, 127]; we need to shift to [0, 255]; it affects the dateTime
	}
	return s;
    }

    // convert a string to an array of bytes
    public static byte[] strToBytes(String s) {
	byte[] buffer = new byte[s.length()];       
	for (int i = 0; i < s.length(); i++) {
	    buffer[i] = (byte) s.charAt(i);
	}       
	return buffer;
    }

    public static String setReceivingTime(String message) {
	return message.substring(0, Globals.DATE_TIME_POS) + 
	       longToBytesStr(System.currentTimeMillis()) + 
	       message.substring(message.indexOf(Globals.FIRST_RECORD_MARKER));
    }

    public static String setReceiverAccountNumber(String message) {
	String accountNumber = SearchAndSort.getAccountNumberFromAccountName(message.substring(Globals.COMMAND_LEN + Globals.CLIENT_ID_LEN, 
									     Globals.COMMAND_LEN + 2 * Globals.CLIENT_ID_LEN));
	return message.substring(0, Globals.COMMAND_LEN + Globals.CLIENT_ID_LEN) + accountNumber + message.substring(Globals.COMMAND_LEN + 2 * Globals.CLIENT_ID_LEN);
    }
    
    public static void uniqueEntries(int[] list) {
	int upper = list.length * 5; // set range to five times the array size
	int number = -1;
	for (int i = 0; i < list.length; i++) {
	    boolean repeatFound = false;
	    do {
		repeatFound = false;
		number = (int) (Math.random() * upper); // generate a new number
		for (int j = 0; j < i && !repeatFound; j++) { // check if it's in list
		    repeatFound = (number == list[j]);
		}
	    } while (repeatFound); // keep generating numbers until
	    // a unique one is found
	    list[i] = number;
	}
    }
    
    public static String generateCode(int size) {
	String code = "";
	for (int i = 0; i < size; i++) {
	    code = code + (int) (Math.random() * 10);
	}
	return code;
    }

    
    
    
// Only for Grade 11 University class
    public static String myIPAddress() {
	String ipAddress = "";
	try {
	    InetAddress myComputer = InetAddress.getLocalHost();
	    ipAddress = myComputer.getHostAddress();
	}
	catch(Exception e) {
	    System.out.println("***Error obtaining computer address");
	}
	return ipAddress;
    }
    
    public static String myUserName() {
	String userName = "";
	try {
	    userName = System.getProperty("user.name");
	}
	catch(Exception error) {
	    System.out.println("Error obtaining user name.");
	}
	return userName;
    }
    
    // this method puts zeroes in front of text for a total length
    // of 9 characters. If the length of text is more than 9, then
    // the method cuts off at the end of text so that it becomes
    // exactly 9
    
    public static String leftPadding(String text) {
	if (text.length() >= 9) {
	    return text.substring(0, 9);
	}
	else {
	    do {
		text = "0" + text;
	    } while (text.length() != 9);
	    
	    return text;
	}
    }

}

