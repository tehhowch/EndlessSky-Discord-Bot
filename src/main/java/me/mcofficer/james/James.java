package me.mcofficer.james;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.mcofficer.esparser.DataFile;
import me.mcofficer.james.audio.Audio;
import me.mcofficer.james.commands.*;
import me.mcofficer.james.commands.audio.*;
import me.mcofficer.james.commands.creatortools.*;
import me.mcofficer.james.commands.fun.*;
import me.mcofficer.james.commands.lookup.*;
import me.mcofficer.james.commands.moderation.*;
import me.mcofficer.james.tools.Lookups;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class James {

    public final static String GITHUB_URL = "https://github.com/MCOfficer/EndlessSky-Discord-Bot/";
    public final static String ES_GITHUB_URL = "https://github.com/endless-sky/endless-sky/";

    public final static EventWaiter eventWaiter = new EventWaiter();

    private static Properties cfg = new Properties();

    Logger log = LoggerFactory.getLogger(James.class);

    public static void main(String[] args) {
        try {
            cfg.load(new FileReader("james.properties"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            new James(cfg);
        }
        catch (LoginException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private James(Properties cfg) throws LoginException, InterruptedException, IOException {
        CommandClientBuilder clientBuilder = new CommandClientBuilder()
                .setPrefix("-")
                .setGame(Game.listening("-help"))
                .setOwnerId("177733454824341505"); // yep, that's me
        addCommands(clientBuilder, cfg.getProperty("github"));

        clientBuilder.setHelpConsumer(new Help(clientBuilder.build())); // this HAS to be done after adding all Commands!

        JDA jda = new JDABuilder(AccountType.BOT)
                .setToken(cfg.getProperty("token"))
                .addEventListener(clientBuilder.build())
                .addEventListener(eventWaiter)
                .build()
                .awaitReady();
    }

    private void addCommands(CommandClientBuilder builder, String githubToken) throws IOException {
        log.info("Downloading game data...");
        ArrayList<File> paths = Util.fetchGameData(githubToken);
        ArrayList<DataFile> dataFiles = new ArrayList<>();
        for (File path : paths)
            dataFiles.add(new DataFile(path.getAbsolutePath()));

        log.info("Fetching image paths...");
        ArrayList<String> imagePaths = Util.get1xImagePaths(githubToken);
        Lookups lookups = new Lookups(dataFiles, imagePaths);
        log.info("Lookups instantiated");

        log.info("Starting background thread to fetch hdpi image paths...");
        new Thread(() -> {
            lookups.setImagePaths(Util.get2xImagePaths(imagePaths));
            log.info("Hdpi image paths fetched successfully.");
        }).start();

        Audio audio = new Audio();

        String[] optinRoles = cfg.getProperty("optinRoles").split(",");

        builder.addCommands(
                new Info(githubToken), new Eval(lookups, cfg),
                new Play(audio), new Stop(audio),
                new SwizzleImage(),
                new Cat(), new Dog(), new Birb(),
                new Issue(), new Commit(), new Showdata(lookups), new Showimage(lookups), new Show(lookups), new Lookup(lookups),
                new Purge(), new Optin(optinRoles, cfg.getProperty("timeoutRole")), new Optout(optinRoles), new Timeout(cfg.getProperty("timeoutRole"))
        );
    }
}
