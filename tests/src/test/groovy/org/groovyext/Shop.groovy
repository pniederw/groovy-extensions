/*
 * Copyright 2011 the original author or authors.
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
package org.groovyext

@NamedParameters
class Shop {
  Map buy(String item, String vendor = "myself", BigDecimal price) {
    println "buying $item from $vendor for $price"
    [item: item, vendor: vendor, price: price]
  }

  static Map buyStatic(String item, String vendor = "myself", BigDecimal price) {
    println "buying static $item from $vendor for $price"
    [item: item, vendor: vendor, price: price]
  }
}
