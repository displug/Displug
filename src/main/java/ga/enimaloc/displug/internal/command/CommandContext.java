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
package ga.enimaloc.displug.internal.command;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.cli.CommandLine;

public class CommandContext {

    private final MessageReceivedEvent event;
    private final String arguments;
    private final CommandLine cliArgument;

    public CommandContext(MessageReceivedEvent event, String arguments, CommandLine cliArgument) {
        this.event = event;
        this.arguments = arguments;
        this.cliArgument = cliArgument;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public ChannelType getType() {
        return event.getChannelType();
    }

    public User getAuthor() {
        return event.getAuthor();
    }

    public Message getMessage() {
        return event.getMessage();
    }

    public String getArguments() {
        return arguments;
    }

    public String[] getSplitArguments() {
        return arguments.split(" ");
    }

    public CommandLine getCliArgument() {
        return cliArgument;
    }
}
