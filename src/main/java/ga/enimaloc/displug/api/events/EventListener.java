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
package ga.enimaloc.displug.api.events;

import ga.enimaloc.displug.api.events.command.CommandExecuted;
import ga.enimaloc.displug.api.events.command.CommandRegister;
import ga.enimaloc.displug.api.events.plugin.PluginLoaded;
import ga.enimaloc.displug.api.events.plugin.PluginStarted;
import ga.enimaloc.displug.api.events.plugin.PluginStopped;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventListener extends ListenerAdapter {

    public void onPluginLoaded(PluginLoaded event) {
    }

    public void onPluginStarted(PluginStarted event) {
    }

    public void onPluginStopped(PluginStopped event) {
    }

    public void onCommandRegister(CommandRegister event) {
    }

    public void onCommandExecuted(CommandExecuted event) {
    }

    @Override
    public void onGenericEvent(@NotNull GenericEvent event) {

        // Plugin related
        if (event instanceof PluginLoaded) {
            onPluginLoaded((PluginLoaded) event);
        } else if (event instanceof PluginStarted) {
            onPluginStarted((PluginStarted) event);
        } else if (event instanceof PluginStopped) {
            onPluginStopped((PluginStopped) event);
        }

        // Command related
        if (event instanceof CommandRegister) {
            onCommandRegister((CommandRegister) event);
        } else if (event instanceof CommandExecuted) {
            onCommandExecuted((CommandExecuted) event);
        }

        super.onGenericEvent(event);
    }
}
