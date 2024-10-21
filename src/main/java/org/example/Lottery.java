package org.example;

import com.google.common.collect.Iterables;
import org.example.MySQL.MysqlRepository;
import org.example.MySQL.MysqlRepositoryPlay;
import org.example.Rabbit.RabbitMQServiceCheck;
import org.example.Rabbit.RabbitMQServiceGenerate;
import org.example.Redis.RedisCheck5Part;
import org.example.Redis.RedisCheckRunnable;
import org.example.Redis.RedisGenerateRunnable;
import org.example.Redis.RedisService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.example.Main.*;

public class Lottery {

    RedisService redisService = new RedisService("localhost", 6379);
    private RabbitMQServiceGenerate rabbitMQServiceGenerate;
    private RabbitMQServiceCheck rabbitMQServiceCheck;

    HashMap<UUID, List<Integer>> loterryTicketsHashMap = new HashMap<>();

    public static Set<String> staticListForProccesedKeys;
    public static Set<String> staticListForProccesedKeysSQL;

    public Lottery() throws IOException, ClassNotFoundException {

    }

    public void play() throws IOException, ClassNotFoundException {
        List<Integer> luckyNumbers = generateLuckyNumbers();

        Set<String> ticketKeys = redisService.getAllTicketsKeys();

        for(String key : ticketKeys){
            List<Integer> ticketNumbers = redisService.get(key);
            int matchingNumbers = checkTicket(luckyNumbers, ticketNumbers);
            declareResult(matchingNumbers, key);
        }

        System.out.println("Matched 1: " +howMuchMatched1);
        System.out.println("Matched 2: " +howMuchMatched2);
        System.out.println("Matched 3: " +howMuchMatched3);
        System.out.println("Matched 4: " +howMuchMatched4);
        System.out.println("Won Grand Prize: " +howMuchWonGrandPrize);

    }

    public int checkTicket(List<Integer> luckyNumbers, List<Integer> ticketNumbers){
        int matchingNumbers = 0;
        for(Integer i : ticketNumbers){
            for(Integer j : luckyNumbers){
                if(j == i){
                    i = 0;
                    matchingNumbers++;
                }
            }
        }
        return matchingNumbers;
    }

    public List<Integer> generateLuckyNumbers(){
        List<Integer> luckyNumbers = new ArrayList<>();
        Random random = new Random();
        for(int j = 0; j < 5; j++){
            luckyNumbers.add(random.nextInt(1, 35));
        }
        System.out.println("Lucky numbers: " + luckyNumbers);
        return luckyNumbers;
    }

    public void generateTickets(int numberOfTickets) throws IOException {

        for(int i = 0; i < numberOfTickets; i++){
            Random random = new Random();
            UUID uuid = UUID.randomUUID();
            List<Integer> ticketNumbers = new ArrayList<>();
            for(int j = 0; j < 5; j++){
                ticketNumbers.add(random.nextInt(1,35));
            }
            redisService.put(String.valueOf(uuid), ticketNumbers);
        }
    }

    public void declareResult(int matchingNumbers, String uuid){

        switch (matchingNumbers){
            case 0:
                break;
            case 1:
                System.out.println("Ticket UUID: " + uuid + " matched 1 number. Prize: " + " 0.50 EUR");
                howMuchMatched1++;
                break;
            case 2:
                System.out.println("Ticket UUID: " + uuid + " matched 2 numbers. Prize: " + " 3 EUR");
                howMuchMatched2++;
                break;
            case 3:
                System.out.println("Ticket UUID: " + uuid + " matched 3 numbers. Prize: " + " 15 EUR");
                howMuchMatched3++;
                break;
            case 4:
                System.out.println("Ticket UUID: " + uuid + " matched 4 numbers. Prize: " + " 500 EUR");
                howMuchMatched4++;
                break;
            case 5:
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!Ticket UUID: " + uuid + " matched 5 numbers. Prize: " + " 5000 EUR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                howMuchWonGrandPrize++;

                break;
        }

    }
    public void deleteDB(){
        redisService.deleteDB();
    }


