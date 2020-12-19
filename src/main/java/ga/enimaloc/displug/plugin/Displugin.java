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
package ga.enimaloc.displug.plugin;

import ga.enimaloc.displug.api.Displug;
import ga.enimaloc.displug.internal.objects.Indentable;
import ga.enimaloc.displug.internal.objects.Permissible;

public abstract class Displugin implements Permissible, Indentable {

    private final Displug displug;

    public Displugin(Displug displug) {
        this.displug = displug;
    }

    protected Displug getDisplug() {
        return displug;
    }

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();
}
