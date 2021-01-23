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

import com.github.displug.displug.internal.entity.InteractionResponse;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.dv8tion.jda.api.entities.Emote;

public enum CommandResult {

    REPLY((context, input) -> {
        if (context.isInteraction()) {
            context.getInteractionCreatedEvent().sendResponse(
                    new InteractionResponse(InteractionResponse.Type.CHANNEL_MESSAGE_WITH_SOURCE,
                            new InteractionResponse.Callback.Builder().setContent(input.toString()).build()
                    )
            );
        } else {
            context.getChannel().sendMessage(input.toString()).queue();
        }
    }),
    REACT_UNICODE((context, input) -> {
        if (!context.isInteraction()) {
            Objects.requireNonNull(context.getMessage()).addReaction((String) input).queue();
        }
    }),
    REACT_EMOTE((context, input) -> {
        if (!context.isInteraction()) {
            Objects.requireNonNull(context.getMessage()).addReaction((Emote) input).queue();
        }
    });

    private final BiConsumer<CommandContext, Object> action;
    private Object input;
    private CommandResult next;

    CommandResult(BiConsumer<CommandContext, Object> action) {
        this.action = action;
    }

    public CommandResult setInput(Object input) {
        this.input = input;
        return this;
    }

    public void execute(CommandContext context) {
        action.accept(context, input);
        if (next != null) {
            next.execute(context);
        }
    }

    public CommandResult next(CommandResult commandResult) {
        next = commandResult;
        return this;
    }
}
