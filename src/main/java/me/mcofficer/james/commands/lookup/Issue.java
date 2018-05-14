package me.mcofficer.james.commands.lookup;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import me.mcofficer.james.Util;
import net.dv8tion.jda.core.entities.TextChannel;

@CommandInfo(
        name = {"issue", "pull", "pr"},
        description = "Gets an issue/PR from the ES repo by its number."
)

@Author("MCOfficer")
public class Issue extends Command{

    public Issue() {
        this.name = "issue";
        this.help = "Gets an issue from the ES repo by it's number.";
        this.arguments = "<number>";
        this.aliases = new String[]{"pull", "pr"};
    }

    @Override
    protected void execute(CommandEvent event) {
        TextChannel channel = event.getTextChannel();
        String url = "https://github.com/endless-sky/endless-sky/issues/" + event.getArgs();

        int s = Util.getHttpStatus(url);

        if ( (200 <= s && s < 400) || s >= 500)
            channel.sendMessage(url).queue();
        else if(s == 404)
            channel.sendMessage("Issue not found, make sure you entered the correct number.").queue();
    }
}
