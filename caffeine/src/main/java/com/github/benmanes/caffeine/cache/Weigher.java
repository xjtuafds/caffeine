/*
 * Copyright 2014 Ben Manes. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.benmanes.caffeine.cache;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Calculates the weights of cache entries. The total weight threshold is used to determine when an
 * eviction is required.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @author ben.manes@gmail.com (Ben Manes)
 */
@ThreadSafe
@FunctionalInterface
public interface Weigher<K, V> {

  /**
   * Returns the weight of a cache entry. There is no unit for entry weights; rather they are simply
   * relative to each other.
   *
   * @param key the key to weigh
   * @param value the value to weigh
   * @return the weight of the entry; must be non-negative
   */
  @Nonnegative
  int weigh(@Nonnull K key, @Nonnull V value);

  /**
   * Returns a weigher where an entry has a weight of {@code 1}.
   *
   * @param <K> the type of keys
   * @param <V> the type of values
   * @return a weigher where an entry has a weight of {@code 1}
   */
  @Nonnull
  static <K, V> Weigher<K, V> singleton() {
    @SuppressWarnings("unchecked")
    Weigher<K, V> self = (Weigher<K, V>) SingletonWeigher.INSTANCE;
    return self;
  }

  /**
   * Returns a weigher that enforces that the weight is non-negative.
   *
   * @param delegate the weigher to that weighs the entry
   * @param <K> the type of keys
   * @param <V> the type of values
   * @return a weigher that enforces that the weight is non-negative
   */
  @Nonnull
  static <K, V> Weigher<K, V> bounded(@Nonnull Weigher<K, V> delegate) {
    return new BoundedWeigher<>(delegate);
  }
}

enum SingletonWeigher implements Weigher<Object, Object> {
  INSTANCE;

  @Override public int weigh(Object key, Object value) {
    return 1;
  }
}

final class BoundedWeigher<K, V> implements Weigher<K, V>, Serializable {
  static final long serialVersionUID = 1;
  final Weigher<? super K, ? super V> delegate;

  BoundedWeigher(Weigher<? super K, ? super V> delegate) {
    requireNonNull(delegate);
    this.delegate = delegate;
  }

  @Override
  public int weigh(K key, V value) {
    int weight = delegate.weigh(key, value);
    Caffeine.requireArgument(weight >= 0);
    return weight;
  }

  Object writeReplace() {
    return delegate;
  }
}
