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
package ga.enimaloc.displug.internal.managers;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.list.UnmodifiableList;

public class SManager<T> {

    private final List<T> objects;

    public SManager() {
        this.objects = new ArrayList<>();
    }

    public void add(T object) {
        this.objects.add(object);
    }

    public void remove(int i) {
        this.objects.remove(i);
    }

    public void remove(T object) {
        this.objects.remove(object);
    }

    public T get(int i) {
        return this.objects.get(i);
    }

    public List<T> all() {
        return UnmodifiableList.unmodifiableList(this.objects);
    }
}
