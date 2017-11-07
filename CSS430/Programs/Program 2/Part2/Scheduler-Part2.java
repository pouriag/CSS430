/*
*Scheduler MFQS
*CSS430 Operating Systems
*@author: Pouria Ghadimi
*/


import java.util.*;

public class Scheduler extends Thread
{
    private Vector queue0, queue1, queue2;  // creating 3 Queues
    private int timeSlice;
    private static final int DEFAULT_TIME_SLICE = 500;

    // New data added to p161 
    private boolean[] tids; // Indicate which ids have been used
    private static final int DEFAULT_MAX_THREADS = 10000;

    // A new feature added to p161 
    // Allocate an ID array, each element indicating if that id has been used
    private int nextId = 0;
    private void initTid( int maxThreads ) {
		tids = new boolean[maxThreads];
		for ( int i = 0; i < maxThreads; i++ )
			tids[i] = false;
    }

    // A new feature added to p161 
    // Search an available thread ID and provide a new thread with this ID
    private int getNewTid( ) {
		for ( int i = 0; i < tids.length; i++ ) {
			int tentative = ( nextId + i ) % tids.length;
			if ( tids[tentative] == false ) {
				tids[tentative] = true;
				nextId = ( tentative + 1 ) % tids.length;
				return tentative;
			}
		}
		return -1;
    }

    // A new feature added to p161 
    // Return the thread ID and set the corresponding tids element to be unused
    private boolean returnTid( int tid ) {
		if ( tid >= 0 && tid < tids.length && tids[tid] == true ) {
			tids[tid] = false;
			return true;
		}
		return false;
    }

    // A new feature added to p161 
    // Retrieve the current thread's TCB from the queue
    public TCB getMyTcb( ) {
		Thread myThread = Thread.currentThread( ); // Get my thread object
		synchronized( queue0 ) {
			for ( int i = 0; i < queue0.size( ); i++ ) {
			TCB tcb = ( TCB )queue0.elementAt( i );
			Thread thread = tcb.getThread( );
			if ( thread == myThread ) // if this is my TCB, return it
				return tcb;
			}
		}

		for (int i = 0; i < queue1.size(); i++) {
			TCB tcb = (TCB) queue1.elementAt(i);
			Thread thread = tcb.getThread();
			if (thread == myThread) // if this is my TCB, return it
				return tcb;

		}

		for (int i = 0; i < queue2.size(); i++) {
			TCB tcb = (TCB) queue2.elementAt(i);
			Thread thread = tcb.getThread();
			if (thread == myThread) // if this is my TCB, return it
				return tcb;
		}
		return null;

    }

    // A new feature added to p161 
    // Return the maximal number of threads to be spawned in the system
    public int getMaxThreads( ) {
	return tids.length;
    }

    public Scheduler( ) {
		timeSlice = DEFAULT_TIME_SLICE;
		queue0 = new Vector( );
		queue1 = new Vector( );
		queue2 = new Vector( );
		initTid( DEFAULT_MAX_THREADS );
    }

    public Scheduler( int quantum ) {
		timeSlice = quantum;
		queue0 = new Vector( );
		queue1 = new Vector( );
		queue2 = new Vector( );
		initTid( DEFAULT_MAX_THREADS );
    }

    // A new feature added to p161 
    // A constructor to receive the max number of threads to be spawned
    public Scheduler( int quantum, int maxThreads ) {
		timeSlice = quantum;
		queue0 = new Vector( );
		queue1 = new Vector( );
		queue2 = new Vector( );
		initTid( maxThreads );
    }

    private void schedulerSleep( ) {
		try {
			Thread.sleep( timeSlice );
		} catch ( InterruptedException e ) { }
    }

    // A modified addThread of p161 example
    public TCB addThread( Thread t ) {
		//t.setPriority( 2 );
		TCB parentTcb = getMyTcb( ); // get my TCB and find my TID
		int pid = ( parentTcb != null ) ? parentTcb.getTid( ) : -1;
		int tid = getNewTid( ); // get a new TID
		if ( tid == -1)
			return null;
		TCB tcb = new TCB( t, tid, pid ); // create a new TCB
		queue0.add( tcb );		// Add first TCB to queue0
		return tcb;
    }

