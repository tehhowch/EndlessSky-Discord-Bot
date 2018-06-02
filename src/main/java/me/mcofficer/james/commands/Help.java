package me.mcofficer.james.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.util.List;
import java.util.function.Consumer;

public class Help implements Consumer<CommandEvent>{

    private final List<Command> commands;
    private final String prefix;
    private MessageEmbed helpEmbed;

    public Help(CommandClient client) {
        commands = client.getCommands();
        prefix = client.getPrefix();
        helpEmbed = createHelpEmbed();
    }

    @Override
    public void accept(CommandEvent e) {
        if (e.getArgs().isEmpty())
            e.reply(helpEmbed);
        else
            for (Command c : commands)
                if (c.getName().equalsIgnoreCase(e.getArgs()))
                    e.reply(createHelpEmbed(c));
    }

    private MessageEmbed createHelpEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();

        for (Command c : commands) {
            sb.append(String.format("`%s%s %s`", prefix, c.getName(), c.getArguments()));
            if (c.getAliases().length > 0) {
                sb.append(" (");
                for (String alias : c.getAliases())
                    sb.append(String.format("`%s%s`, ", prefix, alias));
                sb.delete(sb.length() - 1, sb.length());
                sb.append(")");
            }
            sb.append("\n");
        }

        embedBuilder.setTitle("EndlessSky-Discord-Bot", James.GITHUB_URL);
        embedBuilder.setDescription(sb.toString());

        return embedBuilder.build();
    }

    private MessageEmbed createHelpEmbed(Command c)  {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("EndlessSky-Discord-Bot", James.GITHUB_URL)
                .setDescription(String.format("`%s%s`\n", c.getName(), c.getArguments()))
                .appendDescription(c.getHelp() + "\n");

        if (c.getAliases().length > 0) {
            embedBuilder.appendDescription(String.format("**Aliases: **"));
            StringBuilder sb = new StringBuilder();
            for (String alias : c.getAliases())
                sb.append(String.format("`%s`, ", alias));
            sb.delete(sb.length() - 1, sb.length());
            embedBuilder.appendDescription(sb);
        }

        return embedBuilder.build();
    }
}
