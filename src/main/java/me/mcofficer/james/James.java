package me.mcofficer.james;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import me.mcofficer.esparser.DataFile;
import me.mcofficer.james.audio.Audio;
import me.mcofficer.james.commands.Help;
import me.mcofficer.james.commands.Info;
import me.mcofficer.james.commands.audio.Play;
import me.mcofficer.james.commands.audio.Stop;
import me.mcofficer.james.commands.creatortools.SwizzleImage;
import me.mcofficer.james.commands.fun.*;
import me.mcofficer.james.commands.lookup.*;
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
        catch (LoginException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private James(Properties cfg) throws LoginException, InterruptedException, IOException {
        //TODO: Use custom help command (no DMs)
        CommandClientBuilder clientBuilder = new CommandClientBuilder()
                .setPrefix("-")
                .setGame(Game.listening("-help"))
                .setOwnerId("177733454824341505"); // yep, that's me
        addCommands(clientBuilder, cfg.getProperty("github"));

        clientBuilder.setHelpConsumer(new Help(clientBuilder.build())); // this HAS to be done after adding all Commands!

        JDA jda = new JDABuilder(AccountType.BOT)
                .setToken(cfg.getProperty("token"))
                .addEventListener(clientBuilder.build())
                .buildBlocking();
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

        builder.addCommands(new Issue(), new Commit(), new Showdata(lookups), new Showimage(lookups), new Show(lookups), new Lookup(lookups),
                new SwizzleImage(),
                new Info(githubToken),
                new Cat(), new Dog(), new Birb(),
                new Play(audio), new Stop(audio)
        );
    }
}
