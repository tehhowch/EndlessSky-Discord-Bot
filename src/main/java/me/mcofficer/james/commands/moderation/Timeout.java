package me.mcofficer.james.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.Util;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.List;

public class Timeout extends Command {

    private final String sTimeoutRole;

    public Timeout(String timeoutRole) {
        name = "timeout";
        help = "Sends the Member(s) X [Y, Z] for S seconds to #the-corner. S must always be the last argument.";
        arguments = "X [Y Z] S";
        sTimeoutRole = timeoutRole;
        category = James.moderation;
    }

    @Override
    protected void execute(CommandEvent event) {
        Role timeoutRole = event.getGuild().getRolesByName(sTimeoutRole, true).get(0);

        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(Util.getRandomDeniedMessage());
            return;
        }

        List<Member> toTimeout = event.getMessage().getMentionedMembers();
        String[] args = event.getArgs().split(" ");
        long time;
        try {
            time = Long.valueOf(args[toTimeout.size()]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            event.reply("Failed to parse \"" + args[toTimeout.size()] + "\"as Long!");
            return;
        }

        GuildController gc = event.getGuild().getController();

        for (Member member : toTimeout) {
            String onCommand = String.format("Sent Member %s to the corner for %s seconds (Ordered by `%s#%s`).",
                    member.getAsMention(), time, event.getMember().getUser().getName(), event.getMember().getUser().getDiscriminator());
            String onRelease = "Released Member " + member.getAsMention() + " from the corner.";
            Util.replaceRolesTemporarily(timeoutRole, time, member, onCommand, onRelease);
        }
    }
}
