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

import org.junit.Test

class NamedParametersTest {
  def shop = new Shop()
  
  @Test
  void basicUsage() {
    assert shop.buy(item: "bread", vendor: "bakery", price: 0.99) == [item: "bread", vendor: "bakery", price: 0.99]
  }
  
  @Test
  void canOmitParameterWithDefaultValue() {
    assert shop.buy(item: "bread", price: 0.99) == [item: "bread", vendor: "myself", price: 0.99]
  }
  
  @Test(expected = MissingNamedParameterException)
  void cannotOmitParameterWithoutDefaultValue() {
    shop.buy(item: "bread", vendor: "bakery")
  }
  
  @Test
  void canCallOriginalMethod() {
    assert shop.buy("bread", "bakery", 0.99) == [item: "bread", vendor: "bakery", price: 0.99]
  }

  @Test
  void canCallOriginalMethodAndOmitParameterWithDefaultValue() {
    assert shop.buy("bread", 0.99) == [item: "bread", vendor: "myself", price: 0.99]
  }
  
  @Test
  void basicUsageWithStaticMethod() {
    assert Shop.buyStatic(item: "bread", vendor: "bakery", price: 0.99) == [item: "bread", vendor: "bakery", price: 0.99]
  }
}
