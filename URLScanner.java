/*
 * URLScanner.java
 * Brandon Lee 2019
 * Works a lot like "Scanner" class to read web files.
 * Would be optimal to extend Scanner, but can't since Scanner is a final class.
 */

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class URLScanner {
    private String address; // The URL address of the website being scanned
    public String getAddress() {return this.address;}
    
    private Scanner sc; // The scanner that actually scans the website
    
    public URLScanner(String address) throws IOException {
        this.address = address;
        URL source = new URL(address);
        this.sc = new Scanner(source.openStream());
    }
    
    // Some implementations of Scanner's methods
    public boolean hasNext() {return sc.hasNext();}
    public String next() {return sc.next();}
    public boolean hasNextLine() {return sc.hasNextLine();}
    public String nextLine() {return sc.nextLine();}
    public void close() {sc.close();}
    
    // Reads the entirety of the website and returns it as a string
    public String readAll() {
        String allLines = "";
        while (this.hasNextLine()) {
            String line = this.nextLine();
            allLines += line;
        }
        return allLines;
    }
}