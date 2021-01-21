package controllertest;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import covidtracer.stereotypes.ClassOnly;
import covidtracer.stereotypes.Mutable;

import java.lang.reflect.Constructor;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "covidtracer")
public class Testing {

    DescribedPredicate<JavaClass> haveAFieldAnnotatedWithPayload =
            new DescribedPredicate<JavaClass>("have a field annotated with @Payload"){
                @Override
                public boolean apply(JavaClass input) {
                    boolean someFieldAnnotatedWithPayload = // iterate fields and check for @Payload
                    return someFieldAnnotatedWithPayload;
                }
            };

    ArchCondition<JavaClass> onlyBeAccessedBySecuredMethods =
            new ArchCondition<JavaClass>("only be accessed by @Secured methods") {
                @Override
                public void check(JavaClass item, ConditionEvents events) {
                    for (JavaMethodCall call : item.getMethodCallsToSelf()) {
                        if (!call.getOrigin().isAnnotatedWith(Secured.class)) {
                            String message = String.format(
                                    "Method %s is not @Secured", call.getOrigin().getFullName());
                            events.add(SimpleConditionEvent.violated(call, message));
                        }
                    }
                }
            };

    @ArchTest
    static final ArchRule noDeprecatedClasses = noClasses()
            .should()
            .beAnnotatedWith(Deprecated.class);

    @ArchTest
    static final ArchRule finalFieldMustBeMutable = fields()
            .that()
            .areNotFinal()
            .should()
            .beAnnotatedWith(Mutable.class);


    @ArchTest
    static final ArchRule allMethodsAndConstructorsMustBePublic1 = constructors()
            .should()
            .bePublic();

    @ArchTest
    static final ArchRule allMethodsAndConstructorsMustBePublic2 = methods()
            .that()
            .areDeclaredInClassesThat()
            .resideInAPackage("covidtracer..")
            .should()
            .bePublic();

    @ArchTest
    static final ArchRule noMethodsMustNotPublic = noMethods()
            .should()
            .notBePublic();

    @ArchTest
    static final ArchRule methodsAndConstructorsWithClassOnlyMayNotCallFromOtherClasses = classes()
            .that()
            .areNotAnnotatedWith(ClassOnly.class)
            .should(callMethodsFrom)
            .callMethod()
            .onlyCallMethodsThat();


    private static <T> ArchCondition<JavaClass> callAsyncMethodsFromTheSameClass(Class<? extends Annotation> clazz) {
        return new ArchCondition<JavaClass>(String.format("call @%s Methods from the same class", clazz.getName())) {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                for (JavaMethodCall call : javaClass.getMethodCallsFromSelf()) {
                    boolean originIsTarget = call.getOriginOwner().equals(call.getTargetOwner());
                    if (call.getTarget().isAnnotatedWith(clazz)) {
                        events.add(new SimpleConditionEvent(call, originIsTarget, call.getDescription()));
                    }
                }
            }
        };
}
