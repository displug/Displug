/*
 * Copyright (c) 2020,
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
package ga.enimaloc.displug.internal.managers;

import ga.enimaloc.displug.api.Command;
import ga.enimaloc.displug.api.Displug;
import ga.enimaloc.displug.api.events.command.CommandExecuted;
import ga.enimaloc.displug.api.events.command.CommandRegister;
import ga.enimaloc.displug.internal.DisplugImpl;
import ga.enimaloc.displug.internal.command.CommandContext;
import java.util.Arrays;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
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
        displug.getJDA().getEventManager().handle(new CommandRegister(displug.getJDA(), command));
    }

    public void remove(Command command) {
        super.remove(command.getName());
        for (String alias : command.getAliases()) {
            super.remove(alias);
        }
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            Message message = ((MessageReceivedEvent) event).getMessage();
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
                if (command == null) {
                    continue;
                }
                try {
                    CommandContext context = new CommandContext((MessageReceivedEvent) event, raw);
                    command.execute(context).execute(((MessageReceivedEvent) event).getMessage());

                    event.getJDA().getEventManager().handle(new CommandExecuted(event.getJDA(), command, context));
                } catch (Exception e) {
                    logger.warn(String.format("An error as occurred when executing command (%s)", command.getName()), e);
                }
            }
        }
    }
}
