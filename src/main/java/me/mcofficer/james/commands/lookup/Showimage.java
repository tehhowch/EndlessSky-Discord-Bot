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

public class Showimage extends Command {

    private final Lookups lookups;

    public Showimage(Lookups lookups) {
        name = "showimage";
        help = "Outputs the image associated with <query>.";
        arguments = "<query>";
        this.lookups = lookups;
    }

    @Override
    protected void execute(CommandEvent event) {
        List<DataNode> matches = lookups.getNodesByString(event.getArgs());

        if (matches.size() < 1)
            event.reply("Found no matches for `" + event.getArgs() + "`!");
        else if (matches.size() == 1)
            event.reply(createShowimageMessage(matches.get(0), event.getGuild()));
        else
            Util.displayNodeSearchResults(matches, event, (message, integer) -> event.reply(createShowimageMessage(matches.get(integer - 1), event.getGuild())));
    }

    private MessageEmbed createShowimageMessage(DataNode node, Guild guild) {
        return new EmbedBuilder()
                .setColor(guild.getSelfMember().getColor())
                .setImage(lookups.getImageUrl(node, false))
                .build();
    }
}
