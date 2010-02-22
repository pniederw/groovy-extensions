/*
 * Copyright 2009 the original author or authors.
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

package org.groovyext.annclosure

class ParametersAndContextTest extends AbstractTest {
  Class getTargetClass() { ParametersAndContext }

  def getOwner() {
    [x: "x", y: "y", z: "z"]
  }

  void testClassAnnotation() {
    assertEquals "argx", classClosure.call("arg")
  }

  void testFieldAnnotation() {
    assertEquals "x", fieldClosure.call()
  }

  void testPropertyAnnotation() {
    assertEquals "arg1arg2z", propertyClosure.call("arg1", "arg2")
  }

  void testMethodAnnotation() {
    assertEquals "arg1xy", methodClosure.call("arg1")
  }

  void testParameterAnnotation() {
    assertEquals "argx", parameterClosure.call("arg") 
  }
}