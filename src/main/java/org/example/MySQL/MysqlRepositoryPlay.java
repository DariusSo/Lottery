package org.example.MySQL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Lottery;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.example.Lottery.staticListForProccesedKeysSQL;
import static org.example.Main.reentrantLock;

public class MysqlRepositoryPlay implements Runnable{

    private static final String URL = "jdbc:mysql://localhost:3306/lottery";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private CountDownLatch latch;
    private static Connection connection;
    Lottery lottery = new Lottery();
    private List<Integer> luckyNumbers;

    private Set<String> ticketKeys;

    static {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public MysqlRepositoryPlay(CountDownLatch latch, List<Integer> luckyNumbers, Set<String> ticketKeys) throws IOException, ClassNotFoundException {
        this.latch = latch;
        this.luckyNumbers = luckyNumbers;
        this.ticketKeys = ticketKeys;
    }

    public static Set<String> getAllTicketsNames() throws SQLException {
        Set<String> uuidList = new HashSet<>();
        String sql = "SELECT uuid FROM tickets";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            uuidList.add(rs.getString("uuid"));
        }
        return uuidList;
    }

    public List<Integer> getTicket(String uuid) throws SQLException, JsonProcessingException {
        List<Integer> ticketNumber = new ArrayList<>();
        String sql = "SELECT ticket_numbers FROM tickets WHERE uuid = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, uuid);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            ObjectMapper mapper = new ObjectMapper();
            ticketNumber = mapper.readValue(rs.getString("ticket_numbers"), List.class);
        }
        return ticketNumber;
    }

    @Override
    public void run() {
        for(String key : ticketKeys){
            reentrantLock.lock();
            if(staticListForProccesedKeysSQL.contains(key)){
                reentrantLock.unlock();
            }else {
                staticListForProccesedKeysSQL.add(key);
                reentrantLock.unlock();
                List<Integer> ticketNumbers = null;
                try {
                    try {
                        ticketNumbers = getTicket(String.valueOf(key));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } catch (IOException e) {
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
