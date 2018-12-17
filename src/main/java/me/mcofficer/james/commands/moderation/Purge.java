package me.mcofficer.james.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

public class Purge extends Command {

    public Purge() {
        name = "purge";
        help = "Purges the last X messages from the current channel.";
        arguments = "X";
        category = James.moderation;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE)) {
            try {
                int amount = Integer.valueOf(event.getArgs());
                if (amount < 2 || 100 < amount)
                    throw new NumberFormatException();
                TextChannel channel = event.getTextChannel();

                event.getMessage().delete().queue( a ->
                    channel.getHistory().retrievePast(amount).queue( messages -> {
                        try {
                            channel.deleteMessages(messages).queue(b -> {
                                EmbedBuilder embedBuilder = new EmbedBuilder()
                                        .setTitle("Moderation")
                                        .setColor(event.getGuild().getSelfMember().getColor())
                                        .setDescription("Spaced " + messages.size() + " Messages! Who's next?!");
                                event.reply(embedBuilder.build());
                                Util.log(event.getGuild(), String.format("Purged %s messages in %s, ordered by `%s`.",
                                        amount, channel.getAsMention(), event.getMember().getEffectiveName()));
                            });
                        }
                        catch(IllegalArgumentException e) {
                            event.reply(event.getMember().getAsMention() + " One or more messages are older than 2 weeks and cannot be deleted.");
                        }
                    })
                );
            }
            catch (NumberFormatException e) {
                event.reply("'" + event.getArgs() + "' is not a valid integer between 2 and 100!");
            }
        }
        else {
            event.reply(Util.getRandomDeniedMessage());
        }
    }
}
