# SonyAITest_JN
made by: Juan Neuenschwander
# Distributed Ping-Pong System

This project implements a distributed system using Java. The system consists of a master node and multiple worker nodes. The master node can communicate with the worker nodes using three different messaging patterns: one-to-one, broadcasting, and round-robin.

## Features

1. **One-to-One Messaging**: The master node sends a "ping" message to each worker node, and each worker node responds with a "pong" message.
2. **Broadcasting**: The master node sends a "broadcast" message to all worker nodes simultaneously, and each worker node responds with a "received broadcast" message.
3. **Round-Robin Messaging**: The master node sends a "chain" message to the first worker node. Each worker node appends its name to the message and passes it to the next worker node. The last worker node sends the final message back to the master node.

## Requirements

- Java 11 or higher

## Setup and Usage

1. **Clone the repository**:

   ```bash
   git clone <repository-url>
   cd <repository-directory>
   
2. **Compile the Java files:**:
   ```bash
   javac -d bin src/*.java
3. **Run the application**:
   ```bash
   java -cp bin src.Main
4. **Interaction**:
   * When prompted, enter the number of worker nodes you would like to run.
   * After the workers are started, you can choose from the following options:
---
   * 1 for one-to-one messaging
   * 2 for broadcasting
   * 3 for round-robin messaging
   * 0 to exit
