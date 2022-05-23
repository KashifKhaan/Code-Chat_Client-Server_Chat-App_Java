import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CLIENT {
    Socket socket;
    BufferedReader br;
    PrintWriter out;
    public CLIENT(){
        try {
            System.out.println("Sanding Request To Server...");
            socket = new Socket("127.0.0.1",7778);
            System.out.println("Connection Established");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            startReading();
            startWriting();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startReading(){
        // thread
        Runnable r1 = ()->{
            System.out.println("READER STARTED....");
            while(true){
                try {
                    String msg = br.readLine();
                    if (msg.equals("exit")){
                        System.out.println("SERVER TERMINATED");
                        socket.close();
                        break;
                    }
                    System.out.println("SERVER: "+msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r1).start();
    }

    public void startWriting(){
        System.out.println("WRITER STARTED");
        Runnable r2 = ()->{
            System.out.println("Writer Started...........");
            while(true){
                try {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();

                    if (content.equals("exit")){
                        socket.close();
                        break;
                    }
                } catch(Exception e){

                }
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is Client.....");
        new CLIENT();
    }
}