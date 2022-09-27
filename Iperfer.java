import java.io.IOException;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.net.ServerSocket;



public class Iperfer {
    public static void main(String[] args) throws IOException {

        if(args.length < 2){
            System.err.println("Error: invalid arguments");
            System.exit(1);
        }
        boolean clientCheck = false, serverCheck = false;
        // System.out.println(args[0].equals("-c"));
        if(args[0].equals("-c")) {clientCheck = true;}
        

        else if(args[0].equals("-s")) {serverCheck = true;}

        else {
            System.err.println("Error: invalid arguments");
            System.exit(1);
        }

        if(clientCheck){

            //client side call handling
            
            String hostName = args[2];
        	int portNumber = Integer.parseInt(args[4]);
            System.out.println(portNumber);

        	int time = Integer.parseInt(args[6]);
            if(args.length > 7 || (portNumber >= 65535 || portNumber <= 1024)) { System.err.println("Error: invalid arguments");
            System.exit(1); }

        	client(hostName, portNumber, time);

        }
        else{
            if(args.length > 3) {System.err.println("Error: invalid arguments");
            System.exit(1);}
            int portNumber = Integer.parseInt(args[2]);
        	server(portNumber);

            //server side call handling

        }
        
    }


    public static void client(String hostName, int portNumber, int time){


        try (
            Socket tcpsock = new Socket(hostName, portNumber);
            PrintWriter out =
            new PrintWriter(tcpsock.getOutputStream(), true);
            BufferedReader in =
            new BufferedReader(
                new InputStreamReader(tcpsock.getInputStream()));
    ) 
        {
            long totalTime = (long) (time*Math.pow(10,9));
    		long startTime = System.nanoTime();
    		boolean toFinish = false;
    		long totalNumberOfBytes = 0;
    		while(!toFinish){
	        	byte[] dataChunk = new byte[1000];
	        	totalNumberOfBytes+=(long)1000;
	        	Arrays.fill(dataChunk, (byte)0);
	            out.println(dataChunk);
	            in.readLine();
	            toFinish = (System.nanoTime() - startTime >= totalTime);
    		}
    		int sentInKB = (int) (totalNumberOfBytes/1024);
    		long rate = (totalNumberOfBytes/(long)Math.pow(2,20 ))/time;
    		System.out.print("sent="+sentInKB+"KB rate="+rate+"Mbps");




        } catch (UnknownHostException e) {
            System.err.println("Host Unknown ::  " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } 
    }

    public static void server(int portNumber){
        long totalSize = 0;
    long startTime = 0;
	try (
            ServerSocket serverSocket =
                new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();     
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);                   
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String inputLine;
            boolean firstTime = true;
            while ((inputLine = in.readLine()) != null) {
            	if(firstTime){
            		startTime = System.nanoTime();
            		firstTime = false;
            	}
            	totalSize+=1000;
                out.println(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }finally{
        	int recievedInKB = (int) (totalSize/1024);
        	long time = System.nanoTime() - startTime;
    		long rate = (long) ((totalSize/(long)Math.pow(2,20 ))/(time/Math.pow(10,9)));
    		System.out.print("sent="+recievedInKB+"KB rate="+rate+"Mbps");
        }
    }
    

}
