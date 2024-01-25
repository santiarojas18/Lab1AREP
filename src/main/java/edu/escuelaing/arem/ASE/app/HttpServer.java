package edu.escuelaing.arem.ASE.app;
import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;

            //Initializing the movieTitle to be searched
            String movieTitle = "";
            //External connection api
            HttpConnectionExample connectionToApi = new HttpConnectionExample();
            StringBuffer apiResponse = new StringBuffer();
            boolean giveMovieInfo = false;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (inputLine.contains("title") && !inputLine.contains("Referer")) {
                    String regex = "=([^\\s]+)\\s+HTTP/1.1";
                    // Compila la expresión regular
                    Pattern pattern = Pattern.compile(regex);
                    // Crea un objeto Matcher para realizar la búsqueda en la cadena de entrada
                    Matcher matcher = pattern.matcher(inputLine);
                    // Verifica si se encuentra la coincidencia
                    if (matcher.find()) {
                        // Obtiene el valor encontrado en el grupo 1
                        String value = matcher.group(1);
                        movieTitle = value;
                    }
                    giveMovieInfo = true;
                    System.out.println("API's being called and the title is: " + movieTitle);
                    //External API request
                    apiResponse = connectionToApi.getMovieInfo(movieTitle);
                    System.out.println(apiResponse);
                }
                if (!in.ready()) {
                    break;
                }
            }

            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type:text/html; charset=utf-8\r\n"
                    + "\r\n"
                    + "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "    <head>\n" +
                    "        <title>Form Example</title>\n" +
                    "        <meta charset=\"UTF-8\">\n" +
                    "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    </head>\n" +
                    "    <body>\n" +
                    "        <h1>Movies Info</h1>\n" +
                    "        <form action=\"/hello\">\n" +
                    "            <label for=\"name\">Name:</label><br>\n" +
                    "            <input type=\"text\" id=\"name\" name=\"title\" value=\"\"><br><br>\n" +
                    "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
                    "        </form> \n" +
                    "        <div id=\"getrespmsg\"></div>\n" +
                    "\n" +
                    "        <script>\n" +
                    "            function loadGetMsg() {\n" +
                    "                let nameVar = document.getElementById(\"name\").value;\n" +
                    "                const xhttp = new XMLHttpRequest();\n" +
                    "                xhttp.onload = function() {\n" +
                    "                    try {\n" +
                    "                         response = JSON.parse(this.responseText);\n"+
                    "                         const formattedOutput = JSON.stringify(response, null, 2);\n"+
                    "                         document.getElementById(\"getrespmsg\").innerHTML = \"<pre>\" + formattedOutput + \"</pre>\";\n"+
                    "                     } catch (error) {\n"+
                    "                         document.getElementById(\"getrespmsg\").innerHTML =\n" +
                    "                         this.responseText;\n" +
                    "                     }\n"+
                    "                }\n" +
                    "                xhttp.open(\"GET\", \"/?title=\"+nameVar);\n" +
                    "                xhttp.send();\n" +
                    "            }\n" +
                    "        </script>\n" +
                    "\n" +
                    "    </body>\n" +
                    "</html>";

            //Returns only the movies info
            if (giveMovieInfo) {
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type:text/html; charset=utf-8\r\n"
                        + "\r\n"
                        + apiResponse.toString();
                giveMovieInfo = false;
            }

            out.println(outputLine);

            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
}
