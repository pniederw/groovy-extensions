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

package org.groovyext.annclosure.validate

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.reflect.*

@Retention(RetentionPolicy.RUNTIME)
@interface Require {
  Class value()
}

class Validator {
  boolean validate(Object obj) {
    obj.getClass().declaredFields.every {
      validateField(obj, it)
    }
  }

  boolean validateField(Object obj, Field field) {
    def ann = field.getAnnotation(Require)
    if (ann == null) return true

    def constraint = ann.value().newInstance(null, null)
    field.setAccessible(true)
    constraint.call(field.get(obj))
  }
}

class Person {
  @Require({ it ==~ /[a-z A-Z]+/ })
  String name
  @Require({ it in (0..130) })
  int age
}

def validator = new Validator()

def peter = new Person(name: "Peter", age: 33)
assert validator.validate(peter)

def oldie = new Person(name: "Oldie", age: 140)
assert !validator.validate(oldie)
