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

package org.groovyext.annclosure;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class AnnotationClosureTransformation implements ASTTransformation {
  public void visit(ASTNode[] nodes, SourceUnit unit) {
    ModuleNode moduleNode = (ModuleNode)nodes[0];
    for (ClassNode classNode : moduleNode.getClasses())
      new ClassRewriter(classNode).rewrite();
  }
}

class ClassRewriter {
  private final ClassNode classNode;
  private int movedClosures = 0;

  public ClassRewriter(ClassNode classNode) {
    this.classNode = classNode;
  }

  public void rewrite() {
    new ClassCodeVisitorSupport() {
      public void visitAnnotations(AnnotatedNode annotatedNode) {
        super.visitAnnotations(annotatedNode);
        for (AnnotationNode annotation : annotatedNode.getAnnotations())
          processAnnotation(annotation, classNode);
      }

      protected SourceUnit getSourceUnit() {
        return null;
      }
    }.visitClass(classNode);
  }

  private void processAnnotation(AnnotationNode annotation, ClassNode annotatedClass) {
    for (Map.Entry<String, Expression> member : annotation.getMembers().entrySet()) {
      if (!(member.getValue() instanceof ClosureExpression))
        continue;

      ClosureExpression closureExpr = (ClosureExpression) member.getValue();
      int closureNumber = countClosuresInStaticFieldInitializers(annotatedClass) + movedClosures + 1;
      ClassExpression classExpr = new ClassExpression(
          ClassHelper.makeWithoutCaching(annotatedClass.getName() + "$__clinit__closure" + closureNumber));
      member.setValue(classExpr);
      getStaticIntializerStatements(annotatedClass).add(movedClosures, new ExpressionStatement(closureExpr));

      movedClosures++;
    }
  }

  private List<Statement> getStaticIntializerStatements(ClassNode classNode) {
    MethodNode staticInitializer = getOrAddStaticConstructorNode(classNode);
    return ((BlockStatement) staticInitializer.getCode()).getStatements();
  }

  private MethodNode getOrAddStaticConstructorNode(ClassNode classNode) {
    try {
      Method method = ClassNode.class.getDeclaredMethod("getOrAddStaticConstructorNode");
      method.setAccessible(true);
      return (MethodNode) method.invoke(classNode);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private int countClosuresInStaticFieldInitializers(ClassNode classNode) {
    final AtomicInteger count = new AtomicInteger();

    for (FieldNode field : classNode.getFields()) {
      if (!field.isStatic() || field.getInitialValueExpression() == null) continue;

      field.getInitialValueExpression().visit(new CodeVisitorSupport() {
        public void visitClosureExpression(ClosureExpression expression) {
          // must not count nested closures, so we don't call super.visitClosureExpression() here
          count.incrementAndGet();
        }
      });
    }

    return count.get();
  }
}
