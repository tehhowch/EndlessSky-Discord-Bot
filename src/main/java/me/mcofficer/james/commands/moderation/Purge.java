package me.mcofficer.james.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

public class Purge extends Command {

    public Purge() {
        this.name = "Purge";
        this.help = "Purges the last X messages from the current channel.";
        this.arguments = "X";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE)) {
            try {
                int amount = Integer.valueOf(event.getArgs());
                if (amount < 1 || 100 < amount)
                    throw new NumberFormatException();
                TextChannel channel = event.getTextChannel();

                event.getMessage().delete().queue( a ->
                    channel.getHistory().retrievePast(amount).queue( messages -> {
                        channel.deleteMessages(messages).queue( b ->{
                            EmbedBuilder embedBuilder = new EmbedBuilder()
                                    .setTitle("Moderation")
                                    .setColor(event.getGuild().getSelfMember().getColor())
                                    .setDescription("Spaced " + messages.size() + " Messages! Who's next?!");
                            event.reply(embedBuilder.build());
                            //TODO: Log to #mod-log
                        });
                    })
                );
            }
            catch (NumberFormatException e) {
                event.reply("'" + event.getArgs() + "' is not a valid integer between 1 and 100!");
                return;
            }
        }
        else {
            //TODO: sassy "access denied" message
        }
    }
}
