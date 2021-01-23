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

package com.github.displug.displug.internal.entity.webhook;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Requester;
import okhttp3.RequestBody;

public class WebhookMessage {
    public static class Creation {
        private final String content;
        private final String username;
        private final String avatarUrl;
        private final boolean tts;
        private final MessageEmbed[] embeds;
        //private final String payloadJson; // - Used when Message contains file

        Creation(String content, String username, String avatarUrl, boolean tts, MessageEmbed[] embeds) {
            this.content = content;
            this.username = username;
            this.avatarUrl = avatarUrl;
            this.tts = tts;
            this.embeds = embeds;
        }

        public String getContent() {
            return content;
        }

        public String getUsername() {
            return username;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public boolean tts() {
            return tts;
        }

        public MessageEmbed[] getEmbeds() {
            return embeds;
        }

        public RequestBody asJSON() {
            return RequestBody.create(Requester.MEDIA_TYPE_JSON, getJSON().toJson());
        }

        public DataObject getJSON() {
            DataObject data = DataObject.empty();
            data.put("content", getContent())
                    .put("username", getUsername())
                    .put("avatar_url", getAvatarUrl())
                    .put("tts", tts());
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
            private String content;
            private String username;
            private String avatarUrl;
            private boolean tts;
            private MessageEmbed[] embeds;
            //private String payloadJson; // - Used when Message contains file

            public Builder clearContent() {
                return this.setContent("");
            }

            public Builder appendContent(String append) {
                return setContent(this.content + append);
            }

            public Builder setContent(String content) {
                this.content = content;
                return this;
            }

            public Builder setUsername(String username) {
                this.username = username;
                return this;
            }

            public Builder setAvatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
                return this;
            }

            public Builder tts() {
                return this.tts(true);
            }

            public Builder tts(boolean tts) {
                this.tts = tts;
                return this;
            }

            public Builder clearEmbed() {
                return this.setEmbeds();
            }

            public Builder setEmbeds(MessageEmbed... embeds) {
                this.embeds = embeds;
                return this;
            }

            public Creation build() {
                return new Creation(
                        content,
                        username,
                        avatarUrl,
                        tts,
                        embeds
                );
            }
        }
    }

    public static class Edit {
        private final String content;
        private final MessageEmbed[] embeds;

        Edit(String content, MessageEmbed[] embeds) {
            this.content = content;
            this.embeds = embeds;
        }

        public String getContent() {
            return content;
        }

        public MessageEmbed[] getEmbeds() {
            return embeds;
        }

        public RequestBody asJSON() {
            return RequestBody.create(Requester.MEDIA_TYPE_JSON, getJSON().toJson());
        }

        public DataObject getJSON() {
            DataObject data = DataObject.empty();
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
            private String content;
            private MessageEmbed[] embeds;

            public Builder clearContent() {
                return this.setContent("");
            }

            public Builder appendContent(String append) {
                return setContent(this.content + append);
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

            public Edit build() {
                return new Edit(
                        content,
                        embeds
                );
            }
        }
    }
}
