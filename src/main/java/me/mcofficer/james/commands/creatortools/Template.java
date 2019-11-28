package me.mcofficer.james.commands.creatortools;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.HashMap;

public class Template extends Command {

    private HashMap<String, String> templates = new HashMap<>() {
        {
            put("plugin", "exampleplugin.zip");
            put("outfit", "outfittemplate.blend");
            put("ship", "shiptemplate.blend");
            put("thumbnail", "thumbnail.blend");
        }};

    public Template() {
        name = "template";
        help = "Serves a template X for content creators. Available templates are: "
                + String.join(", ", templates.keySet());
        category = James.creatorTools;
        arguments = "X";
    }

    @Override
    protected void execute(CommandEvent event) {
        String key = event.getArgs().toLowerCase();
        if (templates.containsKey(key)) {
            String url = James.GITHUB_RAW_URL + "data/templates/" + templates.get(key);
            event.reply(new EmbedBuilder()
                    .setTitle(templates.get(key), url)
                    .setColor(event.getGuild().getSelfMember().getColor())
                    .setDescription("Here's your template, served hot and crunchy :)")
                    .build()
            );
        }
        else
            event.reply(String.format("Which template would you like? I have the following flavours available: %s.",
                    String.join(", ", templates.keySet())));
    }
}
