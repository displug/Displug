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
package ga.enimaloc.displug.internal;

import java.lang.reflect.InvocationTargetException;
import org.slf4j.impl.Logger;

@SuppressWarnings("unused")
public enum ExitCode {

    OK(0),
    JDA_RELATED(1001),
    CONFIGURATION_RELATED(1002),
    PLUGIN_RELATED(1003),
    COMMAND_RELATED(1004),
    EVENT_RELATED(1005),
    UNKNOWN(1999);

    private final int code;

    ExitCode(int code) {
        this.code = code;
    }

    public void exit() {
        System.exit(code);
    }

    public void exit(Logger logger, String message) {
        exit(logger, Level.INFO, message);
    }

    public void exit(Logger logger, Level level, String message) {
        exit(logger, level, message, (Throwable) null);
    }

    public void exit(Class<? extends Throwable> clazz) {
        exit(null, Level.CONSOLE, "", clazz);
    }

    public void exit(Throwable throwable) {
        exit(null, Level.CONSOLE, "", throwable);
    }

    public void exit(Logger logger, Class<? extends Throwable> clazz) {
        exit(logger, "", clazz);
    }

    public void exit(Logger logger, Throwable throwable) {
        exit(logger, "", throwable);
    }

    public void exit(Logger logger, String message, Class<? extends Throwable> clazz) {
        exit(logger, Level.ERROR, message, clazz);
    }

    public void exit(Logger logger, String message, Throwable throwable) {
        exit(logger, Level.ERROR, message, throwable);
    }

    public void exit(Logger logger, Level level, Class<? extends Throwable> clazz) {
        exit(logger, level, "", clazz);
    }

    public void exit(Logger logger, Level level, Throwable throwable) {
        exit(logger, level, "", throwable);
    }

    public void exit(Logger logger, Level level, String message, Class<? extends Throwable> clazz) {
        try {
            thr(clazz);
        } catch (Throwable throwable) {
            exit(logger, level, message, throwable);
        }
    }

    public void exit(Logger logger, Level level, String message, Throwable throwable) {
        switch (level) {
            case TRACE -> logger.trace(message, throwable);
            case DEBUG -> logger.debug(message, throwable);
            case INFO -> logger.info(message, throwable);
            case WARN -> logger.warn(message, throwable);
            case ERROR -> logger.error(message, throwable);
            case CONSOLE -> {
                if (logger == null) {
                    System.out.println(message);
                } else {
                    logger.console(message);
                }
                if (throwable != null) {
                    throwable.printStackTrace();
                }
            }
        }
        System.exit(code);
    }

    private void thr(Class<? extends Throwable> clazz) throws Throwable {
        try {
            throw clazz.cast(clazz.getDeclaredConstructor(new Class[]{}).newInstance());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
        }
    }

    enum Level {
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE,
        CONSOLE
    }
}
