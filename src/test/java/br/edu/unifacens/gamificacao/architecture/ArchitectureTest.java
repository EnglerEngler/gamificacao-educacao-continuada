package br.edu.unifacens.gamificacao.architecture;
import com.tngtech.archunit.junit.*;
import com.tngtech.archunit.lang.*;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import java.util.Set;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages="br.edu.unifacens.gamificacao",importOptions=ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
    @ArchTest static final ArchRule dominioPuro=noClasses().that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().resideInAnyPackage("org.springframework..","jakarta..","..internal..","..api..","..application..");
    @ArchTest static final ArchRule modulos=classes().should(new ArchCondition<>("usar apenas APIs públicas de outros módulos") {
        public void check(JavaClass source, ConditionEvents events) {
            String prefix="br.edu.unifacens.gamificacao.";
            Set<String> modules=Set.of("aluno","ranking","ia","eventos","iot");
            String own=source.getName().substring(prefix.length()).split("\\.")[0];
            if (!modules.contains(own)) return;
            for (var dependency: source.getDirectDependenciesFromSelf()) {
                String target=dependency.getTargetClass().getName();
                if (!target.startsWith(prefix)) continue;
                String other=target.substring(prefix.length()).split("\\.")[0];
                if (modules.contains(other) && !own.equals(other) && !target.startsWith(prefix+other+".api."))
                    events.add(SimpleConditionEvent.violated(source, dependency.getDescription()));
            }
        }
    });
}

