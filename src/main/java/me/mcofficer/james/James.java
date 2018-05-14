package me.mcofficer.james;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class James {

    Logger log = LoggerFactory.getLogger(James.class);

    public static void main(String[] args) {
        Properties cfg = new Properties();
        try {
            cfg.load(new FileReader("james.properties"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            new James(cfg);
        }
        catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public James(Properties cfg) throws LoginException, InterruptedException {
        //TODO: Use custom help command (no DMs)
        CommandClient commandClient = new CommandClientBuilder()
                .setPrefix("-")
                .setGame(Game.listening("-help"))
                .setOwnerId("177733454824341505") // yep, that's me
                .build();

        JDA jda = new JDABuilder(AccountType.BOT)
                .setToken(cfg.getProperty("token"))
                .addEventListener(commandClient)
                .buildBlocking();
    }
}
