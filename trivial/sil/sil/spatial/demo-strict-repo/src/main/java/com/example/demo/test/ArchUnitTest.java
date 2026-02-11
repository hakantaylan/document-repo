package com.example.demo.test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class ArchUnitTest {

    @Test
    void restrictedRepositoryNoDerivedQueries() {
        JavaClasses classes = new ClassFileImporter().importPackages("com.example.demo.repository");
        classes().that().areAssignableTo(com.example.demo.repository.RestrictedRepository.class)
                .should().haveOnlyMethodsThat(
                        m -> m.isDefault() || m.getDeclaringClass().getSimpleName().equals("RestrictedRepository")
                );
    }
}
