package org.example.Redis;

import org.example.Lottery;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.example.Main.reentrantLock;


public class RedisCheckRunnable implements Runnable{

    private RedisService redisService;
    private Set<String> ticketKeys;
    Lottery lottery = new Lottery();
    private List<Integer> luckyNumbers;
    private Set<String> staticListForProccesedKeys;
    private CountDownLatch latch;

    public RedisCheckRunnable() throws IOException, ClassNotFoundException {
    }

    public RedisCheckRunnable(RedisService redisService, Set<String> ticketKeys, List<Integer> luckyNumbers, Set<String> staticListForProccesedKeys, CountDownLatch latch) throws IOException, ClassNotFoundException {
        this.redisService = redisService;
        this.ticketKeys = ticketKeys;
        this.luckyNumbers = luckyNumbers;
        this.staticListForProccesedKeys = staticListForProccesedKeys;
        this.latch = latch;
    }

    @Override
    public void run() {
        for(String key : ticketKeys){
            reentrantLock.lock();
            if(staticListForProccesedKeys.contains(key)){
                reentrantLock.unlock();
            }else {
                staticListForProccesedKeys.add(key);
                reentrantLock.unlock();
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
        }
        latch.countDown();
    }
}
