package me.mcofficer.james.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.Util;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Optout extends Command {

    private final String[] optinRoles;

    public Optout(String[] optinRoles) {
        name = "optout";
        help = "Removes the user from one or more roles X (, Y, Z). A list of free-to-join roles can be found in the rules.";
        arguments = "X [Y Z]";
        category = James.moderation;
        this.optinRoles = optinRoles;
    }

    @Override
    protected void execute(CommandEvent event) {
        List<Role> remove = Util.getOptinRolesByQuery(event.getArgs(), event.getGuild(), optinRoles);
        event.getGuild().modifyMemberRoles(event.getMember(), remove).queue(success1 ->
                event.getMessage().addReaction("\uD83D\uDC4C").queue(success2 ->
                        event.getMessage().delete().queueAfter(20, TimeUnit.SECONDS)
                )
        );
    }
}
