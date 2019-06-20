package me.mcofficer.james.commands.lookup;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.Util;

public class Commit extends Command{

    public Commit() {
        name = "commit";
        help = "Gets a commit from the ES repo by it's hash.";
        arguments = "<hash>";
        category = James.lookup;
    }

    @Override
    protected void execute(CommandEvent event) {
        String url = James.ES_GITHUB_URL + "commit/" + event.getMessage().getContentDisplay();

        int s = Util.getHttpStatus(url);

        if ( (200 <= s && s < 400) || s >= 500)
            event.reply(url);
        else if(s == 404)
            event.reply("Commit not found, make sure you entered a valid commithash.");
    }
}
