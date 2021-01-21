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

    static ArchCondition<JavaClass> getOnlyCalledFromSameClass =
        new ArchCondition<JavaClass>("only be accessed by self") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                for (JavaMethodCall call : item.getMethodCallsToSelf()) {
                    // Check that is annotated with ClassOnly
                    if(call.getTarget().isAnnotatedWith(ClassOnly.class)) {
                        // If ClassOnly methods are called by sth other than self class throw error
                        boolean originIsTarget = call.getOriginOwner().equals(call.getTargetOwner());
                        if (!originIsTarget) {
                            String message = String.format(
                                "Method %s is not self called", call.getOrigin().getFullName());
                            events.add(SimpleConditionEvent.violated(call, message));
                        }
                    }
                }
            }
        };

    @ArchTest
    static final ArchRule methodsAndConstructorsWithClassOnlyMayNotCallFromOtherClasses = classes()
            .should(getOnlyCalledFromSameClass);
}
