package rocks.milspecsg.msparties.commands.party;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msparties.PluginInfo;
import rocks.milspecsg.msparties.api.party.PartyRepository;
import rocks.milspecsg.msparties.model.core.Party;
import rocks.milspecsg.msparties.model.results.CreateResult;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class PartyCreateCommand implements CommandExecutor {

    private PartyRepository partyRepository;

    @Inject
    public PartyCreateCommand(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {
        Optional<String> optionalName = context.getOne(Text.of("name"));
        if (source instanceof Player) {
            Player player = (Player) source;


            // should not happen
            if (!optionalName.isPresent()) throw new CommandException(Text.of("Missing name"));


            partyRepository.createParty(optionalName.get(), player).thenAcceptAsync(createResult -> {

                if (createResult.isSuccess() && createResult.getValue().isPresent()) {
                    Party party = createResult.getValue().get();
                    player.sendMessage(Text.of(
                            PluginInfo.PluginPrefix, TextColors.GRAY, "You have successfully created ", TextColors.YELLOW, party.name, "\n",
                            TextColors.GRAY, "Run ", TextColors.GREEN, "/p invite <player>", TextColors.GRAY, " to invite a friend\n",
                            "or ", TextColors.GREEN, "/p public", TextColors.GRAY, " allow anyone to join"
                    ));
                } else {
                    player.sendMessage(Text.of(
                            PluginInfo.PluginPrefix, TextColors.RED, createResult.getErrorMessage()
                    ));
                }
            });


//            CreateResult<? extends Party> createResult = partyRepository.createParty(optionalName.get(), player).thenCompose();
//            if (createResult.isSuccess() && createResult.getValue().isPresent()) {
//                Party party = createResult.getValue().get();
//                player.sendMessage(Text.of(
//                        PluginInfo.PluginPrefix, TextColors.GRAY, "You have successfully created ", TextColors.YELLOW, party.name, "\n",
//                        TextColors.GRAY, "Run ", TextColors.GREEN, "/p invite <player>", TextColors.GRAY, " to invite a friend\n",
//                        "or ", TextColors.GREEN, "/p public", TextColors.GRAY, " allow anyone to join"
//                ));
//            } else {
//                throw new CommandException(Text.of(TextColors.RED, createResult.getErrorMessage()));
//            }



            return CommandResult.success();
        } else {
            throw new CommandException(Text.of(TextColors.RED, "Command can only be run as player"));
        }
    }
}
