




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) throws IOException {
        
		// Hardcode in IP and Port here if required
    	args = new String[] {"192.168.1.5", "30124"};
    	
        if (args.length != 2) {
            System.err.println(
                "Usage: java Client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            Socket clientSocket = new Socket(hostName, portNumber);//creating the client socket
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));//standard input stream to get user's requests	
            PrintWriter requestWriter = new PrintWriter(clientSocket.getOutputStream(), true);//stream to write text requests to server          
            BufferedReader responseReader= new BufferedReader(new InputStreamReader(clientSocket.getInputStream())) //read response from server
        ) {
    		System.out.println("connected to server port, client can now write to and get requests from server");
    		
    		String userInput;
    		Integer jobIDRequest;
  			char anotherQuestion = 'a';
  			
  			System.out.println("Welcome to the job-doer!\n"
  					+ "The jobs that you can request from us are as follows.\n"
  					+ "\t1. Calculate the sum of two numbers\n"
  					+ "\t2. Add a number to itself 10 times\n"
  					+ "\t3. Run two nested loops each 10 times\n"
  					+ "\t4. Run three nested loops each 10 times\n"
  					+ "\t5. Run four nested loops each 10 itmesn"
  					+ "\t6. Run five nested loops each 10 times\n"
  					+ "\t7. Add the numbers 1-100\n\n");
  			
            do {
            	//step 1:
            	//user enters the job id of the job he wants to be completed
  				System.out.println("Please enter the job ID of the job that you wish to be completed."
  						+ "Please enter a number from 1-7.");
  				userInput = stdIn.readLine();
  				jobIDRequest = Integer.parseInt(userInput);
  				if (jobIDRequest < 1 || jobIDRequest > 7) {
  					System.out.println("Please enter a Job-ID in the range of 1-7");
  				}
  				else {
  	                requestWriter.println(jobIDRequest); // the jobID request is sent to the server
  				}

  				//step 2:
                //this is what is used for the loop
                System.out.println("Do you want another job to be completed? Please enter 'Y' for yes or 'N' for no.");
                anotherQuestion = stdIn.readLine().toUpperCase().charAt(0);
                requestWriter.println(anotherQuestion);
            } while (anotherQuestion == 'Y');
            
            //when the client finishes sending all of his job requests, he will wait for the server to let him know
            //that all of the jobs were completed.
            //when all of the jobs are completed, the server will send a message to the client to let him know :)
 			String serverResponse;
 			serverResponse = responseReader.readLine();
 			System.out.println(serverResponse);
            
            
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
        finally {
            System.out.println("Socket connections closed.");
        }
    }
}
