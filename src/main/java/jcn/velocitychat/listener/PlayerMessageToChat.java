package jcn.velocitychat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collection;
import java.util.EventListener;

@AllArgsConstructor
public class PlayerMessageToChat implements EventListener {
    private ProxyServer server;

    @Subscribe
    public void onPlayerWriteMessage(PlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        if (message.startsWith("/")) {
            return;
        }
        if (message.startsWith("!")){
            Collection<RegisteredServer> servers = server.getAllServers();
            for (RegisteredServer registeredServer : servers) {registeredServer.sendMessage(ChatFormatGlobal(message, player));}
        }
        else {player.getCurrentServer().get().getServer().sendMessage(ChatFormatLocal(message, player));
        }
        event.setResult(PlayerChatEvent.ChatResult.denied());
    }

    public Component ChatFormatGlobal(String message, Player player){
        Component formatedMessge = Component.text()
                .append(Component.text("Префикс "))
                .append(Component.text(player.getUsername(), NamedTextColor.GREEN))
                .append(Component.text(" Суффикс"))
                .append(Component.text( " > "))
                .append(Component.text(message))
                .build();
        return formatedMessge;
    }

    public Component ChatFormatLocal(String message, Player player){
        Component formatedMessge = Component.text()
                .append(Component.text("Префикс "))
                .append(Component.text(player.getUsername(), NamedTextColor.BLUE))
                .append(Component.text(" Суффикс"))
                .append(Component.text( " > "))
                .append(Component.text(message))
                .build();
        return formatedMessge;
    }
}