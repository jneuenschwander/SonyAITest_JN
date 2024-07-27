package src;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MasterNode {
    private List<Integer> workerPorts; //this for the master node to keep track of all the workers


    public MasterNode(List<Integer> workersPorts) {
        this.workerPorts = workersPorts;
    }

    public void sendPingToWorkers() {
        for (Integer workerPort : workerPorts) { // this loop will send a signal to all port related to a worker.
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

        for (Integer workerPort : workerPorts) { // this loop will send a signal to all port related to a worker.
            Thread thread = new Thread(() -> { //this is block was made to ensure the broadcasting didn't follow a secuencial process
                try (Socket socket = new Socket("localhost", workerPort); // every thread will send a message of broadcasting to a worker
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

        // Wait for all threads to complete, Avoiding IOEException
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startRoundRobin() {
        String message = "chain"; // this is the signal each worker will take for the round robin pattern.
        try (Socket socket = new Socket("localhost", workerPorts.get(0));
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(message); // this where the message is being sent.
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

                out.writeObject("exit"); //this is the signal to tell the worker to quit looping.

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
