package jcn.velocitychat;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.route.Route;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import jcn.velocitychat.command.ClanMessage;
import jcn.velocitychat.command.PrivateMessage;
import jcn.velocitychat.database.DataBaseManager;
import jcn.velocitychat.listener.PlayerMessageToChat;
import lombok.Getter;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

@Getter
@Plugin(
        id = "velocitychat",
        name = "VelocityChat",
        version = "1.0"
)
public class VelocityChat {
    private final ProxyServer server;
    private Logger logger;
    private YamlDocument config;
    @Inject
    private @DataDirectory Path dataDirectory;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private DataBaseManager dataBaseManager;
    private Connection connection;

    @Inject
    public VelocityChat(ProxyServer server) {
        this.server = server;

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException, SQLException, ClassNotFoundException {
        config = YamlDocument.create(new File(dataDirectory.toFile(), "config.yml"),
                getClass().getResourceAsStream("/config.yml"),
                GeneralSettings.DEFAULT,
                LoaderSettings.builder().setAutoUpdate(true).build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.builder().
                        setVersioning(new BasicVersioning("file-version")).
                        setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build());

                config.update();
                config.save();

        if(!setupDatabase()){
            logger.info("Подключение к базе данных отсутсвует");
        }

        CommandManager commandManager = server.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("pm")
                .aliases("tell")
                .aliases("w")
                .aliases("msg")
                .build();
        SimpleCommand simpleCommand = new PrivateMessage(logger, server);
        commandManager.register(commandMeta, simpleCommand);

        CommandManager commandManager2 = server.getCommandManager();
        CommandMeta commandMeta2 = commandManager2.metaBuilder("clanmsg")
                .build();
        SimpleCommand simpleCommand2 = new ClanMessage(server, connection);
        commandManager2.register(commandMeta2, simpleCommand2);

        server.getEventManager().register(this, new PlayerMessageToChat(server));

    }

    private boolean setupDatabase() throws SQLException, ClassNotFoundException {
        host = (String) config.get(Route.from("host"), String.class);
        port = (int) config.get(Route.from("port"), Integer.class);
        database = (String) config.get(Route.from("database"), String.class);
        username = (String) config.get(Route.from("username"), String.class);
        password = (String) config.get(Route.from("password"), String.class);

        DataBaseManager dataBaseManager = new DataBaseManager(host, port, database, username, password);
        if (!dataBaseManager.connect()) {
            return false;
        }
        connection = dataBaseManager.getConnection();
        return true;
    }
}
