package org.example.MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.example.Main.*;

public class MysqlRepository implements Runnable{

    private static final String URL = "jdbc:mysql://localhost:3306/lottery";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private CountDownLatch latch;
    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public MysqlRepository(CountDownLatch latch) {
        this.latch = latch;
    }

    public void saveTicket(UUID uuid, String ticketNumbers) throws SQLException {

        String sql = "INSERT INTO tickets (uuid, ticket_numbers) VALUES (?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, String.valueOf(uuid));
        ps.setString(2, ticketNumbers);
        ps.execute();
    }

    @Override
    public void run() {
        for(int i = 0; i < toKnowHowMuchForMysql;){
            reentrantLock.lock();
            toKnowHowMuchForMysql--;
            reentrantLock.unlock();
            Random random = new Random();
            UUID uuid = UUID.randomUUID();
            List<Integer> ticketNumbers = new ArrayList<>();
            for(int j = 0; j < 5; j++){
                ticketNumbers.add(random.nextInt(1,35));
            }
            try {
                saveTicket(uuid, String.valueOf(ticketNumbers));
                //System.out.println(Thread.currentThread().getName());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        latch.countDown();
    }
}
