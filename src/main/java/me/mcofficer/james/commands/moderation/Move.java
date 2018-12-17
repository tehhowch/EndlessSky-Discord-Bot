package me.mcofficer.james.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Move extends Command {

    public Move() {
        name = "move";
        help = "Moves X messages to Channel Y. Removes Embeds in the process.";
        arguments = "X Y";
        aliases = new String[]{"wormhole"};
        category = James.moderation;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        int amount;
        TextChannel dest = event.getMessage().getMentionedChannels().get(0);
        try {
            amount = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            event.reply("Failed to parse \"" + args[1] + "\"as Integer!");
            return;
        }

        if (event.getMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE)
                && event.getMember().hasPermission(dest, Permission.MESSAGE_WRITE)
                && event.getGuild().getSelfMember().hasPermission(dest, Permission.MESSAGE_WRITE)) {
            event.getMessage().delete().complete();

            // Use a lambda to asynchronously perform this request:
            event.getTextChannel().getHistory().retrievePast(amount).queue( toDelete -> {
                if (toDelete.isEmpty())
                    return;
                LinkedList<String> toMove = new LinkedList<>();
                for (Message m : toDelete) {
                    Member chatter = m.getMember();
                    String what = m.getContentStripped().trim();
                    if (what.isEmpty())
                        continue;
                    toMove.addFirst(m.getCreationTime()
                            .format(DateTimeFormatter.ISO_INSTANT).substring(11, 19)
                            + "Z " + chatter.getEffectiveName() + ": " + what + "\n"
                    );
                }
                // Remove the messages from the original channel and log the move.
                event.getTextChannel().deleteMessages(toDelete).queue(x -> {
                    EmbedBuilder log = new EmbedBuilder();
                    log.setDescription(dest.getAsMention());
                    log.setThumbnail("https://cdn.discordapp.com/emojis/344684586904584202.png");
                    log.appendDescription("\n(" + toMove.size() + " messages await)");
                    if (toDelete.size() - toMove.size() > 0)
                        log.appendDescription("\n(Some embeds were eaten)");
                    event.getTextChannel().sendMessage(log.build()).queue();
                });

                // Transport the message content to the new channel.
                if(!toMove.isEmpty())
                    Util.sendInChunks(dest, toMove, "Incoming wormhole content from " + dest.getAsMention() + ":\n```", "```");

                // Log the move in mod-log.
                String report = "Moved " + toMove.size() +
                        " messages from " + event.getTextChannel().getAsMention() +
                        " to " + dest.getAsMention() + ", ordered by `" +
                        event.getMember().getEffectiveName() + "`.";
                Util.log(event.getGuild(), report);
            });
        }
        else
            event.reply(Util.getRandomDeniedMessage());
    }
}
