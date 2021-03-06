package me.mcofficer.james.commands.lookup;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.Util;

public class Issue extends Command{

    public Issue() {
        name = "issue";
        help = "Gets an issue from the ES repo by it's number.";
        arguments = "<number>";
        category = James.lookup;
        aliases = new String[]{"pull", "pr"};
    }

    @Override
    protected void execute(CommandEvent event) {
        String url = James.ES_GITHUB_URL + "issues/" + event.getMessage().getContentDisplay().split(" ")[1];

        int s = Util.getHttpStatus(url);

        if ( (200 <= s && s < 400) || s >= 500)
            event.reply(url);
        else if(s == 404)
            event.reply("Issue not found, make sure you entered the correct number.");
    }
}
