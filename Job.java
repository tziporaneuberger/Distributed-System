





import java.util.Calendar;

public class Job extends Thread{

	private int jobID; 
	private int jobRanking;
	private long completionTimeInMillis;
	private final int computationNumber = 10;
	private boolean jobCompleted;
	
	public Job(int jobID) {
		if (jobID < 1 || jobID > 7) {
			throw new IllegalArgumentException("Job ID must be in the range of 1-7");
		}
		this.jobID = jobID;
		this.jobRanking = jobID;
		this.jobCompleted = false;
	}

	
	public int getJobID() {
		return this.jobID;
	}
	
	public int getJobRanking() {
		return this.jobRanking;
	}
	
	public long getCompletionTimeInMillis() {
		return this.completionTimeInMillis;
	}

	
	public void runJob() {
		try {
			Calendar time = Calendar.getInstance();
			performJobTask(jobID);
			Thread.sleep(jobRanking * 1000);
			this.completionTimeInMillis = Calendar.getInstance().getTimeInMillis() - time.getTimeInMillis();	
			jobCompleted = true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void performJobTask(int jobID){
		switch(jobID) {
		case 1:
			sumOfTwoNumbers();
			break;
		case 2:
			numberAddedToItselfNTimes();					
			break;
		case 3:
			TwoNestedLoops();
			break;
		case 4:
			ThreeNestedLoops();
			break;
		case 5:
			FourNestedLoops();
			break;
		case 6:
			FiveNestedLoops();
			break;
		case 7:
			addNumbers1to100();
			break;
		default:
			break;
		}
	}
	
	//Job 1: O(1) operation
	private void sumOfTwoNumbers() {
		int sum = computationNumber+computationNumber;
	}
	
	//Job 2: O(n) operation
	private void numberAddedToItselfNTimes() {
		int sum = 0;
		for (int i=0; i<5; i++) {
			sum += computationNumber;
		}
	}
	
	//Job 3: O(n^2) operation
	private void TwoNestedLoops() {
		long sum = 0;
		for (int i=0; i<computationNumber; i++) {
			for (int j=0; j<computationNumber; j++) {
				sum += i+j;
			}
		}
	}
	
	//Job 4: O(n^3) operation
	private void ThreeNestedLoops() {
		long sum = 0;
		for (int i=0; i<computationNumber; i++) {
			for (int j=0; j<computationNumber; j++) {
				for (int k=0; k<computationNumber; k++) {
					sum += i+j+k;
				}
			}
		}
	}
	
	//Job 5: O(n^4) operation
	private void FourNestedLoops() {
		long sum = 0;
		for (int i=0; i<computationNumber; i++) {
			for (int j=0; j<computationNumber; j++) {
				for (int k=0; k<computationNumber; k++) {
					for (int l=0; l<computationNumber; l++) {
						sum += i+j+k+l;
					}
				}
			}
		}
	}
	
	//Job 6: O(n^5) operation
	private void FiveNestedLoops() {
		long sum = 0;
		for (int i=0; i<computationNumber; i++) {
			for (int j=0; j<computationNumber; j++) {
				for (int k=0; k<computationNumber; k++) {
					for (int l=0; l<computationNumber; l++) {
						for (int m=0; m<computationNumber; m++) {
							sum += i+j+k+l+m;
						}
					}
				}
			}
		}
	}

	//Job 7:
	private void addNumbers1to100() {
		int sum = 0;
		for (int i=1; i<=100; i++) {
			sum += i;
		}
	}	

}
