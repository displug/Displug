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
import java.util.Objects;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Requester;
import okhttp3.RequestBody;
import org.jetbrains.annotations.Nullable;

public class InteractionResponse {

    private final Type type;
    @Nullable
    private final Callback callback;

    public InteractionResponse(Type type) {
        this(type, null);
    }

    public InteractionResponse(Type type, @Nullable Callback callback) {
        if (type == Type.CHANNEL_MESSAGE || type == Type.CHANNEL_MESSAGE_WITH_SOURCE) {
            Objects.requireNonNull(callback, String.format("With %s type, callback cannot be null", type));
        }
        this.type = type;
        this.callback = callback;
    }

    public Type getType() {
        return type;
    }

    @Nullable
    public Callback getCallback() {
        return callback;
    }

    public RequestBody asJSON() {
        return RequestBody.create(Requester.MEDIA_TYPE_JSON, getJSON().toJson());
    }

    public DataObject getJSON() {
        DataObject data = DataObject.empty();
        data.put("type", getType().ordinal() + 1);
        if (getCallback() != null) {
            data.put("data", getCallback().getJSON());
        }
        return data;
    }

    public enum Type {
        PONG,
        ACKNOWLEDGE,
        CHANNEL_MESSAGE,
        CHANNEL_MESSAGE_WITH_SOURCE,
        ACKNOWLEDGE_WITH_SOURCE
    }

    public static class Callback {

        private final boolean tts;
        private final String content;
        @Nullable
        private final MessageEmbed[] embeds;

        Callback(boolean tts, String content, @Nullable MessageEmbed[] embeds) {
            if (embeds != null && embeds.length > 10) {
                throw new IllegalArgumentException("Only up to 10 embeds is supported");
            }
            this.tts = tts;
            this.content = content;
            this.embeds = embeds;
        }

        public boolean tts() {
            return tts;
        }

        public String getContent() {
            return content;
        }

        @Nullable
        public MessageEmbed[] getEmbeds() {
            return embeds;
        }

        public RequestBody asJSON() {
            return RequestBody.create(Requester.MEDIA_TYPE_JSON, getJSON().toJson());
        }

        public DataObject getJSON() {
            DataObject data = DataObject.empty();
            data.put("tts", tts());
            data.put("content", getContent());
            if (getEmbeds() != null && getEmbeds().length != 0) {
                data.put("embeds", DataArray.fromCollection(Arrays.stream(getEmbeds())
                        .filter(Objects::nonNull)
                        .map(MessageEmbed::toData)
                        .collect(Collectors.toList()))
                );
            }
            return data;
        }

        public static class Builder {
            private boolean tts;
            private String content;
            private MessageEmbed[] embeds;

            public Builder tts() {
                return tts(true);
            }

            public Builder tts(boolean tts) {
                this.tts = tts;
                return this;
            }

            public Builder clearContent() {
                return this.setContent("");
            }

            public Builder setContent(String content) {
                this.content = content;
                return this;
            }

            public Builder clearEmbed() {
                return this.setEmbeds();
            }

            public Builder setEmbeds(MessageEmbed... embeds) {
                this.embeds = embeds;
                return this;
            }

            public Callback build() {
                return new Callback(tts, content, embeds);
            }
        }
    }

}
