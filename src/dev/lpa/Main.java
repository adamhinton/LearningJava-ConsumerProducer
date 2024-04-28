package dev.lpa;

import java.util.Random;

// Consumer
class MessageRepository{

    private String message;

    // Indicates to both threads wheterh they have work to do
    // When false, prod can pop shared message
    // when true, consumer can read it
    private boolean hasMessage = false;

    public synchronized String read(){
        while (!hasMessage){
            // empty
        }

        // False again once message is retrieved
        hasMessage = false;

        return message;
    }


    public synchronized void write (String message){
        while (hasMessage){
            //empty
            // wait for msg to be read by consumer
        }

        hasMessage = true;
        this.message = message;
    }
}

// Producer
class MessageWriter implements Runnable{

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

        for (int i=0; i< lines.length; i++){
            outgoingMessage.write(lines[i]);
            try{
                Thread.sleep(random.nextInt(500, 2000));
            }
            catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }
        outgoingMessage.write("Finished");

    }
}

public class Main {
    public static void main(String[] args) {



    }
}