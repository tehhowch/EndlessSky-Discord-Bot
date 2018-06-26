package me.mcofficer.james.commands.fun;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.Util;

public class Birb extends Command{

    public Birb() {
        this.name = "Birb";
        this.help = "Posts a random birb image.";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("https://random.birb.pw/img/" + Util.getContentFromUrl("http://random.birb.pw/tweet/"));
    }
}
