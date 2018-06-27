package me.mcofficer.james.commands.lookup;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.tools.Lookups;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;

public class Show extends Command {

    private final Lookups lookups;

    public Show(Lookups lookups) {
        this.name = "show";
        this.help = "Outputs the image and data associated with <query>.";
        this.arguments = "<query>";
        this.lookups = lookups;
    }

    @Override
    protected void execute(CommandEvent event) {

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(event.getGuild().getSelfMember().getColor())
                .setImage(lookups.getImageUrlByString(event.getArgs()));
        event.reply(
                new MessageBuilder()
                        .setEmbed(embedBuilder.build())
                        .append("```")
                        .append(lookups.getNodeAsText(lookups.getNodeByString(event.getArgs())))
                        .append("```")
                        .build()
        );
    }
}
