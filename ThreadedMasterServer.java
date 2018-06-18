

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadedMasterServer {

    public static void main(String[] args) throws IOException {
        
    	args = new String[] {"30124", "30122", "30123"};
    	
        if (args.length != 3)
        {
            System.err.println("Usage: java ThreadedMasterServer <port #1> <port #2> <port #3>");
            System.exit(1);
        }
        
        int portNumberForClient = Integer.parseInt(args[0]);
        int portNumberForSlave1 = Integer.parseInt(args[1]);
        int portNumberForSlave2 = Integer.parseInt(args[2]);
                
        try (
                //opening up a socket at a new port to connect to the client
                ServerSocket serverSocket = new ServerSocket(portNumberForClient);
                //accepting a client socket, waits for a client to connect to this port and ip address
                Socket clientSocket = serverSocket.accept();
                //to read in the requests from the client
                BufferedReader requestReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                //to write a response to the client when all of the jobs are completed :)!
                PrintWriter responseWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                
        		//server sockets that are sent as parameters in the MasterThreads' constructors
                ServerSocket serverSocketToSlave1 = new ServerSocket(portNumberForSlave1);
                ServerSocket serverSocketToSlave2 = new ServerSocket(portNumberForSlave2);
                ) {
            ArrayList<MasterThread> threads = new ArrayList<MasterThread>();
            
            threads.add(new MasterThread(serverSocketToSlave1, 1));
            threads.add(new MasterThread(serverSocketToSlave2, 2));
            
            for (MasterThread t : threads)
                t.start();         

            String input = "";
            int jobRequestID;
            char anotherJobRequest = 'a';
            MasterThread masterThread;
            int lowestTotalRanking, totalRankingOfThisSlave, idOfSlaveWithLowestTotalRanking;
            int threadID;

            do {
                //step 1: get the job request from the client
            	System.out.println("TMS: Waiting for a job request from the client");
                input = requestReader.readLine();
                jobRequestID = Integer.parseInt(input);
                if(jobRequestID >= 1 && jobRequestID <= 7) {
                    System.out.println("TMS: The job request " + jobRequestID + " was received from the Client");

                    //step 2: algorithm- decide which slave to give the job to!
                    //initialize the lowestSumTime variables                
                    masterThread = threads.get(0);
                    totalRankingOfThisSlave = masterThread.getTotalRanking();
                    lowestTotalRanking = totalRankingOfThisSlave;
                    idOfSlaveWithLowestTotalRanking = masterThread.getMasterThreadID();
                    System.out.println("TMS: Total ranking of Slave 1: " + totalRankingOfThisSlave);
                    
                   //loop through the ArrayList of slave threads
                    for(int i=1; i<threads.size(); i++) {
                    	masterThread = threads.get(i);
                    	threadID = masterThread.getMasterThreadID();
                        totalRankingOfThisSlave = masterThread.getTotalRanking();
                        System.out.println("TMS: Total ranking of Slave " + threadID + ": " + totalRankingOfThisSlave);
                        if(totalRankingOfThisSlave <= lowestTotalRanking) {
                            lowestTotalRanking = totalRankingOfThisSlave;
                            idOfSlaveWithLowestTotalRanking = masterThread.getMasterThreadID(); 
                        }
                    }
                    MasterThread slaveWithLowestTotalRanking = threads.get(idOfSlaveWithLowestTotalRanking-1); 
                    
                    //step 3: add this job to the queue of the slave with the lowest total ranking
                    System.out.println("TMS: The new job was sent to the Slave with the lowest total ranking: " + slaveWithLowestTotalRanking.getMasterThreadID());
                    slaveWithLowestTotalRanking.addNewJob(jobRequestID);
                }                
                
                //step 4: see if the client has any more jobs
                System.out.println("TMS: waiting for the client to let the master know if he has any more jobs to complete");
                anotherJobRequest = requestReader.readLine().toUpperCase().charAt(0);
            }while (anotherJobRequest == 'Y');
            
            System.out.println("TMS: Letting MasterThreads know that they are not receiving any more jobs");
            for(MasterThread s : threads) {
                s.setNotReceivingAnyMoreJobs();
            }
            
            System.out.println("TMS: joining the threads");
            for(Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            System.out.println("TMS: We are pleased that all of the jobs have been completed successfully!!! (round of applause)");
            
            //send response to the client that all of the jobs have completed successfully
            responseWriter.println("We are pleased to inform you that all of the jobs have been completed successfully!!! (round of applause)");
        } catch (IOException e) {
            System.out.println(
                    "TMS: Exception caught when trying to listen on port " + portNumberForClient  + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
