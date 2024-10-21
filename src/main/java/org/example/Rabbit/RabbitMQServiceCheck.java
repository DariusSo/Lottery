package org.example.Rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.Lottery;
import org.example.Ticket;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RabbitMQServiceCheck{
    private static final String QUEUE_NAME = "Tickets_queue";
    private static final String HOST = "localhost";
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;
    Lottery lottery = new Lottery();


    private long recievedCount = 0;

    public RabbitMQServiceCheck() throws IOException, ClassNotFoundException {
        this.factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.objectMapper = new ObjectMapper();
    }
    public void receiveAndProcessOneMessageAtATime(List<Integer> luckyNumbers) throws Exception{
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            //channel.basicQos(1);
            AtomicInteger messageCount = new AtomicInteger((int) channel.messageCount(QUEUE_NAME));

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String jsonMessage = new String(delivery.getBody(), "UTF-8");
                try {
                    Ticket ticket = objectMapper.readValue(jsonMessage, Ticket.class);
                    int matchingNumbers = lottery.checkTicket(luckyNumbers, ticket.getTickets());
                    lottery.declareResult(matchingNumbers, ticket.getUuid());
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    messageCount.getAndDecrement();

                }catch (Exception e){
                    e.printStackTrace();

                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                }
            };
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {});
            while(messageCount.get() != 0) {
                Thread.sleep(10);
            }
        }
    }
}
