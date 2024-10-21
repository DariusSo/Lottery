package org.example.Redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.*;

import java.io.*;
import java.util.*;

public class RedisService {

    private final JedisPool jedisPool;

    public RedisService(String host, int port) {
        this.jedisPool = new JedisPool(host, port);
    }

    public void put(String key, Object value) throws IOException {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key.getBytes(), serialize(value));
        }
    }

    public List<Integer> get(String key) throws IOException, ClassNotFoundException {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] data = jedis.get(key.getBytes());
            if (data != null) {
                return deserialize(data);
            }
            return null;
        }
    }
    public Set<String> getAllTicketsKeys() throws IOException, ClassNotFoundException {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("*");

            return keys;
        }
    }

    public void pipeline(int numberOfTickets) throws IOException {
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline p = jedis.pipelined();
            for(int i = 0; i < numberOfTickets; i++){
                Random random = new Random();
                UUID uuid = UUID.randomUUID();
                List<Integer> ticketNumbers = new ArrayList<>();
                for(int j = 0; j < 5; j++){
                    ticketNumbers.add(random.nextInt(1,35));
                }
                p.sadd(String.valueOf(uuid), String.valueOf(ticketNumbers));

            }
            //p.sync();
        }
    }

//    public void pipelineGet() throws IOException, ClassNotFoundException {
//        Lottery lottery = new Lottery();
//        List<Integer> luckyNumbers = lottery.generateLuckyNumbers();
//        List<Response> responses = new ArrayList<>();
//        try (Jedis jedis = jedisPool.getResource()) {
//            Pipeline p = jedis.pipelined();
//            Set<String> ticketKeys = getAllTicketsKeys();
//            for(String ticketKey : ticketKeys){
//                responses.add(p.get(ticketKey));
//
//                //int matchingNumbers = lottery.checkTicket(luckyNumbers ,deserialize(ticketPipeline.get().getBytes()));
//                //lottery.declareResult(matchingNumbers, ticketKey);
//            }
//            for(Response r : responses){
//                r.get();
//                int matchingNumbers = lottery.checkTicket(luckyNumbers , (List<Integer>) r.get());
//                lottery.declareResult(matchingNumbers, "test");
//            }
//        }
//    }

    private byte[] serialize(Object obj) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        byte[] json = mapper.writeValueAsString(obj).getBytes();
        return json;
    }

    private List<Integer> deserialize(byte[] data) throws IOException, ClassNotFoundException {

        ObjectMapper mapper = new ObjectMapper();
        List<Integer> obj = mapper.readValue(data, List.class);
        return obj;
    }
    public void deleteDB(){
        Jedis jedis = jedisPool.getResource();
        jedis.flushDB();
    }

    public void close() {
        jedisPool.close();
    }






}
