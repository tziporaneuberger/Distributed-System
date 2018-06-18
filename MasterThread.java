import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

class MasterThread extends Thread implements Runnable  {
	private ServerSocket serverSocket = null;
    private  int id;
	private int totalRanking;
	private Queue<Integer> newJobsQueue;
	private Integer numJobsInSlaveQueue;
	private boolean receivingMoreJobs;
	
	public MasterThread(ServerSocket s,int id)
	{
		this.serverSocket = s;
		this.id=id;
		this.totalRanking = 0;
		this.newJobsQueue = new LinkedList<Integer>();
		numJobsInSlaveQueue = 0;
		this.receivingMoreJobs = true;
	}

	public int getMasterThreadID() {
		return id;
	}

	public int getTotalRanking() {
		return totalRanking;
	}
	
	public void addNewJob(Integer jobID) {
		newJobsQueue.add(jobID);
		int jobRanking = jobID;//used for simplicity, not a typical way to rank jobs
		totalRanking += jobRanking;
	}
	
	public void setNotReceivingAnyMoreJobs() {
		receivingMoreJobs = false;
	}

	public void run() {
		//This Slave thread accepts its own Slave socket from the shared MainMasterServer socket
		try (Socket slaveSocket = serverSocket.accept();
				PrintWriter writerToSlave = new PrintWriter(slaveSocket.getOutputStream(), true);
				BufferedReader readerFromSlave = new BufferedReader(new InputStreamReader(slaveSocket.getInputStream()));
			) {
			System.out.println("Master thread " + id + " successfully connected to Slave");
			writerToSlave.println(id);//send the id to the Slave
			String input = readerFromSlave.readLine();//read from Slave that was connected successfully
			System.out.println(input);
			Integer jobID;
			Integer jobRankingOfJobJustCompleted;
			final Integer JOBS_ARE_COMPLETE = 0;

			int numIterationOfLoop = 1;
			while(receivingMoreJobs == true || !newJobsQueue.isEmpty() || numJobsInSlaveQueue > 0) {
				if (!newJobsQueue.isEmpty()) { //if the newJobsQueue is not empty, send those jobs to the slave
					do { //loop through all of the jobs in the newJobsQueue to send to the slave
						System.out.println("MasterThread " + id + ": removing the next job from the newJobsQueue");
						jobID = newJobsQueue.poll();//sumRanking was incremented when the job was added to this queue
													//so it does not need to be incremented here
						writerToSlave.println(jobID);//send this job ID to the slave
						numJobsInSlaveQueue++; //one more job is now in the Slave's queue
						System.out.println("MasterThread " + id + ": The jobID " + jobID + " was just sent to the slave");
					} 
					while (!newJobsQueue.isEmpty());
				} 
				//this sleeping is necessary for the program to run properly
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (newJobsQueue.isEmpty() && numJobsInSlaveQueue > 0) { //once the above do-while loop finishes, this should evaluate to true, so it will evaluate to true regardless
					do {
						//the slave does have more jobs in its queue, so we are waiting 
						//for the slave to send a message that a job was completed
						while((input = readerFromSlave.readLine()) == null) {
						}					
						//the Slave will return the ranking of the job that he just finished
        				jobRankingOfJobJustCompleted = Integer.parseInt(input);
						System.out.println("MasterThread " + id + ": The Slave just completed a job!!!");
        				totalRanking -= jobRankingOfJobJustCompleted;
        				numJobsInSlaveQueue--;
					} 
					while (newJobsQueue.isEmpty() && numJobsInSlaveQueue > 0);
				}	
			}
			System.out.println("MasterThread " + id + ": All jobs are complete!!!");
			writerToSlave.println(JOBS_ARE_COMPLETE);
		} 
		catch (IOException e) {
			System.out.println(
					"Exception caught when trying to listen on port " + serverSocket.getLocalPort() + " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}
}

