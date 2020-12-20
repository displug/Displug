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

import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventManager extends SManager<EventListener> implements IEventManager {

    // Disallow access Manager method
    @Override
    public void add(EventListener object) {
        try {
            throw new IllegalAccessException("You can't access to that please use register method");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(int i) {
        try {
            throw new IllegalAccessException("You can't access to that please use unregister method");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(EventListener object) {
        try {
            throw new IllegalAccessException("You can't access to that please use unregister method");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public EventListener get(int i) {
        try {
            throw new IllegalAccessException("You can't access");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<EventListener> all() {
        try {
            throw new IllegalAccessException("You can't access");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void register(@NotNull Object listener) {
        if (!(listener instanceof EventListener) || listener instanceof ListenerAdapter) {
            throw new ClassCastException("A listener need to extend EventListener");
        }
        super.add((EventListener) listener);
    }

    @Override
    public void unregister(@NotNull Object listener) {
        if (!(listener instanceof EventListener) || listener instanceof ListenerAdapter) {
            throw new ClassCastException("A listener need to extend EventListener");
        }
        super.remove((EventListener) listener);
    }

    @Override
    public void handle(@NotNull GenericEvent event) {
        super.all().forEach(eventListener -> eventListener.onEvent(event));
    }

    @NotNull
    @Override
    public List<Object> getRegisteredListeners() {
        return Arrays.asList(super.all().toArray());
    }
}
