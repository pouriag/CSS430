
public class SyncQueue {
    private QueueNode [] queues;
    private static int DEFAULT_TID = 0;
    public SyncQueue(){
        queues = new QueueNode[10];

        for (int i = 0 ; i < 10 ; i++){// to initilize the array
            queues[i] = new QueueNode();
        }
    }

    public SyncQueue(int condMax){
        queues = new QueueNode[condMax];

        for (int i = 0 ; i < condMax ; i++){ // to initialize the array
            queues[i] = new QueueNode();
        }
    }

    public int enqueueAndSleep(int condition){


        if (condition > 0 && condition < this.queues.length) return this.queues[condition].sleep();
        else return -1;

    }


    public void dequeueAndWakeup(int condition, int tid){

        if (condition > 0 && condition < this.queues.length) return this.queues[condition].wakeup(tid);

    }

    public void dequeueAndWakeup(int condition){

        dequeueAndWakeup(condition,DEFAULT_TID);

    }
}
