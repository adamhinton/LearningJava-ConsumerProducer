package dev.lpa;

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

public class Main {
    public static void main(String[] args) {



    }
}