package me.mcofficer.james.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.Util;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Optin extends Command {

    private final String[] optinRoles;

    public Optin(String[] optinRoles) {
        this.optinRoles = optinRoles;
        this.name = "Optin";
        this.help = "Adds the user to one or more roles X (, Y, Z). A list of free-to-join roles can be found in the rules.";
        this.arguments = "X [Y Z]";
    }

    @Override
    protected void execute(CommandEvent event) {
        List<Role> add = Util.getOptinRolesByQuery(event.getArgs(), event.getGuild(), optinRoles);
        event.getGuild().getController().addRolesToMember(event.getMember(), add).queue(success1 ->
                event.getMessage().addReaction("\uD83D\uDC4C").queue(success2 ->
                        event.getMessage().delete().queueAfter(20, TimeUnit.SECONDS)
                )
        );
    }
}
