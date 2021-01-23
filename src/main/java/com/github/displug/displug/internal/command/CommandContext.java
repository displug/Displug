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
package com.github.displug.displug.internal.command;

import com.github.displug.displug.api.events.interaction.InteractionCreatedEvent;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.cli.CommandLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandContext {

    private final @NotNull GenericEvent event;
    private final String arguments;
    private final CommandLine cliArgument;

    public CommandContext(@NotNull GenericEvent event, String arguments, CommandLine cliArgument) {
        this.event = event;
        this.arguments = arguments;
        this.cliArgument = cliArgument;
    }

    public @NotNull GenericEvent getGenericEvent() {
        return event;
    }

    public boolean isInteraction() {
        return event instanceof InteractionCreatedEvent;
    }

    public @NotNull InteractionCreatedEvent getInteractionCreatedEvent() {
        if (!isInteraction()) {
            throw new ClassCastException("This command was not executed from interaction");
        }
        return (InteractionCreatedEvent) event;
    }

    public @NotNull MessageReceivedEvent getMessageReceivedEvent() {
        if (isInteraction()) {
            throw new ClassCastException("This command was not executed from message");
        }
        return (MessageReceivedEvent) event;
    }

    public ChannelType getType() {
        return isInteraction() ? getInteractionCreatedEvent().getInteraction().getTextChannel().getType() : getMessageReceivedEvent().getChannelType();
    }

    public User getAuthor() {
        return isInteraction() ? getInteractionCreatedEvent().getInteraction().getMember().getUser() : getMessageReceivedEvent().getAuthor();
    }

    @Nullable
    public Message getMessage() {
        return isInteraction() ? null : getMessageReceivedEvent().getMessage();
    }

    public TextChannel getChannel() {
        return isInteraction() ? getInteractionCreatedEvent().getInteraction().getTextChannel() : getMessageReceivedEvent().getTextChannel();
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
