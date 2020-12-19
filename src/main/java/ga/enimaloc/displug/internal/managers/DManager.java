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

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.map.UnmodifiableMap;

public class DManager<K, V> {

    private final Map<K, V> objects;

    public DManager() {
        this.objects = new HashMap<>();
    }

    public void add(K key, V object) {
        this.objects.put(key, object);
    }

    public void remove(K key) {
        this.objects.remove(key);
    }

    public V get(K key) {
        return this.objects.get(key);
    }

    public Map<K, V> all() {
        return UnmodifiableMap.unmodifiableMap(this.objects);
    }
}
