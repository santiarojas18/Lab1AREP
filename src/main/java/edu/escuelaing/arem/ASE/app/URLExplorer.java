
package edu.escuelaing.arem.ASE.app;

import java.net.*;
/**
 *
 * @author santiago.arevalo-r
 */
public class URLExplorer {
    
    public static void main (String[] args) throws MalformedURLException {
        URL myurl = new URL("https://campusvirtual.escuelaing.edu.co:5678/moodle/ClientService.pdf?val=67&ang=6.30#grafico");
        System.out.println("Host: " + myurl.getHost());
        System.out.println("Authority: " + myurl.getAuthority());
        System.out.println("Path: " + myurl.getPath());
        System.out.println("Protocol: " + myurl.getProtocol());
        System.out.println("Port: " + myurl.getPort());
        System.out.println("Query: " + myurl.getQuery());
        System.out.println("Ref.: " + myurl.getRef());
        System.out.println("File: " + myurl.getFile());
    }
    
}