    // A new feature added to p161
    // Removing the TCB of a terminating thread
    public boolean deleteThread( ) {
		TCB tcb = getMyTcb( );
		if ( tcb!= null )
			return tcb.setTerminated( );
		else
			return false;
    }

    public void sleepThread( int milliseconds ) {
		try {
			sleep( milliseconds );
		} catch ( InterruptedException e ) { }
    }

    // run queue0
	public void runQueue0(){

    	// Get TCB and thread
		TCB currentTCB = (TCB) queue0.firstElement();
		Thread current = currentTCB.getThread();

		// Start a non null thread
		if (current != null) current.start();

		schedulerSleep();
		//System.out.println("* * * Context Switch * * * ");

		if (currentTCB.getTerminated() == true) {//Got terminated? get rid of it
			queue0.remove(currentTCB);
			returnTid(currentTCB.getTid());
		}


		synchronized (queue0) {// still there? add to next queue

			if (current != null && current.isAlive())
				current.suspend();
			queue0.remove(currentTCB);
			queue1.add(currentTCB);
		}

	}

	public int runQueue1(int quantum){

		//Get TCB and Thread
		TCB currentTCB = (TCB) queue1.firstElement();
		Thread current = currentTCB.getThread();

		// resume a non null thread
		if (current != null) {

			if (current.isAlive()) current.resume();
		}

		schedulerSleep();

		// move the quantum by 500ms
		quantum += timeSlice;

		//System.out.println("* * * Context Switch * * * ");

		if (currentTCB.getTerminated() == true) {//Got terminated? get rid of it
			quantum =0;
			queue1.remove(currentTCB);
			returnTid(currentTCB.getTid());
		}

		else if (quantum == 1000) {// has it reached 1 seconds(AKA queue1 quantum)?

			// still there? add to next queue
			if (current != null && current.isAlive())
				current.suspend();
			queue1.remove(currentTCB);
			queue2.add(currentTCB);
			quantum = 0;

		}

		return quantum;// return the quantum
	}

	public int runQueue2(int quantum){

		// get TCB and Thread
		TCB currentTCB =(TCB) queue2.firstElement();
		Thread current = currentTCB.getThread();

		//resume a non null Thread
		if (current != null) {
			if (current.isAlive()) current.resume();
		}


		schedulerSleep();

		// move the quantum by 500ms
		quantum += timeSlice;

		//System.out.println("* * * Context Switch * * * ");

		if (currentTCB.getTerminated() == true) {//Got terminated get rid of it
			quantum = 0;
			queue2.remove(currentTCB);
			returnTid(currentTCB.getTid());
		}

		else if (quantum==2000) {// has it reached 2 seconds(AKA queue2 quantum)?

			if (current != null && current.isAlive()) //is it still there? rotate
				current.suspend();
			queue2.remove(currentTCB); // rotate this TCB to the end
			queue2.add(currentTCB);
			quantum = 0;
		}


		if (queue0.size() > 0 ) quantum =0;/// /if something got into q0 reset the quantum for q1

		return  quantum;
	}


    // A modified run of p161
    public void run( ) {

		int quantum = 0;

		while ( true ) {
			try {

				if ( queue0.size( ) > 0 ) {// is there anything in the q0?
					runQueue0();
				}

				if ( queue0.size( ) == 0 && queue1.size( ) > 0  ) {// is q0 empty and is there anything in the q1?

					quantum = runQueue1(quantum);
				}

				// are q0 and q1 empty and is there anything in the q2?
				if ( queue0.size( ) == 0 && queue1.size( ) == 0 && queue2.size()>0  ) {

					quantum = runQueue2(quantum);

				}
				
			} catch ( NullPointerException e3 ) { };
		}
    }
}

