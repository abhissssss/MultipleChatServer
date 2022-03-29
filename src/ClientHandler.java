import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private static final List<ClientHandler> clientHandlers = new ArrayList<>();
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private  String clientUsername;


    public ClientHandler(Socket socket) {
        Socket tempSocket;
        BufferedReader tempBufferedReader;
        BufferedWriter tempBufferedWriter;
        try{
            tempSocket = socket;
            tempBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            tempBufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = tempBufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER :" + clientUsername +"has entered the chat");
        }
        catch (IOException e){
//            closeEverything(socket, bufferedWriter , bufferedReader);
            tempSocket=null;
            tempBufferedReader=null;
            tempBufferedWriter=null;

        }
        this.socket=tempSocket;
        this.bufferedReader=tempBufferedReader;
        this.bufferedWriter=tempBufferedWriter;

    }

    @Override
    public void run() {
  String messageFromClient;
  while (socket.isConnected()){
      try {
          messageFromClient= bufferedReader.readLine();
          broadcastMessage (messageFromClient);
      }
      catch (IOException e) {
          closeEverything(socket,bufferedReader,bufferedWriter);
          break;
      }
  }
    }


    public void broadcastMessage (String messageToSend){
        for (ClientHandler clientHandler : clientHandlers){
            try{
                if (!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e){
                closeEverything (socket, bufferedReader, bufferedWriter);
            }
        }
    }


    public void removeClient (){
        clientHandlers.remove(this);
        broadcastMessage("SERVER:" + clientUsername + "has left the chat") ;
    }

    public void closeEverything (Socket socket ,BufferedReader bufferedReader, BufferedWriter bufferedWriter ){
        removeClient();
        try {
            if(bufferedReader!= null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket!=null){
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
