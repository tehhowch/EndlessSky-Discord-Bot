package me.mcofficer.james.commands.lookup;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.Lookups;
import net.dv8tion.jda.core.EmbedBuilder;

public class Showimage extends Command {

    private final Lookups lookups;

    public Showimage(Lookups lookups) {
        this.name = "showimage";
        this.help = "Outputs the image associated with <query>.";
        this.arguments = "<query>";
        this.lookups = lookups;
    }

    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(event.getGuild().getSelfMember().getColor())
                .setImage(lookups.getImageUrlByString(event.getArgs()));
        event.reply(embedBuilder.build());
    }
}