    public void generateTicketForHashMap(int numberOfTickets){
        for(int i = 0; i < numberOfTickets; i++){
            Random random = new Random();
            UUID uuid = UUID.randomUUID();
            List<Integer> ticketNumbers = new ArrayList<>();
            for(int j = 0; j < 5; j++){
                ticketNumbers.add(random.nextInt(1,35));
            }
            loterryTicketsHashMap.put(uuid, ticketNumbers);
        }
    }

    public void playHashMap() throws IOException, ClassNotFoundException {
        List<Integer> luckyNumbers = generateLuckyNumbers();

        for (UUID key : loterryTicketsHashMap.keySet()) {
            int matchingNumbers = checkTicket(luckyNumbers, loterryTicketsHashMap.get(key));
            declareResult(matchingNumbers, String.valueOf(key));
        }

        System.out.println("Matched 1: " +howMuchMatched1);
        System.out.println("Matched 2: " +howMuchMatched2);
        System.out.println("Matched 3: " +howMuchMatched3);
        System.out.println("Matched 4: " +howMuchMatched4);
        System.out.println("Won Grand Prize: " +howMuchWonGrandPrize);

    }

    public void playWithRunnables() throws IOException, ClassNotFoundException, InterruptedException {
        List<Integer> luckyNumbers = generateLuckyNumbers();
        staticListForProccesedKeys = new HashSet<>();

        Set<String> ticketKeys = redisService.getAllTicketsKeys();

        CountDownLatch latch = new CountDownLatch(5);

        Thread thread = new Thread(new RedisCheckRunnable(redisService, ticketKeys, luckyNumbers, staticListForProccesedKeys, latch));
        thread.start();

        Thread thread2 = new Thread(new RedisCheckRunnable(redisService, ticketKeys, luckyNumbers, staticListForProccesedKeys, latch));
        thread2.start();

        Thread thread3 = new Thread(new RedisCheckRunnable(redisService, ticketKeys, luckyNumbers, staticListForProccesedKeys, latch));
        thread3.start();

        Thread thread4 = new Thread(new RedisCheckRunnable(redisService, ticketKeys, luckyNumbers, staticListForProccesedKeys, latch));
        thread4.start();

        Thread thread5 = new Thread(new RedisCheckRunnable(redisService, ticketKeys, luckyNumbers, staticListForProccesedKeys, latch));
        thread5.start();

        latch.await();

        System.out.println("Matched 1: " +howMuchMatched1);
        System.out.println("Matched 2: " +howMuchMatched2);
        System.out.println("Matched 3: " +howMuchMatched3);
        System.out.println("Matched 4: " +howMuchMatched4);
        System.out.println("Won Grand Prize: " +howMuchWonGrandPrize);

    }

    public void generateTicketsWithRunnables(int numberOfTickets) throws IOException, ClassNotFoundException, InterruptedException {

        toKnowHowMuch = numberOfTickets;
        CountDownLatch latchForTickets = new CountDownLatch(5);

        Thread thread = new Thread(new RedisGenerateRunnable(redisService, latchForTickets));
        thread.start();

        Thread thread1 = new Thread(new RedisGenerateRunnable(redisService, latchForTickets));
        thread1.start();

        Thread thread2 = new Thread(new RedisGenerateRunnable(redisService, latchForTickets));
        thread2.start();

        Thread thread3 = new Thread(new RedisGenerateRunnable(redisService, latchForTickets));
        thread3.start();

        Thread thread4 = new Thread(new RedisGenerateRunnable(redisService, latchForTickets));
        thread4.start();

        latchForTickets.await();
    }

