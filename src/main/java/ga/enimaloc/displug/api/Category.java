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
package ga.enimaloc.displug.api;

import java.awt.*;
import java.util.function.Consumer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class Category {

    private final String name;
    private final String description;
    private final Color color;
    private final Consumer<MessageReceivedEvent> onCommand;
    private final Consumer<PrivateMessageReceivedEvent> onPrivateCommand;
    private final Consumer<GuildMessageReceivedEvent> onGuildCommand;

    Category(
            String name,
            String description,
            Color color,
            Consumer<MessageReceivedEvent> onCommand,
            Consumer<PrivateMessageReceivedEvent> onPrivateCommand,
            Consumer<GuildMessageReceivedEvent> onGuildCommand
    ) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.onCommand = onCommand;
        this.onPrivateCommand = onPrivateCommand;
        this.onGuildCommand = onGuildCommand;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Color getColor() {
        return color;
    }

    public Consumer<MessageReceivedEvent> getOnCommand() {
        return onCommand;
    }

    public Consumer<PrivateMessageReceivedEvent> getOnPrivateCommand() {
        return onPrivateCommand;
    }

    public Consumer<GuildMessageReceivedEvent> getOnGuildCommand() {
        return onGuildCommand;
    }

    public static class Builder {

        private String name;
        private String description;
        private Color color;
        private Consumer<MessageReceivedEvent> onCommand;
        private Consumer<PrivateMessageReceivedEvent> onPrivateCommand;
        private Consumer<GuildMessageReceivedEvent> onGuildCommand;

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void setOnCommand(Consumer<MessageReceivedEvent> onCommand) {
            this.onCommand = onCommand;
        }

        public void setOnPrivateCommand(Consumer<PrivateMessageReceivedEvent> onPrivateCommand) {
            this.onPrivateCommand = onPrivateCommand;
        }

        public void setOnGuildCommand(Consumer<GuildMessageReceivedEvent> onGuildCommand) {
            this.onGuildCommand = onGuildCommand;
        }

        public Category build() {
            return new Category(name, description, color, onCommand, onPrivateCommand, onGuildCommand);
        }
    }
}
