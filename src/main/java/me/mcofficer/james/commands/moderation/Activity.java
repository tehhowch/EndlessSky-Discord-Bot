package me.mcofficer.james.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.Util;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.MiscUtil;

import java.time.Instant;
import java.util.ArrayList;

public class Activity extends Command {

    private final String[] ontopicCategories;

    public Activity(String[] ontopicCategories) {
        name = "activity";
        arguments = "X";
        help = "Provides stats about the activity of a Member X in the ontopic channels. For Mods only, has 30 seconds cooldown.";
        cooldown = 30;
        category = James.moderation;
        this.ontopicCategories = ontopicCategories;
    }

    @Override
    protected void execute(CommandEvent event) {
        // only allowed for Mods
        if (event.getMember().getRoles().get(0).getPosition() < event.getGuild().getSelfMember().getRoles().get(0).getPosition()) {
            event.reply(Util.getRandomDeniedMessage());
            return;
        }

        Member query = event.getMessage().getMentionedMembers().get(0);

        long timestamp = Instant.now().minusSeconds(1209600L).toEpochMilli(); // 2 weeks ago
        String discordTimestamp = Long.toUnsignedString(MiscUtil.getDiscordTimestamp(timestamp));

        ArrayList<Message> messages = new ArrayList<>();
        for (String c : ontopicCategories)
            for (TextChannel channel : event.getJDA().getCategoryById(c).getTextChannels())
                messages.addAll(MessageHistory.getHistoryBefore(channel, discordTimestamp).complete().getRetrievedHistory());

        int count = 0;
        for (Message msg : messages)
            if (msg.getMember().equals(query))
                count++;

        event.reply(query.getAsMention() + " sent " + count + " Messages in Ontopic-Channels over the last 2 weeks.");
    }
}
