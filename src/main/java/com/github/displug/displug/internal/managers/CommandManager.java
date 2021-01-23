/*
 * Copyright (c) 2021,
 * Displug team(https://github.com/orgs/displug/people)
 * and collaborator(https://github.com/displug/Displug/graphs/contributors)
 * All Right Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.displug.displug.internal.managers;

import com.github.displug.displug.api.Command;
import com.github.displug.displug.api.Displug;
import com.github.displug.displug.api.events.command.CommandExecuted;
import com.github.displug.displug.api.events.interaction.InteractionCreatedEvent;
import com.github.displug.displug.internal.DisplugImpl;
import com.github.displug.displug.internal.command.CommandContext;
import com.github.displug.displug.internal.command.CommandResult;
import com.github.displug.displug.internal.entity.ApplicationCommand;
import com.github.displug.displug.internal.utils.RequestUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.impl.Logger;
import org.slf4j.impl.StaticLoggerBinder;

public class CommandManager extends DManager<String, Command> implements EventListener {

    private final Displug displug;

    private final Logger logger = StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger(this.getClass());

    public CommandManager(Displug displug) {
        this.displug = displug;
    }

    @Override
    public void add(String unused, Command command) {
        ((DisplugImpl) displug).getRequiredPermission().addAll(Arrays.asList(command.getPermissions()));
        super.add(command.getName(), command);
        for (String alias : command.getAliases()) {
            super.add(alias, command);
        }
    }

    public void remove(Command command) {
        super.remove(command.getName());
        for (String alias : command.getAliases()) {
            super.remove(alias);
        }
    }

    public void syncWithDiscord() {
        List<ApplicationCommand.CommandData> registeredCommand = RequestUtils.getAlreadyRegisteredCommand(displug.getJDA());
        List<Command> notSync = new ArrayList<>(new HashSet<>(this.all().values()));
        if (registeredCommand != null) {
            for (ApplicationCommand.CommandData commandData : registeredCommand) {
                if (this.all().get(commandData.getName()) == null) {
                    RequestUtils.removeCommand(displug.getJDA(), commandData);
                    notSync.remove(this.all().get(commandData.getName()));
                }
            }
        }
        notSync = notSync.stream().filter(Command::isSlashCommand).collect(Collectors.toList());
        for (Command command : notSync) {
            RequestUtils.addCommand(displug.getJDA(), new ApplicationCommand.CommandData(command));
        }
    }

    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            onText((MessageReceivedEvent) event);
        }
        if (event instanceof InteractionCreatedEvent) {
            onInteraction((InteractionCreatedEvent) event);
        }
    }

    public void onText(@NotNull MessageReceivedEvent event) {
        Message message = event.getMessage();
        if (message.getAuthor().isBot()) {
            return;
        }
        for (String prefix : displug.getConfiguration().bot.prefix) {
            String raw = message.getContentRaw();
            if (!raw.startsWith(prefix)) {
                continue;
            }
            raw = raw.replaceFirst(prefix, "");
            String[] splitRaw = raw.split(" ");
            raw = raw.replaceFirst(splitRaw[0] + " ", "");
            Command command = get(splitRaw[0]);
            execute(event, raw.split(" "), command);
        }
    }

    public void onInteraction(@NotNull InteractionCreatedEvent event) {
        ApplicationCommand interaction = (ApplicationCommand) event.getInteraction();
        Command command = get(interaction.getCommandData().getName());
        String raw = "";
        if (interaction.getCommandData().getOptions() != null && interaction.getCommandData().getOptions().length != 0) {
            raw = formatRawFromInteraction(interaction.getCommandData().getOptions());
        }
        execute(event, raw.split(" "), command);
    }

    private String formatRawFromInteraction(ApplicationCommand.CommandData.Option[] options) {
        StringBuilder raw = new StringBuilder();
        for (ApplicationCommand.CommandData.Option option : options) {
            if (option.getType() == ApplicationCommand.CommandData.Option.Type.BOOLEAN) {
                if ((boolean) option.getValue()) {
                    raw.append(" --").append(option.getName());
                }
            }
            raw.append(" --").append(option.getName()).append(" ").append(option.getValue());
            if (option.getOptions() != null && option.getOptions().length != 0) {
                raw.append(formatRawFromInteraction(option.getOptions()));
            }
        }
        return raw.toString();
    }

    public void execute(@NotNull GenericEvent event, String[] args, Command command) {
        if (command == null) {
            return;
        }
        try {
            CommandLine cliArgument = null;
            if (command.getArguments() != null) {
                cliArgument = new DefaultParser().parse(command.getArguments(), args);
            }
            CommandContext context = new CommandContext(event, String.join(" ", args), cliArgument);
            CommandResult execute = command.execute(context);
            if (execute != null) {
                execute.execute(context);
            }

            event.getJDA().getEventManager().handle(new CommandExecuted(event.getJDA(), command, context));
        } catch (Exception e) {
            logger.warn(String.format("An error as occurred when executing command (%s)", command.getName()), e);
        }
    }
}
