package MyHttpServer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

/**
 * A simple HTTP server that handles GET requests and returns information about the request.
 * This server listens on port 8080 and responds to requests at the "/hello" path.
 * Each request is handled in a separate thread.
 */
public class MyHttpServer {
    
    /**
     * Main method to start the HTTP server.
     * STEP 1: Create the server class and main method
     */
    public static void main(String[] args) {
        try {
            // STEP 2: Create the HttpServer instance
            // Create a new HttpServer that listens on port 8080 with default backlog
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            System.out.println("Server starting on port 8080");
            
            // STEP 7: Create a context and start the server
            // Map the "/hello" path to our handler
            server.createContext("/hello", new MyHandler());
            
            // Start the server - it will now listen for incoming HTTP requests
            server.start();
            System.out.println("Server is running. Visit http://localhost:8080/hello in your browser or run the client application");
            
        } catch (IOException e) {
            // Handle any exceptions that might occur during server creation
            System.out.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * STEP 3: Create the HttpHandler (inner class)
     * This handler receives HTTP requests and delegates processing to a new thread.
     */
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            // STEP 4: Implement request handling in a separate thread
            // Create and start a new thread to handle this request
            new RequestHandlerThread(exchange).start();
        }
    }
    
    /**
     * Thread class that handles HTTP requests.
     * This allows the server to handle multiple requests concurrently.
     */
    static class RequestHandlerThread extends Thread {
        private HttpExchange exchange;
        
        /**
         * Constructor that takes the HttpExchange object.
         */
        public RequestHandlerThread(HttpExchange exchange) {
            this.exchange = exchange;
        }
        
        /**
         * STEP 5: Implement request processing and response
         * The run method is executed when the thread starts.
         */
        @Override
        public void run() {
            try {
                // Get request information
                String requestMethod = exchange.getRequestMethod();
                URI requestURI = exchange.getRequestURI();
                Headers requestHeaders = exchange.getRequestHeaders();
                
                // Display request information in the server console
                System.out.println("\n=== INCOMING REQUEST ===");
                System.out.println("Method: " + requestMethod);
                System.out.println("URI: " + requestURI);
                System.out.println("Headers:");
                printHeaders(requestHeaders);
                
                // Prepare response with request information
                StringBuilder responseContent = new StringBuilder();
                responseContent.append("Request Details:\n");
                responseContent.append("Method: ").append(requestMethod).append("\n");
                responseContent.append("URI: ").append(requestURI).append("\n");
                responseContent.append("Headers:\n");
                requestHeaders.forEach((key, value) ->
                    responseContent.append(key).append(": ").append(value).append("\n"));
                responseContent.append("\nHandled by thread: ").append(Thread.currentThread().getName());
                
                String response = responseContent.toString();
                
                // Get response headers
                Headers responseHeaders = exchange.getResponseHeaders();
                
                // Set content type to plain text for better browser display
                responseHeaders.set("Content-Type", "text/plain");
                
                // Send response headers with status code 200 (OK) and the content length
                exchange.sendResponseHeaders(200, response.getBytes().length);
                
                // Display response information in the server console
                System.out.println("\n=== OUTGOING RESPONSE ===");
                System.out.println("Status: 200 OK");
                System.out.println("Headers:");
                printHeaders(responseHeaders);
                System.out.println("Body length: " + response.getBytes().length + " bytes");
                
                // Send response body
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (IOException e) {
                // Handle any exceptions that might occur during request processing
                System.out.println("Error handling request: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Always close the exchange to finish handling the request
                exchange.close();
            }
        }
    }
    
    /**
     * STEP 6: Implement printHeaders method
     * Helper method to print HTTP headers in a readable format.
     */
    private static void printHeaders(Headers headers) {
        headers.forEach((key, value) -> {
            System.out.println(key + ": " + String.join(", ", value));
        });
    }
}