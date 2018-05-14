package me.mcofficer.james;

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
        JDA jda = new JDABuilder(AccountType.BOT)
                .setToken(cfg.getProperty("token"))
                .setGame(Game.listening("-help"))
                .buildBlocking();
    }
}
