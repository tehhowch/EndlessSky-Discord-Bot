package me.mcofficer.james.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.Util;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Lawyer extends Command {

    private final String sLaywerRole;

    public Lawyer(String laywerRole) {
        this.name = "laywer";
        this.help = "Sends the Member(s) X [Y, Z] for S seconds to #funderdome. S must always be the last argument.";
        this.arguments = "X [Y Z] S";
        sLaywerRole = laywerRole;
    }

    @Override
    protected void execute(CommandEvent event) {
        Role laywerRole = event.getGuild().getRolesByName(sLaywerRole, true).get(0);

        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(Util.getRandomDeniedMessage());
            return;
        }

        List<Member> toFunderdome = event.getMessage().getMentionedMembers();
        String[] args = event.getArgs().split(" ");
        long time;
        try {
            time = Long.valueOf(args[args.length - 1]);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            event.reply("Failed to parse \"" + args[args.length - 1] + "\"as Long!");
            return;
        }

        GuildController gc = event.getGuild().getController();

        for (Member member : toFunderdome) {
            List<Role> originalRoles = member.getRoles();

            // Remove current roles & add timeout role
            gc.removeRolesFromMember(member, originalRoles).queue();
            gc.addSingleRoleToMember(member, laywerRole).queue( success1 ->
                    // Remove timout role & re-add old roles
                    gc.removeSingleRoleFromMember(member, laywerRole).queueAfter(time, TimeUnit.SECONDS, success2 -> {
                        originalRoles.forEach(role -> gc.addSingleRoleToMember(member, role).queue());
                        Util.log(event.getGuild(), "Released Member " + member.getAsMention() + " from the corner.");
                })
            );
            Util.log(event.getGuild(), String.format("Sent Member %s to the corner for %s seconds (Ordered by `%s#%s`).",
                    member.getAsMention(), time, event.getMember().getUser().getName(), event.getMember().getUser().getDiscriminator()));
        }
    }
}
