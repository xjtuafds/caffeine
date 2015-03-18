/*
 * Copyright 2015 Ben Manes. All Rights Reserved.
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

import com.github.benmanes.caffeine.base.UnsafeAccess;

/**
 * Static classes that provide AtomicXXX variants that support relaxed reads.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
final class Relaxed {

  private Relaxed() {}

  /** A variant of AtomicLong supporting only relaxed read and write operations. */
  static final class RelaxedLong {
    static final long VALUE_OFFSET = UnsafeAccess.objectFieldOffset(RelaxedLong.class, "value");

    @SuppressWarnings("unused")
    private volatile long value;

    /** Returns a relaxed read of the last known value. */
    public long lazyGet() {
      return UnsafeAccess.UNSAFE.getLong(this, VALUE_OFFSET);
    }

    /** Eventually sets to the given value. */
    public final void lazySet(long newValue) {
      UnsafeAccess.UNSAFE.putOrderedLong(this, VALUE_OFFSET, newValue);
    }
  }

  /** A variant of AtomicReference supporting only relaxed read and write operations. */
  static final class RelaxedReference<V> {
    static final long VALUE_OFFSET =
        UnsafeAccess.objectFieldOffset(RelaxedReference.class, "value");

    @SuppressWarnings("unused")
    private volatile V value;

    /** Returns a relaxed read of the last known value. */
    @SuppressWarnings("unchecked")
    public V lazyGet() {
      return (V) UnsafeAccess.UNSAFE.getObject(this, VALUE_OFFSET);
    }

    /** Eventually sets to the given value. */
    public final void lazySet(V newValue) {
      UnsafeAccess.UNSAFE.putOrderedObject(this, VALUE_OFFSET, newValue);
    }

    /** Returns {@code true} if the value was swapped from the expected to the updated. */
    public boolean compareAndSet(V expect, V update) {
      return UnsafeAccess.UNSAFE.compareAndSwapObject(this, VALUE_OFFSET, expect, update);
    }
  }
}
