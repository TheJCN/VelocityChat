package jcn.velocitychat.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import jcn.velocitychat.database.DataBaseRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class ClanMessage implements SimpleCommand {
    private ProxyServer server;
    private Connection connection;

    @SneakyThrows
    @Override
    public void execute(Invocation invocation) {

        String clanPrefix = null;
        List<String> clanMember;

        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        Player sender = (Player) source;

        DataBaseRequest request = new DataBaseRequest(connection, server);

        if (request.isPlayerInClan(sender)) {
            return;
        }

        StringBuilder message = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            message.append(args[i]);
        }

        clanPrefix = request.getClanPefix(sender);

        clanMember = request.getClanMembersByClanPrefix(request.getClanPefix(sender));

        for (String player : clanMember) {
            Optional<Player> getter = server.getPlayer(player);
            getter.get().sendMessage(ClanMessage(sender, message.toString(), clanPrefix));
        }
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        return List.of();
    }


    @Override
    public boolean hasPermission(Invocation invocation) {
        return true;
    }

    public Component ClanMessage(Player sender, String message, String clanPrefix) {
        Component formattedMessage = Component.text()
                .append(Component.text("["))
                .append(Component.text(clanPrefix))
                .append(Component.text("]"))
                .append(Component.text(sender.getUsername(), NamedTextColor.GOLD))
                .append(Component.text(" : "))
                .append(Component.text(message))
                .build();
        return formattedMessage;
    }
}
