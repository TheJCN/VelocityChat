package jcn.velocitychat.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@AllArgsConstructor
public class DataBaseManager {
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    @Getter
    private Connection connection;

    public DataBaseManager(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this. database = database;
        this.username = username;
        this.password = password;
    }


    public boolean connect() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
        return true;
    }
}
