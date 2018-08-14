package me.mcofficer.james.commands.fun;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.Util;

public class Dog extends Command{

    public Dog() {
        name = "dog";
        help = "Posts a random dog image.";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("https://random.dog/" + Util.getContentFromUrl("https://random.dog/woof"));
    }
}
