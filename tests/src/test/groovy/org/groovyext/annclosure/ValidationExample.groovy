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

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@interface Require {
  Class value()
}

class Validator {
  def isValid(pogo) {
    pogo.getClass().declaredFields.every {
      isValidField(pogo, it)
    }
  }

  def isValidField(pogo, field) {
    def annotation = field.getAnnotation(Require)
    !annotation || meetsConstraint(pogo, field, annotation.value())
  }

  def meetsConstraint(pogo, field, constraint) {
    def closure = constraint.newInstance(null, null)
    field.setAccessible(true)
    closure.call(field.get(pogo))
  }
}

class Person {
  @Require({ it ==~ /[a-z A-Z]+/ })
  String name
  @Require({ it in (0..130) })
  int age
}

def validator = new Validator()

def fred = new Person(name: "Fred Flintstone", age: 43)
assert validator.isValid(fred)

def barney = new Person(name: "!!!Barney Rubble!!!", age: 37)
assert !validator.isValid(barney)

def dino = new Person(name: "Dino", age: 176)
assert !validator.isValid(dino)
