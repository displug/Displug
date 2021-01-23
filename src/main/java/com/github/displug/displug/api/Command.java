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
package com.github.displug.displug.api;

import com.github.displug.displug.internal.command.CommandContext;
import com.github.displug.displug.internal.command.CommandResult;
import com.github.displug.displug.internal.objects.Permissible;
import org.apache.commons.cli.Options;

@SuppressWarnings({"SameReturnValue", "RedundantThrows"})
public interface Command extends Permissible {

    String getName();

    default String[] getAliases() {
        return new String[0];
    }

    default String getDescription() {
        return "";
    }

    default Category[] getCategory() {
        return null;
    }

    default Options getArguments() {
        return null;
    }

    default boolean isSlashCommand() {
        return false;
    }

    default long forSpecificGuild() {
        return -1;
    }

    CommandResult execute(CommandContext context) throws Exception;

}
