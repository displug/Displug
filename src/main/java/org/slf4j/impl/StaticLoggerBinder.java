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
package org.slf4j.impl;

import java.io.IOException;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * Origin Code by SLF4J-Simple [Link=https://github.com/qos-ch/slf4j/tree/master/slf4j-simple]
 * Modified by enimaloc
 */
public class StaticLoggerBinder implements LoggerFactoryBinder
{
    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    private final LoggerFactory loggerFactory;

    public StaticLoggerBinder() {
        LoggerFactory loggerFactory = null;
        try {
            loggerFactory = new LoggerFactory();
        }catch (IOException ignored){}

        this.loggerFactory = loggerFactory;
    }

    @Override
    public LoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public String getLoggerFactoryClassStr() {
        return loggerFactory.getClass().getName();
    }
}
