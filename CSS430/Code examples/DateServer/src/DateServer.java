/*
 * Created by Pouria Ghadimi
 */
import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class DateServer extends Thread
{

    private static int threadCount = 5; //number of maximum threads
    ServerSocket sock;

    public DateServer(ServerSocket sock){//create servers in the pool
        this.sock = sock;
    }

    public static void main(String []args)
    {

        try {

            //in order to have
            ServerSocket servSock = new ServerSocket(6013);
            DateServer servs[] = new DateServer[threadCount];
            System.out.println("Listening");

            // returns back a executor service
            ExecutorService threadExecutor = Executors.newCachedThreadPool();

            for (int i = 0; i<threadCount;i++){

                servs[i] = new DateServer(servSock);
                threadExecutor.execute(servs[i]);
            }
        }
        catch (IOException ioe){}



    }

    @Override
    public void run() {
        while (true) {

            try {

                Socket s = sock.accept();
                PrintWriter pout = new PrintWriter(s.getOutputStream(), true);
                pout.println(new java.util.Date().toString());
                s.close();
            }

            catch (IOException ioe) { }
        }
    }
}