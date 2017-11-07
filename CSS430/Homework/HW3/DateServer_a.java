/*
 * Created by Pouria Ghadimi
 */
import java.net.*;
import java.io.*;


public class DateServer extends Thread
{
    Socket sock;
    DateServer(Socket sock){

        this.sock = sock;
    }

    public static void main(String []args)
    {

        try {

            //it should be in a try catch statement
            ServerSocket servSock = new ServerSocket(6013);
            System.out.println("Listening");

            while (true)
            {
                Socket client = servSock.accept();
                System.out.println("Connected");
                new Thread (new DateServer(client)).start();

            }
        }
        catch (IOException ioe){}



    }

    @Override
    public void run() {
        try {

            PrintWriter pout = new PrintWriter(sock.getOutputStream(),true);
            pout.println(new java.util.Date().toString());
            sock.close();
        }
        catch (IOException ioe){}
    }
}
