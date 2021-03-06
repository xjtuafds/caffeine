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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.base.CaseFormat;

/**
 * The features that may be code generated.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
enum Feature {
  STRONG_KEYS,
  WEAK_KEYS,

  STRONG_VALUES,
  INFIRM_VALUES,
  WEAK_VALUES,
  SOFT_VALUES,

  EXPIRE_ACCESS,
  EXPIRE_WRITE,
  REFRESH_WRITE,

  MAXIMUM_SIZE,
  MAXIMUM_WEIGHT,

  LOADING,
  LISTENING,
  EXECUTOR,
  STATS;

  public static String makeEnumName(Iterable<Feature> features) {
    return StreamSupport.stream(features.spliterator(), false)
        .map(feature -> feature.name())
        .collect(Collectors.joining("_"));
  }

  public static String makeEnumName(String enumName) {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, enumName);
  }

  public static String makeClassName(Iterable<Feature> features) {
    String enumName = makeEnumName(features);
    return makeClassName(enumName);
  }

  public static String makeClassName(String enumName) {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, enumName);
  }

  public static boolean usesWriteOrderDeque(Set<Feature> features) {
    return features.contains(Feature.EXPIRE_WRITE);
  }

  public static boolean usesAccessOrderDeque(Set<Feature> features) {
    return features.contains(Feature.MAXIMUM_SIZE)
        || features.contains(Feature.MAXIMUM_WEIGHT)
        || features.contains(Feature.EXPIRE_ACCESS);
  }

  public static boolean usesWriteQueue(Set<Feature> features) {
    return features.contains(Feature.MAXIMUM_SIZE)
        || features.contains(Feature.MAXIMUM_WEIGHT)
        || features.contains(Feature.EXPIRE_ACCESS)
        || features.contains(Feature.EXPIRE_WRITE)
        || features.contains(Feature.REFRESH_WRITE);
  }

  public static boolean useWriteTime(Set<Feature> features) {
    return features.contains(Feature.EXPIRE_WRITE)
        || features.contains(Feature.REFRESH_WRITE);
  }

  public static boolean usesTicker(Set<Feature> features) {
    return features.contains(Feature.STATS)
        || features.contains(Feature.EXPIRE_ACCESS)
        || features.contains(Feature.EXPIRE_WRITE)
        || features.contains(Feature.REFRESH_WRITE);
  }

  public static boolean usesMaximum(Set<Feature> features) {
    return features.contains(Feature.MAXIMUM_SIZE)
        || features.contains(Feature.MAXIMUM_WEIGHT);
  }
}
