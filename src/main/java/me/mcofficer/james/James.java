package me.mcofficer.james;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import me.mcofficer.esparser.DataFile;
import me.mcofficer.esparser.Sources;
import me.mcofficer.james.commands.lookup.Issue;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
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

        JDA jda = new JDABuilder(AccountType.BOT)
                .setToken(cfg.getProperty("token"))
                .addEventListener(clientBuilder.build())
                .buildBlocking();
    }

    private void addCommands(CommandClientBuilder builder, String githubToken) throws IOException {
        ArrayList<File> paths = fetchGameData(githubToken);
        ArrayList<DataFile> dataFiles = new ArrayList<>();
        for (File path : paths)
            dataFiles.add(new DataFile(path.getAbsolutePath()));
        Lookups lookups = new Lookups(dataFiles);

        builder.addCommand(new Issue());
    }

    private ArrayList<File> fetchGameData(String githubToken) throws IOException {
        Path temp = Files.createTempDirectory("james");
        File data = new File(temp.toAbsolutePath() + "/data/");
        data.mkdir();

        log.info("Downloading game data...");
        JSONArray json = new JSONArray(Util.getContentFromUrl("https://api.github.com/repos/endless-sky/endless-sky/contents/data?ref=master&access_token=" + githubToken));
        for (Object o : json) {
            JSONObject j = (JSONObject) o;
            Util.downloadFile(j.getString("download_url"), data.toPath());
        }
        ArrayList<File> sources = Sources.getSources(temp, null);
        Files.walk(temp)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        return sources;
    }
}
