package src;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Sony Ping pong messaging");
        System.out.println("Enter the number of workers you would like to run: ");
        Scanner scanner = new Scanner(System.in); // I am using Scanner to take user input
        int amountWorkers = scanner.nextInt(); // this is where the amount of worker is configured
        List<Integer> ports = new ArrayList<>();  // this list is only use during the round robin pattern

        ExecutorService executor = Executors.newFixedThreadPool(amountWorkers); // I am using ExecutorService to create the worker separately and avoid a blocking interface

        for (int i = 0; i < amountWorkers; i++) {
            int port = 1025 + i;  // Assign unique port to each worker
            ports.add(port);
            WorkerNode worker = new WorkerNode("Worker" + ports.get(i), ports.get(i), ports, i);
            executor.submit(() -> worker.start());  // Submit worker to executor
        }

        MasterNode master = new MasterNode(ports);

        //this menu was made to prove that each pattern is working.
        while (true) {
            System.out.println("*********************************\n1 for one to one messaging \n2 for Broadcast \n3 for Round robin \n0 to Exit");
            int choice = scanner.nextInt();
            if (choice == 1) {
                master.sendPingToWorkers();
            } else if (choice == 2) {
                master.broadcastToWorkers();
            } else if (choice == 3) {
                master.startRoundRobin();
            } else if (choice == 0) {
                master.finishWorker();
                break;
            }
        }

        // Properly shut down the executor service
        executor.shutdown(); // This is where we send the signal to terminate the workers
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) { //this part is to wait 60 second for termination in case they still active
                executor.shutdownNow(); // after 60 second we send another termination signal
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        scanner.close();

    }
}
