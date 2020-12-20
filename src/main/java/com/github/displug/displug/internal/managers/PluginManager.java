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
package com.github.displug.displug.internal.managers;

import com.github.displug.displug.api.Displug;
import com.github.displug.displug.api.events.plugin.PluginLoaded;
import com.github.displug.displug.internal.DisplugImpl;
import com.github.displug.displug.internal.ExitCode;
import com.github.displug.displug.internal.exception.PluginException;
import com.github.displug.displug.plugin.Displugin;
import com.moandjiezana.toml.Toml;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import org.slf4j.impl.Logger;
import org.slf4j.impl.StaticLoggerBinder;

public class PluginManager extends SManager<Displugin> {

    public static final File DEFAULT_PLUGINS_FOLDER = new File("data", "plugins");

    private final Displug displug;
    private final File pluginsFolder;

    private final Logger logger = StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger(this.getClass());

    public PluginManager(Displug displug) {
        this(displug, PluginManager.DEFAULT_PLUGINS_FOLDER);
    }

    public PluginManager(Displug displug, File pluginsFolder) {
        this.pluginsFolder = pluginsFolder;
        //noinspection ResultOfMethodCallIgnored
        this.pluginsFolder.mkdirs();
        this.displug = displug;
    }

    public void loadPlugins() {
        for (File file : pluginsFolder.listFiles()) {
            loadPlugin(file);
        }
    }

    public void loadPlugin(File file) {
        try {
            try {
                if (file.isDirectory() || !file.getName().endsWith(".jar")) {
                    return;
                }
                logger.trace("Loading {}...", file.getName());
                URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
                InputStream pluginInfoInputStream = classLoader.getResourceAsStream("plugin.yml");
                if (pluginInfoInputStream == null) {
                    throw new PluginException("Can't find plugin.yml in jar");
                }
                Map<String, Object> pluginInfo = new Toml().read(pluginInfoInputStream).toMap();
                String name = (String) pluginInfo.get("name");
                String main = (String) pluginInfo.get("main");
                String author = (String) pluginInfo.get("author");
                String version = (String) pluginInfo.get("version");
                Objects.requireNonNull(name);
                Objects.requireNonNull(main);
                Objects.requireNonNull(author);
                Objects.requireNonNull(version);
                Class<?> mainClass = classLoader.loadClass(main);
                if (!Displugin.class.isAssignableFrom(mainClass)) {
                    throw new PluginException("Main class of a plugin not extend Displugin class");
                }
                Displugin plugin = (Displugin) mainClass.getConstructor(Displug.class).newInstance(displug);
                ((DisplugImpl) displug).getRequiredPermission().addAll(Arrays.asList(plugin.getPermissions()));
                displug.getJDA().getEventManager().handle(new PluginLoaded(displug.getJDA(), plugin));
                plugin.onLoad();
                add(plugin);
            } catch (MalformedURLException e) {
                throw new PluginException(e);
            } catch (NullPointerException e) {
                throw new PluginException("Maybe some attributes are missing in plugin.yml", e);
            } catch (ClassNotFoundException e) {
                throw new PluginException("Cannot find main class", e);
            } catch (NoSuchMethodException e) {
                throw new PluginException("Can't find constructor", e);
            } catch (InvocationTargetException e) {
                throw new PluginException("A error as been thrown in main constructor", e);
            } catch (InstantiationException e) {
                throw new PluginException("Can't create plugin main class object", e);
            } catch (IllegalAccessException e) {
                throw new PluginException("Can't access to plugin constructor", e);
            } catch (Exception e) {
                throw new PluginException("???", e);
            }
        } catch (PluginException pluginException) {
            ExitCode.PLUGIN_RELATED.exit(logger, "Error while loading " + file.getName(), pluginException);
        }
    }
}
