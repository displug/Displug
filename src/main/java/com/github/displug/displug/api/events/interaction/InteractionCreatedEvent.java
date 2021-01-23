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

package com.github.displug.displug.api.events.interaction;

import com.github.displug.displug.internal.entity.Interaction;
import com.github.displug.displug.internal.entity.InteractionResponse;
import com.github.displug.displug.internal.entity.webhook.WebhookMessage;
import com.github.displug.displug.internal.utils.RequestUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.utils.tuple.MutablePair;
import okhttp3.Response;

public class InteractionCreatedEvent extends Event {

    private final Interaction interaction;
    private final long createdAt;
    private final long applicationId;
    private final List<Long> followupSent;
    private boolean responseSend;

    public InteractionCreatedEvent(long responseNumber, Interaction interaction) {
        super(interaction.getJDA(), responseNumber);
        this.createdAt = System.currentTimeMillis();
        this.interaction = interaction;
        this.applicationId = getJDA().retrieveApplicationInfo().complete().getIdLong();
        this.responseSend = false;
        this.followupSent = new ArrayList<>();
    }

    public Interaction getInteraction() {
        return interaction;
    }

    public void sendResponse(InteractionResponse response) {
        if (this.alreadySendResponse()) {
            throw new IllegalStateException("This method can only be invoked one time");
        }
        if (!this.lessThanThreeSeconds()) {
            throw new IllegalStateException("This action can only be done within 3 seconds of the InteractionCreated event");
        }
        this.responseSend = true;
        RequestUtils.makeRequest(
                false,
                getJDA(),
                Route.post(String.format("interactions/%s/%s/callback", getInteraction().getId(), getInteraction().getToken())).compile(),
                response.asJSON()
        );
    }

    public boolean alreadySendResponse() {
        return responseSend;
    }

    public boolean lessThanThreeSeconds() {
        return System.currentTimeMillis() - this.createdAt < TimeUnit.SECONDS.toMillis(3);
    }

    public boolean lessThanFiftiethMinutes() {
        return System.currentTimeMillis() - this.createdAt > TimeUnit.MINUTES.toMillis(15);
    }

    public boolean canSendResponse() {
        return lessThanThreeSeconds() && !alreadySendResponse();
    }

    public void create(WebhookMessage.Creation message) {
        verify();
        Response response = RequestUtils.makeRequest(
                false,
                getJDA(),
                Route.post(String.format("webhooks/%s/%s?wait=true", this.applicationId, this.interaction.getToken())).compile(),
                message.asJSON(),
                new MutablePair<>("accept-encoding", "gzip")
        );
        System.out.println("response = " + response);
    }

    public void editOriginal(WebhookMessage.Edit message) {
        edit(-1, message);
    }

    public void edit(long messageId, WebhookMessage.Edit message) {
        String toEdit = String.valueOf(messageId);
        if (messageId == -1) {
            if (!responseSend) {
                throw new UnsupportedOperationException("No original message sent");
            }
            toEdit = "@original";
        }
        RequestUtils.makeRequest(
                false,
                getJDA(),
                Route.patch(String.format("webhooks/%s/%s/messages/%s", this.applicationId, this.interaction.getToken(), toEdit)).compile(),
                message.asJSON()
        );
    }

    public void deleteOriginal() {
        delete(-1);
    }

    public void delete(long messageId) {
        String toDelete = String.valueOf(messageId);
        if (messageId == -1) {
            if (!responseSend) {
                throw new UnsupportedOperationException("No original message sent");
            }
            toDelete = "@original";
        }
        RequestUtils.makeRequest(
                false,
                getJDA(),
                Route.delete(String.format("webhooks/%s/%s/messages/%s", this.applicationId, this.interaction.getToken(), toDelete)).compile(),
                null
        );
    }

    private void verify() {
        if (lessThanFiftiethMinutes()) {
            throw new UnsupportedOperationException("The token is invalid for this interaction");
        }
    }
}
