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
package com.github.displug.displug.internal;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import java.io.File;
import java.io.IOException;
import org.slf4j.event.Level;
import org.slf4j.impl.Logger;
import org.slf4j.impl.StaticLoggerBinder;

public class Configuration {

    public final static File DEFAULT_CONFIGURATION_FILE = new File("data", "displug.toml");

    private final File configurationFile;
    private final Toml defaultConfiguration;

    public Bot bot;

    private final Logger logger = StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger(this.getClass());

    public Configuration() {
        this(DEFAULT_CONFIGURATION_FILE);
    }

    public Configuration(File configurationFile) {
        this(configurationFile, getDefault());
    }

    public Configuration(File configurationFile, Toml defaultConfiguration) {
        this.configurationFile = configurationFile;
        this.defaultConfiguration = defaultConfiguration;
        this.bot = new Bot();
    }

    private static Bot createDefault() {
        Bot _default = new Bot();
        _default.prefix = new String[]{"!"};
        _default.token = "Insert your token here";
        _default.logLevel = Level.INFO;
        return _default;
    }

    private static Toml getDefault() {
        return new Toml().read(new TomlWriter().write(createDefault()));
    }

    public void load() {
        this.bot = new Toml(defaultConfiguration).read(configurationFile).to(Bot.class);
    }

    public void save() {
        try {
            new TomlWriter().write(bot, configurationFile);
        } catch (IOException e) {
            ExitCode.CONFIGURATION_RELATED.exit(logger, e);
        }
    }

    public void setDefault() {
        bot = createDefault();
    }

    public static class Bot {
        public String[] prefix;
        String token;
        Level logLevel;
    }
}
