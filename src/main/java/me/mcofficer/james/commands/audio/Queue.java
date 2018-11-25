package me.mcofficer.james.commands.audio;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.audio.Audio;

import java.util.Arrays;

public class Queue extends Command {

    private final Audio audio;

    public Queue(Audio audio) {
        name = "queue";
        help = "Displays the current Queue or (using `-queue print`) returns a text file containing the Queue and currently playing song.";
        category = James.audio;
        this.audio = audio;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (audio.getVoiceChannel() != null && event.getMember().getVoiceState().getChannel().equals(audio.getVoiceChannel())) {
            String[] args = event.getArgs().split(" ");
            if (args[0].equalsIgnoreCase("print")) {
                String fileName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                if (fileName.length() == 0)
                    fileName = "queue.txt";
                else if (!fileName.endsWith(".txt"))
                    fileName += ".txt";
                audio.sendQueueFile(event, fileName);
            }
            else
                audio.createQueueEmbed(event);
        }
    }
}
