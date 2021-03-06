package io.hgc.jarspec;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Test runner for executing JarSpec specifications under JUnit
 *
 * @param <T> Specification class
 */
public class JarSpecJUnitRunner<T extends Specification> extends Runner {
    private Class<T> testClass;
    private T testInstance;

    public JarSpecJUnitRunner(Class<T> testClass) throws InstantiationException {
        this.testClass = testClass;
        try {
            testInstance = testClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Description getDescription() {
        return visitTree(testInstance.root(), Optional.empty());
    }

    @Override
    public void run(RunNotifier notifier) {
        visitTree(testInstance.root(), Optional.of(notifier));
    }

    private Description visitTree(SpecificationNode specificationNode, Optional<RunNotifier> notifier) {
        Description description = Description.createTestDescription(testClass, specificationNode.description());
        visitTree(testInstance.root(), notifier, "").forEach(description::addChild);
        return description;
    }

    private List<Description> visitTree(SpecificationNode specificationNode, Optional<RunNotifier> notifier, String prefix) {
        String text = prefix + specificationNode.description();

        List<Description> descriptions = new ArrayList<>();

        if (specificationNode.test().isPresent()) {
            Description description = Description.createTestDescription(testClass, text);
            descriptions.add(description);

            if (notifier.isPresent()) {
                runTest(specificationNode.test().get(), notifier.get(), description);
            }
        }

        if (specificationNode.children().size() > 0) {
            for (SpecificationNode child : specificationNode.children()) {
                descriptions.addAll(visitTree(child, notifier, text + " "));
            }
        }
        return descriptions;
    }

    private void runTest(Test test, RunNotifier notifier, Description description) {
        try {
            notifier.fireTestStarted(description);
            test.run();
        } catch (Throwable e) {
            notifier.fireTestFailure(new Failure(description, e));
        }
        notifier.fireTestFinished(description);
    }
}