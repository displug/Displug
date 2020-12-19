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

import java.util.function.BiConsumer;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;

public enum CommandResult {

    REPLY((message, input) -> message.getChannel().sendMessage(input.toString()).queue()),
    REACT_UNICODE   ((message, input) -> message.addReaction((String) input).queue()),
    REACT_EMOTE     ((message, input) -> message.addReaction((Emote) input).queue());

    private final BiConsumer<Message, Object> action;
    private Object input;
    private CommandResult next;

    CommandResult(BiConsumer<Message, Object> action) {
        this.action = action;
    }

    public CommandResult setInput(Object input) {
        this.input = input;
        return this;
    }

    public void execute(Message message) {
        action.accept(message, input);
        if (next != null) {
            next.execute(message);
        }
    }

    public CommandResult next(CommandResult commandResult) {
        next = commandResult;
        return this;
    }
}
