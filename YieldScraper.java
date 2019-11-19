/*
 * YieldScraper.java
 * Brandon Lee 2019
 * Downloads and parses all the yield curve rate data from the treasury website:
 * https://www.treasury.gov/resource-center/data-chart-center/interest-rates/Pages/TextView.aspx?data=yieldAll
 * Produces a csv file that contains all the parsed data.
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class YieldScraper {
    // The maturity of the treasury bonds in months
    public static final int[] MATURITIES = {1, 2, 3, 6, 12, 24, 36, 60, 84, 120, 240, 360};
    
    // Keys are dates, float arrays are interest rates per maturity
    private HashMap<String, String[]> data = new HashMap<String, String[]>();
    public HashMap<String, String[]> getData() {return data;}
    
    public void download() throws IOException, ParseException {
        for (int year = 2019; year >= 1990; year--) {
            System.out.println("Parsing year "+year);
            String address = "https://www.treasury.gov/resource-center/data-chart-center/interest-rates/Pages/TextView.aspx?data=yieldYear&year="+year;
            URLScanner sc = new URLScanner(address);
            
            String alltext = sc.readAll();        
            ArrayList<String> tokens = tokenate_text(alltext);
            
            String focusdate = tokens.get(0);
            int i = 0;
            for (String t : tokens) {
                if (t.length() > 4) { // A date
                    i = 0;
                    focusdate = t;
                    data.put(focusdate, new String[MATURITIES.length]);
                } else { // An interest rate
                    String[] newarray = data.get(focusdate);
                    newarray[i] = t;
                    data.replace(focusdate, newarray);
                    i++;
                }
            }
            
            sc.close();
        }
        
        PrintWriter pw = new PrintWriter(new File("all_yield_data.csv"));
        pw.println(this.stringData());
        pw.close();
        
        sortcsv("all_yield_data.csv", true);
    }
    
    public void sortcsv(String filename, boolean header) throws IOException {
        ArrayList<List<String>> csvLines = new ArrayList<List<String>>();
        Scanner sc = new Scanner(new File(filename));
        if (header) sc.nextLine();
        while (sc.hasNextLine()) {
            String[] arrayversion = sc.nextLine().split(",");
            List<String> aslist = Arrays.asList(arrayversion);
            csvLines.add(aslist);
        }
        sc.close();
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
        PrintWriter pw = new PrintWriter(new File("all_yield_data.csv"));
        pw.println(toprint);
        pw.close();
    }
    
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
    
    public static ArrayList<String> tokenate_text(String alltext) {
        alltext = alltext.substring(alltext.indexOf("t-chart"));
        alltext = alltext.substring(0,alltext.indexOf("End Main Content Area"));
        
        String alltext_const = alltext;
        alltext = "";
        boolean adding = true;
        char prevc = '0';
        char prevprevc = '0';
        for (char c : alltext_const.toCharArray()) {
            if (c == '<') {
                adding = false;
                if (alltext.charAt(alltext.length()-1) != '\t') alltext += '\t';
            }
            else if (c == '>') adding = true;
            else if (adding && (c >= '0' && c <= '9' || c == '/' || c == '.')) alltext += c;
            else if (adding && prevprevc == 'N' && prevc == '/' && c == 'A') alltext += "N/A";
            prevprevc = prevc;
            prevc = c;
        }
        String headerline = "1\t2\t3\t6\t1\t2\t3\t5\t7\t10\t20\t30\t";
        alltext = alltext.substring(alltext.indexOf(headerline)+headerline.length());
        String[] tokenated_raw = alltext.split("\t");
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
        ys.download();
    }
}
