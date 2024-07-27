package src;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter the number of workers you would like to run: ");
//        int workers = scanner.nextInt();
//        scanner.close();




        //** One to One messaging
//        List<String> workers = new ArrayList<>();
//        // Add worker addresses here (e.g., "localhost")
//        workers.add("localhost");
//        workers.add("localhost"); // Add more worker addresses as needed
//
//        MasterNode master = new MasterNode(workers);
//        master.sendPingToWorkers();

        //** Broadcast messaging
//        List<String> workers = new ArrayList<>();
//        // Add worker addresses here (e.g., "localhost")
//        workers.add("localhost");
//        workers.add("localhost"); // Add more worker addresses as needed
//
//        MasterNode master = new MasterNode(workers);
//        master.broadcastToWorkers();

        //**Round robin
//        List<String> workers = new ArrayList<>();
//        // Add worker addresses here (e.g., "localhost")
//        workers.add("localhost");
//        workers.add("localhost"); // Add more worker addresses as needed
//
//        MasterNode master = new MasterNode(workers);
//        //master.startRoundRobin();


        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Sony AI test");
        System.out.println("Enter the number of workers you would like to run: ");
        int amountWorkers = scanner.nextInt();
        List<Integer> ports = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(amountWorkers);

        for (int i = 0; i < amountWorkers; i++) {
            int port = 1025 + i;  // Assign unique port to each worker
            ports.add(port);
            WorkerNode worker = new WorkerNode("Worker" + ports.get(i), ports.get(i), ports, i);
            executor.submit(() -> worker.start());  // Submit worker to executor
        }

        MasterNode master = new MasterNode(ports);
        System.out.println("1 for one to one messaging \n2 for Broadcast \n3 for Round robin \n0 to Exit");

        while (true) {
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
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        scanner.close();

    }
}