    public void playWithRunnables5() throws IOException, ClassNotFoundException, InterruptedException {
        List<Integer> luckyNumbers = generateLuckyNumbers();
        staticListForProccesedKeys = new HashSet<>();

        Set<String> ticketKeys = redisService.getAllTicketsKeys();

        CountDownLatch latch5 = new CountDownLatch(5);

        Iterable<List<String>> lists = Iterables.partition(ticketKeys, 200);
        for(List<String> ls : lists){
            Thread thread = new Thread(new RedisCheck5Part(redisService, ls, luckyNumbers, latch5));
            thread.start();
        }


        latch5.await();

        System.out.println("Matched 1: " +howMuchMatched1);
        System.out.println("Matched 2: " +howMuchMatched2);
        System.out.println("Matched 3: " +howMuchMatched3);
        System.out.println("Matched 4: " +howMuchMatched4);
        System.out.println("Won Grand Prize: " +howMuchWonGrandPrize);

    }

    public void generateTicketsWithRunnablesMysql(int numberOfTickets) throws IOException, ClassNotFoundException, InterruptedException {

        toKnowHowMuchForMysql = numberOfTickets;
        CountDownLatch latchForTicketsMysql = new CountDownLatch(5);

        Thread thread = new Thread(new MysqlRepository(latchForTicketsMysql));
        thread.start();

        Thread thread1 = new Thread(new MysqlRepository(latchForTicketsMysql));
        thread1.start();

        Thread thread2 = new Thread(new MysqlRepository(latchForTicketsMysql));
        thread2.start();

        Thread thread3 = new Thread(new MysqlRepository(latchForTicketsMysql));
        thread3.start();

        Thread thread4 = new Thread(new MysqlRepository(latchForTicketsMysql));
        thread4.start();

        latchForTicketsMysql.await();
    }

    public void playWithRunnablesSQL() throws IOException, ClassNotFoundException, InterruptedException, SQLException {
        List<Integer> luckyNumbers = generateLuckyNumbers();
        staticListForProccesedKeysSQL = new HashSet<>();

        Set<String> ticketKeys = MysqlRepositoryPlay.getAllTicketsNames();

        CountDownLatch latchSQL = new CountDownLatch(5);

        Thread thread = new Thread(new MysqlRepositoryPlay(latchSQL, luckyNumbers, ticketKeys));
        thread.start();

        Thread thread2 = new Thread(new MysqlRepositoryPlay(latchSQL, luckyNumbers, ticketKeys));
        thread2.start();

        Thread thread3 = new Thread(new MysqlRepositoryPlay(latchSQL, luckyNumbers, ticketKeys));
        thread3.start();

        Thread thread4 = new Thread(new MysqlRepositoryPlay(latchSQL, luckyNumbers, ticketKeys));
        thread4.start();

        Thread thread5 = new Thread(new MysqlRepositoryPlay(latchSQL, luckyNumbers, ticketKeys));
        thread5.start();

        latchSQL.await();

        System.out.println("Matched 1: " +howMuchMatched1);
        System.out.println("Matched 2: " +howMuchMatched2);
        System.out.println("Matched 3: " +howMuchMatched3);
        System.out.println("Matched 4: " +howMuchMatched4);
        System.out.println("Won Grand Prize: " +howMuchWonGrandPrize);

    }
    public void generateTicketsRabbit(int numberOfTickets) throws Exception {

        rabbitMQServiceGenerate = new RabbitMQServiceGenerate();
        rabbitMQServiceGenerate.sendObjectToQueue(numberOfTickets);
    }

    public void playWithRabbit() throws Exception {
        List<Integer> luckyNumbers = generateLuckyNumbers();
        rabbitMQServiceCheck = new RabbitMQServiceCheck();
        rabbitMQServiceCheck.receiveAndProcessOneMessageAtATime(luckyNumbers);
    }

    public void generateTicketsPipeline(int numberOfTickets) throws IOException {

        redisService.pipeline(numberOfTickets);
    }

//    public void playPipeline() throws IOException, ClassNotFoundException {
//        redisService.pipelineGet();
//
//    }


}




