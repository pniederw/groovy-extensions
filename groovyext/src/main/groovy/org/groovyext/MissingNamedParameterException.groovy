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

class MissingNamedParameterException extends RuntimeException {
  private final String methodName
  private final String parameterName

  MissingNamedParameterException(String methodName, String parameterName) {
    this.methodName = methodName
    this.parameterName = parameterName
  }
  
  String getMessage() {
    "Invocation of method $methodName is missing value for named parameter $parameterName"
  }
}
