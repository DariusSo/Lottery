package org.example.Redis;

import org.example.Lottery;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RedisCheck5Part implements Runnable{

    private RedisService redisService;
    private List<String> ticketKeys;
    Lottery lottery = new Lottery();
    private List<Integer> luckyNumbers;
    private CountDownLatch latch;

    public RedisCheck5Part(RedisService redisService, List<String> ticketKeys, List<Integer> luckyNumbers, CountDownLatch latch) throws IOException, ClassNotFoundException {
        this.redisService = redisService;
        this.ticketKeys = ticketKeys;
        this.luckyNumbers = luckyNumbers;
        this.latch = latch;
    }

    @Override
    public void run() {
        for(String key : ticketKeys){

            List<Integer> ticketNumbers = null;
            try {
                ticketNumbers = redisService.get(key);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            int matchingNumbers = lottery.checkTicket(luckyNumbers, ticketNumbers);
            lottery.declareResult(matchingNumbers, key);

            //System.out.println(Thread.currentThread().getName());

        }
        latch.countDown();
    }
}
