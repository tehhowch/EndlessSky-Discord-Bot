package me.mcofficer.james.commands.lookup;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.esparser.DataNode;
import me.mcofficer.james.James;
import me.mcofficer.james.Util;
import me.mcofficer.james.tools.Lookups;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

public class Show extends Command {

    private final Lookups lookups;

    public Show(Lookups lookups) {
        name = "show";
        help = "Outputs the image and data associated with <query>.";
        arguments = "<query>";
        category = James.lookup;
        this.lookups = lookups;
    }

    @Override
    protected void execute(CommandEvent event) {
    List<DataNode> matches = lookups.getNodesByString(event.getArgs());

    if (matches.size() < 1)
        event.reply("Found no matches for `" + event.getArgs() + "`!");
    else if (matches.size() == 1)
        event.reply(createShowMessage(matches.get(0), event.getGuild()));
    else
        Util.displayNodeSearchResults(matches, event, (((message, integer) -> event.reply(createShowMessage(matches.get(integer - 1), event.getGuild())))));
    }

    private Message createShowMessage(DataNode node, Guild guild) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(guild.getSelfMember().getColor())
                .setImage(lookups.getImageUrl(node, false));
        return new MessageBuilder()
                .setEmbed(embedBuilder.isEmpty() ? null : embedBuilder.build()) // if no image was found, the embed builder cannot be built
                .append("```")
                .append(lookups.getNodeAsText(node))
                .append("```")
                .build();
    }
}
