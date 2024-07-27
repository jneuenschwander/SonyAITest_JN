package src;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MasterNode {
    private List<Integer> workerPorts;


    public MasterNode(List<Integer> workersPorts) {
        this.workerPorts = workersPorts;
    }

    public void sendPingToWorkers() {
        for (Integer workerPort : workerPorts) {
            try (Socket socket = new Socket("localhost", workerPort);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject("ping");
                out.flush();
                String response = (String) in.readObject();

                System.out.println("Received response from port" + workerPorts + ": " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastToWorkers() {
        List<Thread> threads = new ArrayList<>();

        for (Integer workerPort : workerPorts) {
            Thread thread = new Thread(() -> {
                try (Socket socket = new Socket("localhost", workerPort);
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                    System.out.println("Sending broadcast to worker on port " + workerPort);
                    out.writeObject("broadcast");
                    out.flush(); // Ensure data is sent

                    String response = (String) in.readObject();
                    System.out.println("Received response from port " + workerPort + ": " + response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startRoundRobin() {
        String message = "chain";
        try (Socket socket = new Socket("localhost", workerPorts.get(0));
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(message);
            String response = (String) in.readObject();
            System.out.println("Received final message from workers: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void finishWorker(){
        for (Integer workerPort : workerPorts) {
            try (Socket socket = new Socket("localhost", workerPort);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject("exit");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
