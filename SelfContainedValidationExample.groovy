import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@interface Require {
  Class value()
}

@GrabResolver(
  name = 'm2repo.spockframework.org',
  root = 'http://m2repo.spockframework.org/snapshots')
@Grab('org.groovyext:groovyext:0.1-SNAPSHOT')
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

println "All validations passed!"