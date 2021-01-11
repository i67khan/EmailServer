public class SearchAndSort {
    // accountNumber is the user's TDSB 9 digit login identification
    // accountName is the chosen user's name
    
    // S E A R C H I N G
    
    public static int binarySearch (String[] list, String key) {
        int bottom = 0;
        int top = list.length - 1;
        int index = (bottom + top) / 2;
        while (!list[index].equals(key) && bottom <= top) {
            if (key.compareTo(list[index]) > 0)
                bottom = index + 1;
            else
                top = index - 1;
            index = (bottom + top) / 2;
        }
        return (bottom > top) ? Globals.NOT_FOUND : index;
    }    
    
    public static String getPersonNameFromAccountNumber(String accountNumber) {
        boolean found = false;
        int i = 0;
        for (i = 0; i < Globals.accounts.length && !found; i++) {
            found = accountNumber.equals(Globals.accounts[i].substring(Globals.CLIENT_ID_LEN, 
                                                                       Globals.CLIENT_ID_LEN + Globals.CLIENT_ID_LEN));
        }
        return found ? Globals.accounts[i - 1].substring(Globals.CLIENT_ID_LEN + 
                                                         Globals.CLIENT_ID_LEN) : Globals.UNLISTED;
    }
    
    public static String getAccountNumberFromAccountName(String accountName) {
        boolean found = false;
        int i = 0;
        for (i = 0; i < Globals.accounts.length && !found; i++) {
            found = accountName.equals(Globals.accounts[i].substring(0, Globals.CLIENT_ID_LEN));
        }  
        return found ? Globals.accounts[i - 1].substring(Globals.CLIENT_ID_LEN, 
                                                         Globals.CLIENT_ID_LEN + Globals.CLIENT_ID_LEN) : Globals.UNLISTED;
    }
    
    public static boolean accountNumberIsListed(String accountNumber) {
        return !getPersonNameFromAccountNumber(accountNumber).equals(Globals.UNLISTED);
    }
    
    public static boolean accountNameIsListed(String accountName) {
        return !getAccountNumberFromAccountName(accountName).equals(Globals.UNLISTED);
    }

    // S O R T I N G
    
    public static void swapItems(String[] list, int i, int j) {
        if (i != j) {
            String temp = list[i];
            list[i] = list[j];
            list[j] = temp;
        }
    }
  
    public static int indexOfLargest(String[] list,  int end) {
        int index = 0;
        String largest = list[0];
        for (int i = 0; i <= end; i++) {
            if(list[i].compareTo(largest) > 0) {
                largest = list[i];
                index = i;
            }
        }
        return index; 
    }
  
    public static void selectionSort(String[] list) {
        for (int i = list.length - 1; i > 0; i--) {
            swapItems(list, indexOfLargest(list, i), i);
        }
    }    
}
