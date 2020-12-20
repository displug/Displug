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
package com.github.displug.displug.api.events.command;

import com.github.displug.displug.api.Command;
import com.github.displug.displug.internal.command.CommandContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;

public class CommandExecuted extends Event {

    private final Command executedCommand;
    private final CommandContext commandContext;

    public CommandExecuted(@NotNull JDA api, Command executedCommand, CommandContext commandContext) {
        super(api);
        this.executedCommand = executedCommand;
        this.commandContext = commandContext;
    }

    public Command getExecutedCommand() {
        return executedCommand;
    }

    public CommandContext getCommandContext() {
        return commandContext;
    }
}
