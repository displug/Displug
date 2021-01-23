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

package com.github.displug.displug.internal.utils;

import com.github.displug.displug.internal.entity.ApplicationCommand;
import java.io.IOException;
import java.util.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.utils.tuple.MutablePair;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

public class RequestUtils {

    public static final String DISCORD_API_PREFIX = String.format("https://discord.com/api/v%d/", 8);

    public static List<ApplicationCommand.CommandData> getAlreadyRegisteredCommand(JDA jda) {
        return getAlreadyRegisteredCommand(makeRequest(jda, Route.get(String.format("applications/%s/commands", jda.getSelfUser().getId())).compile(), null));
    }

    public static List<ApplicationCommand.CommandData> getAlreadyRegisteredCommand(Guild guild) {
        return getAlreadyRegisteredCommand(makeRequest(guild.getJDA(), Route.get(String.format("applications/%s/guilds/%s/commands", guild.getJDA().getSelfUser().getId(), guild.getId())).compile(), null));
    }

    private static List<ApplicationCommand.CommandData> getAlreadyRegisteredCommand(Response response) {
        if (response == null || response.code() != 200) {
            return null;
        }
        DataArray dataArray = DataArray.fromJson(Objects.requireNonNull(Objects.requireNonNull(response).body()).byteStream());
        List<ApplicationCommand.CommandData> registeredCommands = new ArrayList<>();
        for (Object o : dataArray.toList()) {
            //noinspection unchecked
            HashMap<String, Object> data = (HashMap<String, Object>) o;
            //noinspection unchecked
            registeredCommands.add(
                    new ApplicationCommand.CommandData(
                            Long.parseLong((String) data.get("id")),
                            (String) data.get("name"),
                            (String) data.get("description"),
                            ((ArrayList<Map<String, Object>>) data.getOrDefault("options", new ArrayList<Map<String, Object>>()))
                                    .stream()
                                    .map(ApplicationCommand.CommandData.Option::new)
                                    .toArray(ApplicationCommand.CommandData.Option[]::new)
                    )
            );
        }
        return registeredCommands;
    }

    public static boolean removeCommand(JDA jda, ApplicationCommand.CommandData data) {
        Response response = makeRequest(jda, Route.delete(String.format("applications/%s/commands/%s", jda.getSelfUser().getId(), data.getId())).compile(), null);
        return response != null && response.code() == 204;
    }

    public static boolean removeCommand(Guild guild, ApplicationCommand.CommandData data) {
        Response response = makeRequest(guild.getJDA(), Route.delete(String.format("applications/%s/guilds/%s/commands/%s", guild.getJDA().getSelfUser().getId(), guild.getId(), data.getId())).compile(), null);
        return response != null && response.code() == 204;
    }

    public static boolean addCommand(JDA jda, ApplicationCommand.CommandData data) {
        String content = data.toJson();
        Response response = makeRequest(jda, Route.post(String.format("applications/%s/commands", jda.getSelfUser().getId())).compile(), RequestBody.create(MediaType.get("application/json"), content));
        return response != null && response.code() == 201;
    }

    public static boolean addCommand(Guild guild, ApplicationCommand.CommandData data) {
        String content = data.toJson();
        Response response = makeRequest(guild.getJDA(), Route.post(String.format("applications/%s/guilds/%s/commands", guild.getJDA().getSelfUser().getId(), guild.getId())).compile(), RequestBody.create(MediaType.get("application/json"), content));
        return response != null && response.code() == 201;
    }

    public static Response makeRequest(JDA jda, Route.CompiledRoute route, @Nullable RequestBody body, @Nullable MutablePair<String, String>... header) {
        return makeRequest(true, jda, route, body, header);
    }

    @SafeVarargs
    public static Response makeRequest(boolean needAuth, JDA jda, Route.CompiledRoute route, @Nullable RequestBody body, @Nullable MutablePair<String, String>... header) {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.method(route.getMethod().name(), body).url(DISCORD_API_PREFIX + route.getCompiledRoute());
        if (needAuth) {
            builder.addHeader("Authorization", jda.getToken());
        }
        for (MutablePair<String, String> pair : header) {
            builder.addHeader(Objects.requireNonNull(pair).getLeft(), pair.getRight());
        }
        try {
            return jda.getHttpClient().newCall(builder.build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
