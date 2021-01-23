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
package com.github.displug.displug.internal;

import com.github.displug.displug.api.Command;
import com.github.displug.displug.api.Displug;
import com.github.displug.displug.api.events.EventListener;
import com.github.displug.displug.internal.managers.CommandManager;
import com.github.displug.displug.internal.managers.EventManager;
import com.github.displug.displug.internal.managers.PluginManager;
import com.github.displug.displug.plugin.Displugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.event.Level;
import org.slf4j.impl.Logger;
import org.slf4j.impl.StaticLoggerBinder;

import javax.security.auth.login.LoginException;

public class DisplugImpl implements Displug {

    private final List<Permission> requiredPermission;
    private final Configuration configuration;
    private final CommandManager commandManager;
    private final PluginManager pluginManager;
    private JDA jda;

    private final Logger logger = StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger(this.getClass().getInterfaces()[0]);

    public DisplugImpl() {
        requiredPermission = new ArrayList<>();
        configuration = new Configuration();
        commandManager = new CommandManager(this);
        pluginManager = new PluginManager(this);
    }

    public void start() {
        setupConfiguration();
        setupPlugins();
        setupJDA();
    }

    private void setupConfiguration() {
        if (!Configuration.DEFAULT_CONFIGURATION_FILE.exists()) {
            int createTry = 0;
            while (!Configuration.DEFAULT_CONFIGURATION_FILE.getParentFile().mkdirs() && !Configuration.DEFAULT_CONFIGURATION_FILE.getParentFile().exists()) {
                createTry++;
                if (createTry >= 5) {
                    ExitCode.CONFIGURATION_RELATED.exit(
                            logger,
                            ExitCode.Level.ERROR,
                            String.format(
                                    "Cannot create '%s' after %s try",
                                    Configuration.DEFAULT_CONFIGURATION_FILE.getParentFile().getAbsolutePath(),
                                    createTry
                            )
                    );
                }
            }
            configuration.setDefault();
            configuration.save();
            ExitCode.CONFIGURATION_RELATED.exit(logger, ExitCode.Level.INFO, "A new configuration file was created please modify it!");
        }
        configuration.load();
        StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger("ROOT").setLevel(configuration.bot.logLevel == null ? Level.INFO : configuration.bot.logLevel);
    }

    private void setupPlugins() {
        pluginManager.loadPlugins();
        //            getJDA().getEventManager().handle(new PluginStarted(getJDA(), displugin));
        pluginManager.all().forEach(Displugin::onEnable);
    }

    private void setupJDA() {
        List<GatewayIntent> intents = new ArrayList<>();
        pluginManager.all().forEach(plugin -> intents.addAll(Arrays.asList(plugin.getIntents())));

        if (JDAInfo.DISCORD_REST_VERSION < 8) {
            logger.info("JDA ", JDAInfo.DISCORD_REST_VERSION);
        }


        try {
            jda = JDABuilder.create(configuration.bot.token, intents).setEventManager(new EventManager()).addEventListeners(commandManager, new EventListener()).setRawEventsEnabled(true).build();
            commandManager.syncWithDiscord();
        } catch (LoginException e) {
            ExitCode.JDA_RELATED.exit(logger, e);
        }
    }

    public List<Permission> getRequiredPermission() {
        return requiredPermission;
    }

    @Override
    public String getInviteUrl() {
        return getJDA().getInviteUrl(getRequiredPermission());
    }

    @Override
    public void addCommand(Command command) {
        addCommands(command);
    }

    @Override
    public void addCommands(Command... commands) {
        for (Command command : commands) {
            commandManager.add("", command);
        }
    }

    @Override
    public void removeCommands(Command... commands) {
        for (Command command : commands) {
            commandManager.remove(command);
        }
    }

    @Override
    public void shutdown() {
        shutdown(ExitCode.OK);
    }

    @Override
    public void shutdown(ExitCode exitCode) {
        shutdown(v -> exitCode.exit());
    }

    @Override
    public void shutdown(Consumer<Void> after) {
        pluginManager.all().forEach(Displugin::onDisable);
        getJDA().shutdown();
        after.accept(null);
    }

    @Override
    public JDA getJDA() {
        return jda;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
