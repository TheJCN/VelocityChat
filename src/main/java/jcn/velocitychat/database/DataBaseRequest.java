package jcn.velocitychat.database;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
public class DataBaseRequest {
    private Connection connection;
    private ProxyServer server;

    public String getClanPefix(Player player) throws SQLException {

        String clanPrefix = null;

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM clans WHERE members LIKE ?");
        preparedStatement.setString(1, "%" + player.getUsername() + "%");
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()){
            clanPrefix = resultSet.getString("clanprefix");
        }

        resultSet.close();
        preparedStatement.close();

        return clanPrefix;
    }

    public boolean isPlayerInClan(Player player) throws SQLException {

        boolean isInClan = false;

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM clans WHERE members LIKE ?");
        preparedStatement.setString(1, player.getUsername());
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()){
            isInClan = true;
        }

        resultSet.clearWarnings();
        preparedStatement.close();

        return isInClan;
    }

    public List<String> getClanMembersByClanPrefix(String prefix) throws SQLException {

        List<String> clanMembers = new ArrayList<>();

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT members FROM clans WHERE clanprefix = ?");
        preparedStatement.setString(1, prefix);
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()){
            String membersString = resultSet.getString("members");
            String[] membersArray = membersString.split(",");
            for (String member : membersArray) {
                clanMembers.add(member);
            }
        }

        resultSet.close();
        preparedStatement.close();
        return clanMembers;
    }
}
