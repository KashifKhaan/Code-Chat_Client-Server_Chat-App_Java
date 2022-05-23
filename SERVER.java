import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SERVER {

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    public SERVER(){
        try {
            server = new ServerSocket(7778);
            System.out.println("SERVER IS READY TO ACCEPT CONNECTION");
            System.out.println("WAITING.....");
            socket = server.accept();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            startReading();
            startWriting();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void startReading(){
        // thread
        Runnable r1 = ()->{
            System.out.println("READER STARTED....");

            try{
            while(true){
                    String msg = br.readLine();
                    if (msg.equals("exit")){
                        System.out.println("CLIENT TERMINATED");
                        socket.close();
                        break;
                    }
                    System.out.println("Client: "+msg);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(r1).start();
    }

    public void startWriting(){
        System.out.println("WRITER STARTED");
        Runnable r2 = ()->{
            System.out.println("Writer Started...........");
            try{
            while(true){
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if (content.equals("exit")){
                        socket.close();
                        break;
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        new SERVER();
    }
}