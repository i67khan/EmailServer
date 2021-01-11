import java.io.RandomAccessFile;
public class Globals {
    // debug constants
    public static final boolean DEBUG_ON = true;
    public static final boolean DEBUG_TREE = false;
    
    // debugging tree variables
    public static int case1a = 0;
    public static int case1b = 0;
    public static int case2a = 0;
    public static int case2b = 0;
    public static int case3a = 0;
    public static int case3b = 0;
    public static int case4a = 0;
    public static int case4b = 0;
    public static int case5aa = 0;
    public static int case5ab = 0;
    public static int case5ba = 0;
    public static int case5bb = 0;
    public static int case5ca = 0;
    public static int case5cb = 0;

    // initialization constants
    public static final int NULL = 0;
    public static final String STR_NULL = "";
    public static final char BLANK = ' ';
    public static final char CR = '\n';
    public static final char CHAR_ZERO = '0';
    public static final int  INT_LEN = 4;

    // error constants
    public static final int PROCESS_OK = 0;
    public static final int PROCESS_ERROR = -1;
    
    // position constants for message
    // Structure of message:
    // command + sender + receiver + timeStamp + first record marker + subject + end of subject marker + message text
       
    public static final int COMMAND_POS = 0;
    public static final int COMMAND_LEN = 1;
    
    public static final int CLIENT_POS = COMMAND_POS + COMMAND_LEN;   // these two are here separately because sometimes the client's request contains     
    public static final int CLIENT_ID_LEN = 9;                          // only C + sender or C + receiver, in which case the position of sender and receiver are equal
    
    public static final int SENDER_POS  = COMMAND_POS + COMMAND_LEN;
    public static final int SENDER_LEN  = CLIENT_ID_LEN;
    public static final int RECEIVER_POS = SENDER_POS + SENDER_LEN;
    public static final int RECEIVER_LEN  = CLIENT_ID_LEN;
    public static final int DATE_TIME_POS = RECEIVER_POS + RECEIVER_LEN;
    public static final int DATE_TIME_LEN = 8;  // long current milliseconds coded as eight bytes
    public static final int FIRST_RECORD_MARKER_POS = DATE_TIME_POS + DATE_TIME_LEN;
    public static final int FIRST_RECORD_MARKER_LEN = 1;
    public static final int END_OF_SUBJECT_MARKER_LEN = 1;
    public static final int IDENTIFICATION_LEN = SENDER_LEN + RECEIVER_LEN + DATE_TIME_LEN;
						 
    // constants for records; note that the first record of a message will contain the
    // identification and marker; therefore, there will be more room for text in the
    // subsequent records
    
    public static final int TEXT_LEN = END_OF_SUBJECT_MARKER_LEN + 51;  // for a total of 80 bytes for RECORD_DATA_LEN
    public static final int RECORD_DATA_LEN = COMMAND_LEN   + 
					      SENDER_LEN    +
					      RECEIVER_LEN  +
					      DATE_TIME_LEN +
					      FIRST_RECORD_MARKER_LEN    +
					      TEXT_LEN;
    
    public static final int NEXT_RECORD_LEN = 4;  // integer that stores pointer to next record
    public static final int RECORD_LEN = RECORD_DATA_LEN + NEXT_RECORD_LEN;  // integer at end: 40 bytes
    public static final int END_OF_MESSAGE = -1;                             // marks end of list of records that make up a message
    
    // message and record delimiters; characters used so that special ascii like 1, 2, 3, can be used    
    public static final char FIRST_RECORD_MARKER = '+';       // we mark the start of a message with this marker in case we have to rebuild the indices
    public static final char END_OF_SUBJECT_MARKER = '@';
    public static final char DELETED = '*';                // this character will only be placed at front of every record in case
							   // we need to rebuild the deleted linked list 
    
    // constants for linked list of available records
    public static final int EMPTY_AVAILABLE_LIST = -1; // no records deleted.

    public static final int APPEND = 1;  // modes for writing into the file
    public static final int MODIFY = 2; 
    
    // messages file
    public static final String MESSAGES_FILE = "_messages.dat";
    
    // available list file
    public static final String AVAILABLE_LIST_FILE = "_available.dat";
    
    // accounts file
    public static final String ACCOUNTS_FILE = "_accounts.dat";
    public static final int NOT_FOUND = -1;
    
    // binary tree file
    public static final String SENDER_INDEX_FILE = "_stree.dat";    // tree by sender
    public static final String RECEIVER_INDEX_FILE = "_rtree.dat";  // tree by receiver
    
    public static final int KEY_LEN = SENDER_LEN + RECEIVER_LEN + DATE_TIME_LEN;
    
    public static final int SENDER_ID   = 0;
    public static final int RECEIVER_ID = 1;
    
    // Constants for client
    public static final int MAX_CLIENT_MESSAGES = 500;
    public static final String EMPTY_CLIENT_MESSAGE = STR_NULL; 
    public static final int NO_ITEM_SELECTED = -1;
    
    // Global variables for client
    public static String[] boxMessages = new String[MAX_CLIENT_MESSAGES];
    public static String clientIPAddress = STR_NULL;        // server needs this to know who sent request
    
    // Server command constants
    public static final char SEND_MESSAGE    = 'S';            // client has sent a message
    public static final char DELETE_MESSAGE  = 'D';            // client wants to delete a message
    public static final char IN_BOX          = 'I';            // client wants to retrieve their own mail
    public static final char OUT_BOX         = 'O';            // client wants to retrieve mail they have sent
    public static final char SERVER_SHUTDOWN = 'Q';
    
    // Network constants
    public static final String SERVER_IP_ADDRESS = "192.168.137.1";
    //public static final String SERVER_IP_ADDRESS = "192.168.1.106";
    public static final int PORT_NUMBER = 5000;
    public static final int TIME_OUT    = 10000;   // send request with a 10000 millisecond timeout
    
    public static final int NET_OK            = 0;
    public static final int NET_SEND_ERROR    = -1;
    public static final int NET_RECEIVE_ERROR = -2;  
    
    public static final int SENDING_ATTEMPTS_LIMIT = 500;
    public static final int END_OF_MESSAGES_TRANSMISSION = -2;
    
    // to avoid requests from same machine arriving fast; this would only happen if a client is put on a loop
    public static final int MINIMUM_TIME_BETWEEN_REQUESTS_OF_SAME_MACHINE = 5000; // ms between two messages from same IP address
    
    // Other constants
    public static final String UNLISTED = "_unlisted";
    public static final String UNKNOWN_IP_ADDRESS = "Unknown IP address";

    // global variables
    public static RandomAccessFile msg = null;       // main messages file
    public static AvailableList availableList = null; // start of available of records linked list

    public static Tree senderIndex = null;
    public static Tree receiverIndex = null;
    public static int totalRecordsInMessagesFile = 0; // update every time a record is added; fileLen does not update fast enough  

    // accounts file
    public static String[] accounts = null;
       
    // global network variables
    public static String transmissionString = ""; // concatenates all messages into a single string. then it's decomposed in the client
}

