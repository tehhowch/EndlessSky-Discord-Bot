package me.mcofficer.james.commands.fun;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.Util;
import org.json.JSONObject;

public class Cat extends Command{

    public Cat() {
        name = "cat";
        help = "Posts a random cat image.";
        category = James.fun;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply(new JSONObject(Util.getContentFromUrl("https://aws.random.cat/meow")).getString("file"));
    }
}
