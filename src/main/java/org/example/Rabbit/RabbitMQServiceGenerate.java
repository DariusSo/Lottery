package org.example.Rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.Ticket;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class RabbitMQServiceGenerate{

    private static final String QUEUE_NAME = "Tickets_queue";
    private static final String HOST = "localhost";
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;
    private int howMuchToGenerate;
    private Connection connection;
    private Channel channel;


    public RabbitMQServiceGenerate() throws IOException, TimeoutException {
        this.factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.objectMapper = new ObjectMapper();
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public void sendObjectToQueue(int howMuchToGenerate) throws Exception {
//        try (Connection connection = factory.newConnection();
//             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            for(int i = 0; i < howMuchToGenerate; i++){
                Random random = new Random();
                UUID uuid = UUID.randomUUID();
                List<Integer> ticketNumbers = new ArrayList<>();
                for(int j = 0; j < 5; j++){
                    ticketNumbers.add(random.nextInt(1,35));
                }
                Ticket ticket = new Ticket(String.valueOf(uuid), ticketNumbers);
                try {
                    String jsonMessage = objectMapper.writeValueAsString(ticket);
                    channel.basicPublish("", QUEUE_NAME, null, jsonMessage.getBytes());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }


//            String jsonMessage = objectMapper.writeValueAsString(obj);
//
//            channel.basicPublish("", QUEUE_NAME, null, jsonMessage.getBytes());
            //System.out.println("Issiustas JSON: " + jsonMessage);
        //}
    }

//    public void generateTickets(int howMuchToGenerate){
//        for(int i = 0; i < howMuchToGenerate; i++){
//            Random random = new Random();
//            UUID uuid = UUID.randomUUID();
//            List<Integer> ticketNumbers = new ArrayList<>();
//            for(int j = 0; j < 5; j++){
//                ticketNumbers.add(random.nextInt(1,35));
//            }
//            Ticket ticket = new Ticket(String.valueOf(uuid), ticketNumbers);
//            try {
//                sendObjectToQueue(ticket);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
}
