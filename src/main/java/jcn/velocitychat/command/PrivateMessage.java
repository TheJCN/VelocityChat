package jcn.velocitychat.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class PrivateMessage implements SimpleCommand {
    private Logger logger;
    private ProxyServer server;

    @Override
    public void execute(Invocation invocation) {

        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        Player sender = (Player) source;
        Optional<Player> target = server.getPlayer(args[0]);
        System.out.println(args[0]);

        StringBuilder message = new StringBuilder();

        for (int i = 1; i < args.length; i++){
            message.append(args[i]);
        }

        if(target.isPresent()){
            target.get().sendMessage(PrivateMessageFormatToTarget(sender, target, message.toString()));
            sender.sendMessage(PrivateMessageFormatToSender(sender, target, message.toString()));
        }
        else {
            sender.sendMessage(Component.text("Ошибка! Данный игрок не оффлайн").color(NamedTextColor.RED));
        }

    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> playerListCompleter = new ArrayList<>();
        for(Player player : server.getAllPlayers()){
            playerListCompleter.add(player.getUsername());
        }
        return playerListCompleter;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true;
    }

    public Component PrivateMessageFormatToSender(Player sender, Optional<Player> target, String message){
        Component formatedMessge = Component.text()
                .append(Component.text(sender.getUsername(), NamedTextColor.GOLD))
                .append(Component.text(" -> "))
                .append(Component.text(target.get().getUsername(), NamedTextColor.GOLD))
                .append(Component.text(" : "))
                .append(Component.text(message))
                .build();
        return formatedMessge;
    }

    public Component PrivateMessageFormatToTarget(Player sender, Optional<Player> target, String message){
        Component formatedMessge = Component.text()
                .append(Component.text(target.get().getUsername(), NamedTextColor.GOLD))
                .append(Component.text(" -> "))
                .append(Component.text(sender.getUsername(), NamedTextColor.GOLD))
                .append(Component.text(" : "))
                .append(Component.text(message))
                .build();
        return formatedMessge;
    }
}
