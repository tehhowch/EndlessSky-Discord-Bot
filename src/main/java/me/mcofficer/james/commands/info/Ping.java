package me.mcofficer.james.commands.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import net.dv8tion.jda.core.EmbedBuilder;

public class Ping extends Command {

    public Ping() {
        name = "ping";
        help = "Displays the time of the bot's last heartbeat.";
        category = James.info;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply(new EmbedBuilder()
                .setTitle("EndlessSky-Discord-Bot", James.GITHUB_URL)
                .setDescription("Last Heartbeat took " + event.getJDA().getPing() + "ms.")
                .setColor(event.getGuild().getSelfMember().getColor())
                .build()
        );
    }
}
