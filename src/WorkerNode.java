package src;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;


public class WorkerNode {
    private String workerName;
    private Integer workerPort;


    public WorkerNode(String workerName, Integer workerPort) {
        this.workerName = workerName;
        this.workerPort = workerPort;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(workerPort)) {
            System.out.println(workerName + " is listening on port..." + workerPort);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                    String message = (String) in.readObject();
                    if (message == "ping") {
                        System.out.println("Received ping from master");
                        out.writeObject("pong");
                    } else if (message == "broadcast") {
                        System.out.println("Received broadcast from master");
                        out.writeObject("received broadcast");
                    } else if (message.startsWith("chain")) {
//                        System.out.println(workerName + " received chain message: " + message);
//                        message += " -> " + workerName;
//                        if (workerIndex < workerPorts.size() - 1) {
//                            try (Socket nextSocket = new Socket(workerPorts.get(workerIndex + 1), 12345);
//                                 ObjectOutputStream nextOut = new ObjectOutputStream(nextSocket.getOutputStream());
//                                 ObjectInputStream nextIn = new ObjectInputStream(nextSocket.getInputStream())) {
//
//                                nextOut.writeObject(message);
//                                String nextResponse = (String) nextIn.readObject();
//                                out.writeObject(nextResponse);
//                            }
//                        } else {
//                            out.writeObject(message);
//                        }
                    } else if (message == "exit") {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
