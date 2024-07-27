package src;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;


public class WorkerNode extends Thread {
    private String workerName;
    private Integer workerPort;
    private List<Integer> workerPorts;
    private int workerIndex;


    public WorkerNode(String workerName, Integer workerPort) {
        this.workerName = workerName;
        this.workerPort = workerPort;
    }
    public WorkerNode(String workerName, Integer workerPort, List<Integer> workerPorts, int workerIndex) {
        this.workerName = workerName;
        this.workerPort = workerPort;
        this.workerPorts = workerPorts;
        this.workerIndex = workerIndex;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(workerPort)) {
            System.out.println(workerName + " is listening on port " + workerPort);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                    String message = (String) in.readObject();
                    System.out.println(workerName + " received message: " + message);

                    if ("ping".equals(message)) {
                        System.out.println(workerName + " received ping from master");
                        out.writeObject("pong");
                        out.flush(); // Ensure data is sent
                    } else if ("broadcast".equals(message)) {
                        System.out.println(workerName + " received broadcast from master");
                        out.writeObject("received broadcast");
                        out.flush(); // Ensure data is sent
                    } else if (message.startsWith("chain")) {
                        message += " -> " + workerName;
                        System.out.println(workerName + " updated chain message: " + message);

                        if (workerIndex < workerPorts.size() - 1) {
                            int nextWorkerPort = workerPorts.get(workerIndex + 1);
                            try (Socket nextSocket = new Socket("localhost", nextWorkerPort);
                                 ObjectOutputStream nextOut = new ObjectOutputStream(nextSocket.getOutputStream());
                                 ObjectInputStream nextIn = new ObjectInputStream(nextSocket.getInputStream())) {

                                nextOut.writeObject(message);
                                nextOut.flush();

                                String nextResponse = (String) nextIn.readObject();
                                out.writeObject(nextResponse);
                                out.flush();
                            }
                        } else {
                            out.writeObject(message);
                            out.flush();
                        }

                    } else if ("exit".equals(message)) {
                        System.out.println(workerName + " exiting");
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
