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

import spock.lang.Specification

@NamedParameters
class IWannaBeCalledByName {
  Map buy(String item, String vendor = "myself", BigDecimal price) {
    println "buying $item from $vendor for $price"
    [item: item, vendor: vendor, price: price]
  }

  static Map buyStatic(String item, String vendor = "myself", BigDecimal price) {
    println "buying static $item from $vendor for $price"
    [item: item, vendor: vendor, price: price]
  }
}

class NamedParametersSpec extends Specification {
  def wannabe = new IWannaBeCalledByName()
  
  def "basic usage"() {
    expect:
    wannabe.buy(item: "bread", vendor: "bakery", price: 0.99) == [item: "bread", vendor: "bakery", price: 0.99]
  }
  
  def "can omit parameters with default value"() {
    expect:
    wannabe.buy(item: "bread", price: 0.99) == [item: "bread", vendor: "myself", price: 0.99]
  }
  
  def "cannot omit parameters without default value"() {
    when:
    wannabe.buy(item: "bread", vendor: "bakery")
    
    then:
    MissingNamedParameterException e = thrown()
    e.methodName == "buy"
    e.parameterName == "price"
  }
  
  def "can call original method"() {
    expect:
    wannabe.buy("bread", "bakery", 0.99) == [item: "bread", vendor: "bakery", price: 0.99]
  }

  def "can call original method and omit parameters with default value"() {
    expect:
    wannabe.buy("bread", 0.99) == [item: "bread", vendor: "myself", price: 0.99]
  }
  
  def "also works for static methods"() {
    expect:
    IWannaBeCalledByName.buyStatic(item: "bread", vendor: "bakery", price: 0.99) == [item: "bread", vendor: "bakery", price: 0.99]
  }
}
