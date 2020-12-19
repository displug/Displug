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
package ga.enimaloc.displug.api;

import ga.enimaloc.displug.internal.Configuration;
import ga.enimaloc.displug.internal.ExitCode;
import java.util.function.Consumer;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;

public interface Displug {

    JDA getJDA();

    String getInviteUrl();

    Configuration getConfiguration();

    @Deprecated
    @ReplaceWith("Displug#addCommands(Command...)")
    void addCommand(Command command);

    void addCommands(Command... commands);

    void removeCommands(Command... commands);

    void shutdown();

    void shutdown(ExitCode exitCode);

    void shutdown(Consumer<Void> after);

}
