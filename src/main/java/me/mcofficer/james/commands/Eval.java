package me.mcofficer.james.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.audio.Playlists;
import me.mcofficer.james.tools.Lookups;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Properties;

public class Eval extends Command {

    private final Lookups lookups;
    private final Playlists playlists;
    private final Properties config;

    public Eval(Lookups lookups, Playlists playlists, Properties config) {
        name = "eval";
        arguments = "code";
        hidden = true;
        ownerCommand = true;
        this.lookups = lookups;
        this.playlists = playlists;
        this.config = config;
    }

    @Override
    protected void execute(CommandEvent event) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("Nashorn");
        engine.put("jda", event.getJDA());
        engine.put("event", event);
        engine.put("guild", event.getGuild());
        engine.put("channel", event.getChannel());
        engine.put("lookups", lookups);
        engine.put("playlists", playlists);
        engine.put("config", config);

        try {
            event.reply("Eval successful:\n```\n" + engine.eval(event.getArgs()) + "\n```");
        }
        catch (Exception e) {
            e.printStackTrace();
            event.reply("An Exception occured:\n```\n" + e + "\n```");
        }
    }
}
