package edu.escuelaing.arem.ASE.app;
import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServer {
    private static ConcurrentHashMap<String, StringBuffer> cache;
    public static void main(String[] args) throws IOException {
        cache = new ConcurrentHashMap<>();

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

                    //Verifies if the movie title has not been requested
                    if (!cache.containsKey(movieTitle)) {
                        //External API request
                        apiResponse = connectionToApi.getMovieInfo(movieTitle);
                        cache.put(movieTitle, apiResponse);
                    } else {
                        apiResponse = cache.get(movieTitle);
                    }
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
                    "        <title>Movies Info</title>\n" +
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
                    "            function formatJSON(jsonObj) {\n" +
                    "               let htmlOutput = \"<ul>\";\n" +
                    "\n" +
                    "               for (let key in jsonObj) {\n" +
                    "                   if (jsonObj.hasOwnProperty(key)) {\n" +
                    "                       htmlOutput += \"<li><strong>\" + key + \":</strong> \";\n" +
                    "\n" +
                    "                        if (key === \"Poster\" && typeof jsonObj[key] === \"string\") {\n" +
                    "                           // Si la llave es \"Poster\" y el valor es una cadena, crea una etiqueta de imagen\n" +
                    "                           htmlOutput += \"<img src='\" + jsonObj[key] + \"' alt='Poster'>\";\n" +
                    "                        } else if (typeof jsonObj[key] === \"object\") {\n" +
                    "                           // Si es un objeto, recursivamente formatea sus propiedades\n" +
                    "                           htmlOutput += formatJSON(jsonObj[key]);\n" +
                    "                        } else {\n" +
                    "                           // Si es un valor simple, agrégalo directamente\n" +
                    "                           htmlOutput += jsonObj[key];\n" +
                    "                        }\n" +
                    "\n" +
                    "                   htmlOutput += \"</li>\";\n" +
                    "                   }\n" +
                    "               }\n" +
                    "\n" +
                    "               htmlOutput += \"</ul>\";\n" +
                    "               return htmlOutput;\n" +
                    "           }\n"+
                    "            function loadGetMsg() {\n" +
                    "                let nameVar = document.getElementById(\"name\").value;\n" +
                    "                const xhttp = new XMLHttpRequest();\n" +
                    "                xhttp.onload = function() {\n" +
                    "                    try {\n" +
                    "                         response = JSON.parse(this.responseText);\n"+
                    "                         const formattedOutput = formatJSON(response);\n"+
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
