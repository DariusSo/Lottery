package org.example.Redis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.example.Main.reentrantLock;
import static org.example.Main.toKnowHowMuch;

public class RedisGenerateRunnable implements Runnable{

    private final RedisService redisService;
    private final CountDownLatch latch;


    public RedisGenerateRunnable(RedisService redisService, CountDownLatch latch) throws IOException, ClassNotFoundException {
        this.redisService = redisService;
        this.latch = latch;
    }

    @Override
    public void run() {
        for(int i = 0; i < toKnowHowMuch;){
            reentrantLock.lock();
            toKnowHowMuch--;
            reentrantLock.unlock();
            Random random = new Random();
            UUID uuid = UUID.randomUUID();
            List<Integer> ticketNumbers = new ArrayList<>();
            for(int j = 0; j < 5; j++){
                ticketNumbers.add(random.nextInt(1,35));
            }
            try {
                redisService.put(String.valueOf(uuid), ticketNumbers);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e){
                toKnowHowMuch++;
            }
        }
        latch.countDown();
    }
}
