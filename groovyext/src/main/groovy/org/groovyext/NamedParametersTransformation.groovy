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

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.AbstractASTTransformation

@GroovyASTTransformation
class NamedParametersTransformation extends AbstractASTTransformation {
  @Override
  void visit(ASTNode[] nodes, SourceUnit source) {
    def node = nodes[1]

    if (node instanceof ClassNode) {
      addNamedParametersForMethodsInClass(node)
    } else if (node instanceof MethodNode) {
      addNamedParametersForMethod(node)
    } else {
      addError("@NamedParameters may only be applied to classes and methods", node)
    }
  }

  private addNamedParametersForMethodsInClass(ClassNode clazz) {
    for (method in clazz.methods.clone()) {
      if (method.synthetic) continue
      if (method.modifiers & ACC_SYNTHETIC) continue
      if (method.annotations.find { it.classNode.name == NamedParameters.name }) continue
      addNamedParametersForMethod(method)
    }
  }

  // TODO: handle overloaded methods
  // TODO: handle case where method with same signature already exists
  private addNamedParametersForMethod(MethodNode originalMethod) {
    def newMethodParts = new AstBuilder().buildFromSpec {
      parameters {
        owner.expression << new Parameter(nonGeneric(ClassHelper.MAP_TYPE), "map")
      }
      expression {
        methodCall {
          originalMethod.static ? owner.expression << new ClassExpression(nonGeneric(originalMethod.declaringClass)) : variable("this")
          constant originalMethod.name
          argumentList {
            for (param in originalMethod.parameters) {
              ternary {
                booleanExpression {
                  methodCall {
                    variable "map"
                    constant "containsKey"
                    argumentList {
                      constant param.name
                    }
                  }
                }
                methodCall {
                  variable "map"
                  constant "get"
                  argumentList {
                    constant param.name
                  }
                }
                if (param.initialExpression) {
                  owner.expression << param.initialExpression
                } else {
                  staticMethodCall(NamedParametersRuntime, "missingNamedParameter") {
                    argumentList {
                      constant originalMethod.name
                      constant param.name
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    def newMethod = new MethodNode(originalMethod.name, originalMethod.modifiers,
        nonGeneric(originalMethod.returnType), newMethodParts[0],
        nonGeneric(originalMethod.exceptions), newMethodParts[1])
    newMethod.addAnnotations(originalMethod.annotations)
    originalMethod.declaringClass.addMethod(newMethod)
  }

  protected ClassNode nonGeneric(ClassNode type) {
    if (type.usingGenerics) {
      def nonGeneric = ClassHelper.makeWithoutCaching(type.name)
      nonGeneric.redirect = type
      nonGeneric.genericsTypes = null
      nonGeneric.usingGenerics = false
      return nonGeneric
    } else if (type.array && type.componentType.usingGenerics) {
      return type.componentType.plainNodeReference.makeArray()
    } else {
      return type
    }
  }
  
  protected ClassNode[] nonGeneric(ClassNode[] types) {
    types.collect { nonGeneric(it) } as ClassNode[]
  }
}
