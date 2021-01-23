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

import java.util.Arrays;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Interaction implements ISnowflake {

    private final long id;
    private final Type type;
    private final long guildId;
    private final long channelId;
    private final long memberId;
    private final String token;
    private final int version;
    private final JDA jda;

    public Interaction(long id, Type type, long guildId, long channelId, long memberId, String token, int version, JDA jda) {
        this.id = id;
        this.type = type;
        this.guildId = guildId;
        this.channelId = channelId;
        this.memberId = memberId;
        this.token = token;
        this.version = version;
        this.jda = jda;
    }

    @Override
    public long getIdLong() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    public int getVersion() {
        return version;
    }

    public JDA getJDA() {
        return jda;
    }

    public Guild getGuild() {
        return getJDA().getGuildById(guildId);
    }

    public TextChannel getTextChannel() {
        return getJDA().getTextChannelById(channelId);
    }

    public Member getMember() {
        return getGuild().retrieveMemberById(memberId).complete();
    }

    public enum Type {
        PING(1),
        APPLICATION_COMMAND(2),
        UNKNOWN(0);

        private final int i;

        Type(int i) {
            this.i = i;
        }

        public static Type get(int i) {
            return Arrays.stream(values()).filter(type -> type.i == i).findFirst().orElse(UNKNOWN);
        }
    }
}
