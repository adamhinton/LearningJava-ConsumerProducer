// never assume a thread is being woken up bc the condxn it's waiting on is changed. It could be woken up for a
// variety of reasons

package dev.lpa;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class MessageRepository {

    private String message;

    // Indicates to both threads wheterh they have work to do
    // When false, prod can pop shared message
    // when true, consumer can read it
    private boolean hasMessage = false;

    private final Lock lock = new ReentrantLock();

    public String read() {
        while (!hasMessage) {
            try {
                // Wait until hasMessage, I think
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // False again once message is retrieved
        hasMessage = false;
        // Wake up all threads waiting on this
        notifyAll();
        return message;
    }


    public synchronized void write(String message) {
        while (hasMessage) {
            // wait for msg to be read by consumer
            try{
                // wait for !hasMessage
                wait();
            }
            catch(InterruptedException e){
                throw new RuntimeException(e);
            }
        }

        hasMessage = true;
        // Notify any threads waiting for this
        notifyAll();
        this.message = message;
    }
}

// Producer
class MessageWriter implements Runnable {

    private MessageRepository outgoingMessage;

    private final String text = """
            Humpty Dumpty sat on a wall,
            Humpty Dumpty had a great fall,
            All the king's horses and all the king's men,
            Couldn't put Humpty together again.""";

    public MessageWriter(MessageRepository outgoingMessage) {
        this.outgoingMessage = outgoingMessage;
    }

    @Override
    public void run() {

        Random random = new Random();

        String[] lines = text.split("\n");

        for (int i = 0; i < lines.length; i++) {
            outgoingMessage.write(lines[i]);
            try {
                Thread.sleep(random.nextInt(500, 2000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        outgoingMessage.write("Finished");

    }
}


// Consumer
class MessageReader implements Runnable {

    private MessageRepository incomingMessage;

    public MessageReader(MessageRepository incomingMessage) {
        this.incomingMessage = incomingMessage;
    }

    @Override
    public void run() {

        Random random = new Random();
        String latestMessage = "";

        do {
            try {
                Thread.sleep(random.nextInt(500, 2000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            latestMessage = incomingMessage.read();
            System.out.println(latestMessage);
        }
        while (!latestMessage.equals("Finished"));

    }


}

public class Main {
    public static void main(String[] args) {

        MessageRepository messageRepository = new MessageRepository();

        // two threads - one for message reader and one for message writer

        Thread reader = new Thread(new MessageReader(messageRepository));

        Thread writer = new Thread(new MessageWriter(messageRepository));

        reader.start();
        writer.start();

    }
}