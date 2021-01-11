public class ErrorReport {
    public static void report(int errorNumber) {
        String errorMessage = "";
        switch(errorNumber) {
            case 00 : errorMessage = "Error initializing system";
                      break;
            case 01 : errorMessage = "01 Error opening messages file" + Globals.MESSAGES_FILE;
                      break;
            case 02 : errorMessage = "02 Error opening available list file " + Globals.AVAILABLE_LIST_FILE;
                      break;
            case 03 : errorMessage = "Error retrieving accounts " + Globals.ACCOUNTS_FILE;
                      break;
            case 04 : errorMessage = "Error opening sender index file " + Globals.SENDER_INDEX_FILE;
                      break;
            case 05 : errorMessage = "Error opening receiver index file " + Globals.RECEIVER_INDEX_FILE;
                      break;
            default : errorMessage = "Unknown error";
                      break;
        }
        System.out.println(errorMessage);
    }
}
