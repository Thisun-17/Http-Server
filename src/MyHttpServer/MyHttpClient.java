package MyHttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * A simple HTTP client that sends a GET request to an HTTP server and displays the response.
 * This client connects to http://localhost:8080/hello by default.
 */
public class MyHttpClient {
    
    /**
     * STEP 8: Create the client class and main method
     * Main method to start the HTTP client.
     */
    public static void main(String[] args) {
        try {
            // STEP 9: Create the URL and open a connection
            // Create a URL object for the server endpoint
            URL url = new URL("http://localhost:8080/hello");
            System.out.println("Connecting to: " + url.toString());
            
            // Open a connection to the URL
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
            // STEP 11: Get and display default request headers
            // Display the default headers that will be sent
            System.out.println("DEFAULT REQUEST HEADERS:");
            Map<String, List<String>> defaultHeaders = con.getRequestProperties();
            printHeaders(defaultHeaders);
            
            // STEP 12: Set the request method and get the response code
            // Configure the request method to GET
            con.setRequestMethod("GET");
            System.out.println("Sending GET request...");
            
            // Get the response code from the server
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            // STEP 13: Read and process the response
            // Check if the response is successful (HTTP 200 OK)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Create a reader to read the response body
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                // Read the response line by line
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n");
                }
                in.close();
                
                // Display the full response
                System.out.println("Response: \n" + response.toString());
                
                // Display response headers
                System.out.println("RESPONSE HEADERS:");
                Map<String, List<String>> responseHeaders = con.getHeaderFields();
                printHeaders(responseHeaders);
            } else {
                // Handle unsuccessful responses
                System.out.println("GET request not successful. Response code: " + responseCode);
            }
            
        } catch (IOException e) {
            // Handle any exceptions that might occur during the connection
            System.out.println("Error during connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * STEP 10: Implement printHeaders method for the client
     * Helper method to print HTTP headers in a readable format.
     * This is similar to the server's printHeaders but works with Map<String, List<String>>
     * instead of Headers.
     */
    private static void printHeaders(Map<String, List<String>> headers) {
        headers.forEach((key, value) -> {
            // Some headers might have null keys, so we check for that
            if (key != null) {
                System.out.println(key + ": " + String.join(", ", value));
            }
        });
    }
}