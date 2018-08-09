package me.mcofficer.james.commands.lookup;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.esparser.DataNode;
import me.mcofficer.james.Util;
import me.mcofficer.james.tools.Lookups;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.util.List;

public class Lookup extends Command {

    private final Lookups lookups;

    public Lookup(Lookups lookups) {
        this.name = "lookup";
        this.help = "Outputs the image and description of <query>.";
        this.arguments = "<query>";
        this.lookups = lookups;
    }

    @Override
    protected void execute(CommandEvent event) {
        List<DataNode> matches = lookups.getNodesByString(event.getArgs());

        if (matches.size() < 1)
            event.reply("Found no matches for `" + event.getArgs() + "`!");
        else if (matches.size() == 1)
            event.reply(createLookupMessage(matches.get(0), event.getGuild()));
        else
            Util.displayNodeSearchResults(matches, event, ((message, integer) -> event.reply(createLookupMessage(matches.get(integer - 1), event.getGuild()))));
    }

    private MessageEmbed createLookupMessage(DataNode node, Guild guild) {
        String[] lookup = lookups.getLookupByNode(node);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(guild.getSelfMember().getColor());

        if (lookup[0] == null)
            embedBuilder.appendDescription("Couldn't find an image node!\n\n");
        else
            embedBuilder.setImage(lookup[0]);

        if (lookup[1] == null)
            embedBuilder.appendDescription("Couldn't find a description node!");
        else
            embedBuilder.setDescription(lookup[1]);

        embedBuilder.appendDescription("\n\n" + lookups.getLinks(node));

        return embedBuilder.build();
    }
}
