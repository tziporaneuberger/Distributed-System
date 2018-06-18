import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class Slave2 {

	private int slaveID;
    Queue<Job> jobsQueue;
	int totalJobsRanking;
	boolean boolReceivingMoreJobs;
	
	public Slave2 (int id) {
		this.slaveID=id;
		jobsQueue = new LinkedList<Job>();
		this.totalJobsRanking = 0;
		this.boolReceivingMoreJobs = true;
	}
	
	public boolean IsReceivingAnyMoreJobs() {
		return boolReceivingMoreJobs;
	}
	
	public void setNotReceivingAnyMoreJobs() {
		this.boolReceivingMoreJobs = false;
	}

	public int getID() {
		return slaveID;
	}

	public  int getTotalRanking() {
		return totalJobsRanking;
	}
	
	public void setTotalJobRanking(int totalRanking) {
		this.totalJobsRanking = totalRanking;
	}
	
	public Queue<Job> getJobQueue(){
		return jobsQueue;
	}
	
	public void addToThisSlavesQueue(Integer jobID){
		Job newJob = new Job(jobID);
		jobsQueue.add(newJob);
		totalJobsRanking += newJob.getJobRanking();
	}

	
	public static void main(String[] args) throws IOException {    
		// Hardcode in IP and Port here if required
    	args = new String[] {"192.168.1.5", "30123"};
    	
        if (args.length != 2) {
            System.err.println(
                "Usage: java Slave1 <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            Socket slaveSocket = new Socket(hostName, portNumber);//creating the Slave socket
            PrintWriter writerToMaster = new PrintWriter(slaveSocket.getOutputStream(), true);//stream to write to MainMasterServer          
            BufferedReader readerFromMaster= new BufferedReader(new InputStreamReader(slaveSocket.getInputStream())) //read request from MainMasterServer
        ) {
    		System.out.println("connected to server port, Slave can now write to and get requests from MainMasterServer");
    		String input;
    		input = readerFromMaster.readLine();
    		Integer id = Integer.parseInt(input);//get id
    		Slave2 thisSlave = new Slave2(id);
    		System.out.println("A Slave has just been created with the id of " 
    				+ thisSlave.getID());
    		writerToMaster.println("A Slave has just been created with the id of " 
    				+ thisSlave.getID());
    		 		
    		Job currentJob;
    		Integer jobRanking;
    		Integer inputFromMaster;
    		final Integer JOBS_ARE_COMPLETE = 0;
    		while(thisSlave.IsReceivingAnyMoreJobs()) {
    			if (!thisSlave.getJobQueue().isEmpty()) {
    				while (!thisSlave.getJobQueue().isEmpty()) {
        				currentJob = thisSlave.getJobQueue().remove(); //remove the next job from the queue
    					jobRanking = currentJob.getJobRanking();
        				System.out.println("the next job with the ranking of " + jobRanking + 
        						" was removed from the Slave's queue. It will now be run");
    					currentJob.runJob();
    					System.out.println("The job was run successfully! :)");
    					writerToMaster.println(jobRanking);
    					System.out.println("The ranking of the completed job was just sent to the Master.");
    					thisSlave.setTotalJobRanking(thisSlave.getTotalRanking()-jobRanking);
    				}
    				
    			}
    			System.out.println("now the Slave will see if the Master sent any more jobs to be completed");
    			if((input = readerFromMaster.readLine()) != null) {
    				System.out.println("Input was just read from the master: " + input);
    				inputFromMaster = Integer.parseInt(input);
    				if(inputFromMaster == JOBS_ARE_COMPLETE) {
    					thisSlave.setNotReceivingAnyMoreJobs();
    					System.out.println("The jobs are all complete and the Client is not sending any more jobs");
    					break;//break from this loop
    				}
    				else {
        				thisSlave.addToThisSlavesQueue(inputFromMaster);
        				System.out.println("the job received from the Master was added to this Slave's queue.");
    				}
    			}
    		}
    		
        }catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
        finally {
            System.out.println("Socket connection with this slave has been closed.");
        }
	}
}
