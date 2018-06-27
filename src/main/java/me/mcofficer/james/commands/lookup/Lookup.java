package me.mcofficer.james.commands.lookup;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.tools.Lookups;
import net.dv8tion.jda.core.EmbedBuilder;

public class Lookup extends Command {

    private final Lookups lookups;

    public Lookup(Lookups lookups) {
        this.name = "Lookup";
        this.help = "Outputs the image and description of <query>.";
        this.arguments = "<query>";
        this.lookups = lookups;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] lookup = lookups.getLookupByString(event.getArgs());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(event.getGuild().getSelfMember().getColor())
                .setImage(lookup[0])
                .setDescription(lookup[1]);
        event.reply(embedBuilder.build());
    }
}
