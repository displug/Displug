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

package com.github.displug.displug.internal.entity;

import com.github.displug.displug.api.Command;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.Nullable;

public class ApplicationCommand extends Interaction {

    private final CommandData commandData;

    public ApplicationCommand(DataObject data, JDA jda) {
        super(
                data.getLong("id"),
                Type.get(data.getInt("type")),
                data.getLong("guild_id"),
                data.getLong("channel_id"),
                data.getObject("member").getObject("user").getLong("id"),
                data.getString("token"),
                data.getInt("version"),
                jda
        );
        this.commandData = new CommandData(data.getObject("data"));
        //noinspection unchecked I'm sure about the cast
    }

    public CommandData getCommandData() {
        return commandData;
    }

    public static class CommandData implements ISnowflake {
        private final long id;
        private final String name;
        private final String description;
        @Nullable
        private final Option[] options;

        public CommandData(DataObject data) {
            this(
                    data.getLong("id"),
                    data.getString("name"),
                    data.getString("description", ""),
                    data.hasKey("options") ? data.getArray("options").toList().stream().map(obj -> new Option((Map<String, Object>) obj)).toArray(Option[]::new) : null
            );
        }

        public CommandData(Command command) {
            this(-1, command.getName(), command.getDescription(), command.getArguments().getOptions().stream().map(Option::new).toArray(Option[]::new));
        }

        public CommandData(long id, String name, String description, Option... options) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.options = options;
        }

        @Nullable
        public Option[] getOptions() {
            return options;
        }

        @Override
        public long getIdLong() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String toJson() {
            return "{" +
                    (getIdLong() != -1 ? "\"id\":" + getId() + "," : "") +
                    String.format("\"name\":\"%s\",\"description\":\"%s\"", getName(), getDescription()) +
                    (getOptions() != null && getOptions().length != 0 ? ",\"options\":[" + Arrays.stream(getOptions()).filter(Objects::nonNull).sorted((o1, o2) -> o1.isRequired() ? -1 : o2.isRequired() ? 1 : 0).map(Option::toJson).collect(Collectors.joining(",")) + "]" : "") +
                    "}";
        }

        public static class Option {

            private final String name;
            @Nullable
            private final Object value;
            private final String description;
            private final boolean _default;
            private final boolean required;
            private final Type type;
            @Nullable
            private final Option[] options;

            public Option(org.apache.commons.cli.Option opt) {
                this(opt.getLongOpt(), null, opt.getDescription(), false, opt.isRequired(), Type.typeOf(opt.getType()), new Option[]{});
            }

            public Option(Map<String, Object> data) {
                this(
                        (String) data.get("name"),
                        data.getOrDefault("value", null),
                        (String) data.getOrDefault("description", null),
                        (boolean) data.getOrDefault("default", false),
                        (boolean) data.getOrDefault("required", false),
                        Type.values()[((int) data.getOrDefault("type", 3)) - 1],
                        data.containsKey("options") ? ((ArrayList<Map<String, Object>>) data.get("options")).stream().map(Option::new).toArray(Option[]::new) : null
                );
            }

            public Option(String name, @Nullable Object value, String description, boolean _default, boolean required, Type type, @Nullable Option[] options) {
                this.name = name;
                this.value = value;
                this.description = description;
                this._default = _default;
                this.required = required;
                this.type = type;
                this.options = options;
            }

            public String getName() {
                return name;
            }

            @Nullable
            public Object getValue() {
                return value;
            }

            public String getDescription() {
                return description;
            }

            public boolean isDefault() {
                return _default;
            }

            public boolean isRequired() {
                return required;
            }

            public Type getType() {
                return type;
            }

            @Nullable
            public Option[] getOptions() {
                return options;
            }

            public String toJson() {
                return "{" +
                        String.format("\"type\":%d,\"name\":\"%s\",\"description\":\"%s\"", getType().ordinal() + 1, getName(), getDescription()) +
                        (isDefault() ? ",\"default\":true" : "") +
                        (isRequired() ? ",\"required\":true" : "") +
                        (getOptions() != null && getOptions().length != 0 ? ",\"options\":[" + Arrays.stream(getOptions()).filter(Objects::nonNull).sorted((o1, o2) -> o1.isRequired() ? -1 : o2.isRequired() ? 1 : 0).map(Option::toJson).collect(Collectors.joining(",")) + "]" : "") +
                        "}";
            }

            public enum Type {
                SUB_COMMAND(Type.class),
                SUB_COMMAND_GROUP(Type.class),
                STRING(String.class),
                INTEGER(Integer.class),
                BOOLEAN(Boolean.class),
                USER(User.class, Member.class),
                CHANNEL(GuildChannel.class),
                ROLE(Role.class);

                private final Class<?>[] type;

                Type(Class<?>... type) {
                    this.type = type;
                }

                public static Type typeOf(Object object) {
                    for (Type value : values()) {
                        for (Class<?> clazz : value.type) {
                            if (clazz == object) {
                                return value;
                            }
                        }
                    }
                    return STRING;
                }
            }

        }

    }
}
