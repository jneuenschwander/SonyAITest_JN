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

    // I implemented to constructor to deal with round robin
    public WorkerNode(String workerName, Integer workerPort) {
        this.workerName = workerName; // worker name will always be  worker + the port number
        this.workerPort = workerPort; // the port always going to start from 1025 since from 0 to 1024 are only for root access
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
            // this line is to ensure the worker is online
            System.out.println(workerName + " is listening on port " + workerPort);
            // this while true is to ensure it detect the signal ping, broadcast, chain and exit.
            while (true) {
                try (Socket socket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); // the signal will be store in this var
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) { // this is where the output will go

                    String message = (String) in.readObject();
                    System.out.println(workerName + " received message: " + message);

                    if ("ping".equals(message)) {
                        System.out.println(workerName + " received ping from master");
                        out.writeObject("pong"); // this is where I send the response.
                        out.flush(); //to ensure data is sent
                    } else if ("broadcast".equals(message)) {
                        System.out.println(workerName + " received broadcast from master");
                        out.writeObject("received broadcast"); // this is where I send the response.
                        out.flush(); //to ensure data is sent
                    } else if (message.startsWith("chain")) {
                        message += " -> " + workerName; // for the chain I will first save the worker name
                        System.out.println(workerName + " updated chain message: " + message);

                        if (workerIndex < workerPorts.size() - 1) { // this is to check we don't surpass the workerPorts.
                            int nextWorkerPort = workerPorts.get(workerIndex + 1);
                            try (Socket nextSocket = new Socket("localhost", nextWorkerPort); // this is to route the message to the next worker
                                 ObjectOutputStream nextOut = new ObjectOutputStream(nextSocket.getOutputStream());
                                 ObjectInputStream nextIn = new ObjectInputStream(nextSocket.getInputStream())) {

                                nextOut.writeObject(message); // send the message
                                nextOut.flush(); //to ensure data is sent

                                String nextResponse = (String) nextIn.readObject();
                                out.writeObject(nextResponse);
                                out.flush();
                            }
                        } else {
                            out.writeObject(message);
                            out.flush();
                        }

                    } else if ("exit".equals(message)) {
                        System.out.println(workerName + " exiting"); // this block is to ensure every worker exit the loop.
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
