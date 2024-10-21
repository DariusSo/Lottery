package org.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static int howMuchWonGrandPrize = 0;
    public static int howMuchMatched1 = 0;
    public static int howMuchMatched2 = 0;
    public static int howMuchMatched3 = 0;
    public static int howMuchMatched4 = 0;

    public static int toKnowHowMuch = 0;
    public static int toKnowHowMuchForMysql = 0;

    public static ReentrantLock reentrantLock = new ReentrantLock();


    public static void main(String[] args) throws Exception {

        Lottery lottery = new Lottery();
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Choose: 1-Generate tickets | 2-Play | 0-Exit | 9-Delete tickets");
            int generateOrPlay = scanner.nextInt();
            switch (generateOrPlay){
                case 0: System.exit(0);
                case 1:
                    System.out.println("How many tickets for Redis pipeline?");
                    int ticketQuantityForPipeline = scanner.nextInt();
                    long startTimePipeline = System.nanoTime();
                    lottery.generateTicketsPipeline(ticketQuantityForPipeline);
                    long estimatedTimePipeline = System.nanoTime() - startTimePipeline;

                    System.out.println("How many tickets for redis?");
                    int ticketQuantity = scanner.nextInt();
                    long startTime = System.nanoTime();
                    lottery.generateTickets(ticketQuantity);
                    long estimatedTime = System.nanoTime() - startTime;

                    System.out.println("How many tickets for HashMap?");
                    int ticketQuantityForHashMap = scanner.nextInt();
                    long startTimeHash = System.nanoTime();
                    lottery.generateTicketForHashMap(ticketQuantityForHashMap);
                    long estimatedTimeHash = System.nanoTime() - startTimeHash;

                    System.out.println("How many tickets for Redis with Runnables?");
                    int ticketQuantityForR = scanner.nextInt();
                    long startTimeR = System.nanoTime();
                    lottery.generateTicketsWithRunnables(ticketQuantityForR);
                    long estimatedTimeR = System.nanoTime() - startTimeR;

                    System.out.println("How many tickets for Mysql?");
                    int ticketQuantityForMysql = scanner.nextInt();
                    long startTimeSQL = System.nanoTime();
                    lottery.generateTicketsWithRunnablesMysql(ticketQuantityForMysql);
                    long estimatedTimeSQL = System.nanoTime() - startTimeSQL;

                    System.out.println("How many tickets for Rabbit?");
                    int ticketQuantityForRabbit = scanner.nextInt();
                    long startTimeRabbit = System.nanoTime();
                    lottery.generateTicketsRabbit(ticketQuantityForRabbit);
                    long estimatedTimeRabbit = System.nanoTime() - startTimeRabbit;

                    System.out.println("Redis pipeline: " + TimeUnit.MILLISECONDS.convert(estimatedTimePipeline, TimeUnit.NANOSECONDS) + "ms");
                    System.out.println("Redis time for generating tickets: " + TimeUnit.MILLISECONDS.convert(estimatedTime, TimeUnit.NANOSECONDS) + "ms");
                    System.out.println("HashMap: " + TimeUnit.MILLISECONDS.convert(estimatedTimeHash, TimeUnit.NANOSECONDS) + "ms");
                    System.out.println("Runnable: " + TimeUnit.MILLISECONDS.convert(estimatedTimeR, TimeUnit.NANOSECONDS) + "ms");
                    System.out.println("SQL: " + TimeUnit.MILLISECONDS.convert(estimatedTimeSQL, TimeUnit.NANOSECONDS) + "ms");
                    System.out.println("Rabbit: " + TimeUnit.MILLISECONDS.convert(estimatedTimeRabbit, TimeUnit.NANOSECONDS) + "ms");

                    break;
                case 2:
                    long startTimePlay = System.nanoTime();
                    lottery.play();
                    long estimatedTimePlay = System.nanoTime() - startTimePlay;

                    long startTimePlayHash = System.nanoTime();
                    lottery.playHashMap();
                    long estimatedTimePlayHash = System.nanoTime() - startTimePlayHash;

                    long startTimePlayR = System.nanoTime();
                    lottery.playWithRunnables();
                    long estimatedTimePlayR = System.nanoTime() - startTimePlayR;

                    long startTimePlay5 = System.nanoTime();
                    lottery.playWithRunnables5();
                    long estimatedTimePlay5 = System.nanoTime() - startTimePlay5;

                    long startTimePlaySQL = System.nanoTime();
                    lottery.playWithRunnablesSQL();
                    long estimatedTimePlaySQL = System.nanoTime() - startTimePlaySQL;

                    long startTimePlayRabbit = System.nanoTime();
                    lottery.playWithRabbit();
                    long estimatedTimePlayRabbit = System.nanoTime() - startTimePlayRabbit;

//                    long startTimePlayPipline = System.nanoTime();
//                    lottery.playPipeline();
//                    long estimatedTimePlayRabbit = System.nanoTime() - startTimePlayPipline;

                    System.out.println("Redis time for checking tickets: " + TimeUnit.MILLISECONDS.convert(estimatedTimePlay, TimeUnit.NANOSECONDS) + "ms");
                    System.out.println("HashMap: " + TimeUnit.MILLISECONDS.convert(estimatedTimePlayHash, TimeUnit.NANOSECONDS) + "ms");
                    System.out.println("Runnables: " + TimeUnit.MILLISECONDS.convert(estimatedTimePlayR, TimeUnit.NANOSECONDS) + "ms");
                    System.out.println("Runnables5: " + TimeUnit.MILLISECONDS.convert(estimatedTimePlay5, TimeUnit.NANOSECONDS) + "ms");
                    System.out.println("SQL: " + TimeUnit.MILLISECONDS.convert(estimatedTimePlaySQL, TimeUnit.NANOSECONDS) + "ms");
                    System.out.println("Rabbit: " + TimeUnit.MILLISECONDS.convert(estimatedTimePlayRabbit, TimeUnit.NANOSECONDS) + "ms");
                    break;
                case 9:
                    lottery.deleteDB();
                    break;
            }
            howMuchWonGrandPrize = 0;
            howMuchMatched1 = 0;
            howMuchMatched2 = 0;
            howMuchMatched3 = 0;
            howMuchMatched4 = 0;
        }
    }
}