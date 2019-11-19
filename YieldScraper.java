/*
 * YieldScraper.java
 * Brandon Lee 2019
 * Downloads and parses all the yield curve rate data from the treasury website:
 * https://www.treasury.gov/resource-center/data-chart-center/interest-rates/Pages/TextView.aspx?data=yieldAll
 * Produces a csv file that contains all the parsed data. See 'all_yield_data.csv' in this repository for an example.
 */

import java.io.*;
import java.text.*;
import java.util.*;

public class YieldScraper {
    // The maturity of the treasury bonds in months
    public static final int[] MATURITIES = {1, 2, 3, 6, 12, 24, 36, 60, 84, 120, 240, 360};
    
    // Keys are dates, float arrays are interest rates per maturity
    private HashMap<String, String[]> data = new HashMap<String, String[]>();
    public HashMap<String, String[]> getData() {return data;}
    
    // Downloads all yield curve data (1990-present) from treasury website and puts it into a csv file.
    public void downloadAll(String destinationpath) throws IOException, ParseException {
        // Download all yield rate data
        int currentyear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentyear; year >= 1990; year--) data.putAll(downloadYear(year));
        
        // Print data into csv file
        PrintWriter pw = new PrintWriter(new File(destinationpath));
        pw.println(this.stringData());
        pw.close();
        
        // Sort the csv file created by date
        sortcsv(destinationpath, true);
    }
    
    // Downloads yield curve data from a given year and returns it as a hashmap (same format as 'data')
    public static HashMap<String, String[]> downloadYear(int year) throws IOException, ParseException {
        // The hashmap to be returned
        HashMap<String, String[]> yeardata = new HashMap<String, String[]>();
        
        // The treasury website we get the data from 
        String address = "https://www.treasury.gov/resource-center/data-chart-center/interest-rates/Pages/TextView.aspx?data=yieldYear&year="+year;
        URLScanner sc = new URLScanner(address);
        
        // The entirety of the website's contents are downloaded, then ...
        String alltext = sc.readAll();
        // ... the relevant content is isolated with tokenate_text.
        ArrayList<String> tokens = tokenate_text(alltext);
        
        // Parses the tokenated content and puts the data in our hashmap
        String focusdate = tokens.get(0);
        int i = 0;
        for (String t : tokens) {
            if (t.length() > 4) { // A date
                i = 0;
                focusdate = t;
                yeardata.put(focusdate, new String[MATURITIES.length]);
            } else { // An interest rate
                String[] newarray = yeardata.get(focusdate);
                newarray[i] = t;
                yeardata.replace(focusdate, newarray);
                i++;
            }
        }
        sc.close();
        
        return yeardata;
    }
    
    // Takes the csv file given and sorts by the first column. If header=true, ignores the first row in sorting.
    public void sortcsv(String filename, boolean header) throws IOException {
        // Parses the csv as a two dimensional list.
        ArrayList<List<String>> csvLines = new ArrayList<List<String>>();
        Scanner sc = new Scanner(new File(filename));
        if (header) sc.nextLine();
        while (sc.hasNextLine()) {
            String[] arrayversion = sc.nextLine().split(",");
            List<String> aslist = Arrays.asList(arrayversion);
            csvLines.add(aslist);
        }
        sc.close();
        
        // Sorts rows using a comparator that compares the first cell of each row.
        Comparator<List<String>> comp = new Comparator<List<String>>() {
            public int compare(List<String> csvLine1, List<String> csvLine2) {
                Date date1 = new Date();
                Date date2 = new Date();
                try {
                    date1 = new SimpleDateFormat("MM/dd/yy").parse(csvLine1.get(0));
                    date2 = new SimpleDateFormat("MM/dd/yy").parse(csvLine2.get(0));
                } catch (ParseException e) {
                    return 0;
                }
                return date1.compareTo(date2);
            }
        };
        Collections.sort(csvLines, comp);
        
        // Prints the sorted data in the csv file.
        String toprint = "Date,";
        for (int m : MATURITIES) {
            toprint += m + ",";
        }
        toprint = toprint.substring(0,toprint.length()-1);
        toprint += "\n";
        for (List<String> line : csvLines) {
            for (String cell : line) {
                toprint += cell+",";
            }
            toprint = toprint.substring(0, toprint.length()-1);
            toprint += "\n";
        }
        PrintWriter pw = new PrintWriter(new File(filename));
        pw.println(toprint);
        pw.close();
    }
    
    // Converts the data into a string that can be printed onto a comma-delimited csv file
    public String stringData() {
        String returnval = "Date,";
        for (int m : MATURITIES) {
            returnval += m + ",";
        }
        returnval = returnval.substring(0,returnval.length()-1);
        returnval += "\n";
        for (String key : data.keySet()) {
            returnval += key;
            returnval += ",";
            for (String f : data.get(key)) {
                returnval += f + ",";
            }
            returnval = returnval.substring(0,returnval.length()-1);
            returnval += "\n";
        }
        return returnval;
    }
    
    // Strips the raw website content so that it isolates the relevant information.
    // Preserves only the content of the table holding the data, returning all the table's cells in a list.
    public static ArrayList<String> tokenate_text(String alltext) {
        // Cuts the content to ignore content preceding and following the table.
        alltext = alltext.substring(alltext.indexOf("t-chart"));
        alltext = alltext.substring(0,alltext.indexOf("End Main Content Area"));
        
        // Most of the trimming is done in this block. Essentially, we go through each character in the
        //   content and pick out the ones we want.
        String alltext_const = alltext;
        alltext = "";
        boolean adding = true;
        char prevc = '0'; // keeps track of previous characters to check if N/A is being parsed.
        char prevprevc = '0';
        for (char c : alltext_const.toCharArray()) {
            // Removes any content encapsulated within angle brackets.
            // Also adds tab characters between each cell of the table.
            if (c == '<') {
                adding = false;
                if (alltext.charAt(alltext.length()-1) != '\t') alltext += '\t';
            }
            else if (c == '>') adding = true;
            
            // Gets rid of any characters that aren't numbers, decimal points, or date slashes...
            else if (adding && (c >= '0' && c <= '9' || c == '/' || c == '.')) alltext += c;
            // Except for "N/A" strings.
            else if (adding && prevprevc == 'N' && prevc == '/' && c == 'A') alltext += "N/A";
            prevprevc = prevc;
            prevc = c;
        }
        
        // Removes the header of the table from the text.
        String headerline = "1\t2\t3\t6\t1\t2\t3\t5\t7\t10\t20\t30\t";
        alltext = alltext.substring(alltext.indexOf(headerline)+headerline.length());
        
        // Splits the parsed text so far into tokens separated by tab characters.
        String[] tokenated_raw = alltext.split("\t");
        // One last filter: Removes all tokens that don't match the expected format of an interest rate, a
        //   date, or "N/A".
        ArrayList<String> tokens = new ArrayList<String>();
        for (String t : tokenated_raw) {
            if (t.length() == 4 || (t.length() == 8 && t.contains("/"))) {
                if (t.equals("/N/A")) tokens.add("NaN");
                else{
                    int noperiods = t.length() - t.replace(".", "").length();
                    if (noperiods <= 1) tokens.add(t);
                }
            }
        }
        return tokens;
    }
    
    public static void main(String args[]) throws IOException, ParseException {
        YieldScraper ys = new YieldScraper();
        ys.downloadAll("all_yield_data.csv");
    }
}
