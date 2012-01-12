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

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import java.lang.annotation.ElementType

import org.codehaus.groovy.transform.GroovyASTTransformationClass

@GroovyASTTransformationClass("org.groovyext.NamedParametersTransformation")
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD, ElementType.TYPE])
public @interface NamedParameters {
  NamedParametersTarget value() default NamedParametersTarget.NON_PRIVATE_METHODS
}