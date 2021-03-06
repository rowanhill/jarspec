package io.hgc.jarspec;

import java.util.*;

/**
 * Internal implementation of {@link Specification} as a tree structure.
 */
public abstract class SpecificationNode {
    private SpecificationNode() {}

    abstract Optional<Test> test();

    abstract String description();

    abstract List<SpecificationNode> children();

    static class Aggregate extends SpecificationNode {
        private String unit;
        private List<SpecificationNode> children;

        public Aggregate(String unit, List<SpecificationNode> children) {
            this.unit = unit;
            this.children = children;
        }

        @Override
        Optional<Test> test() {
            return Optional.empty();
        }

        @Override
        String description() {
            return unit;
        }

        @Override
        List<SpecificationNode> children() {
            return children;
        }
    }

    static class Statement extends SpecificationNode {
        private String behaviour;
        private Test test;

        public Statement(String behaviour, Test test) {
            this.behaviour = behaviour;
            this.test = test;
        }

        @Override
        Optional<Test> test() {
            return Optional.of(test);
        }

        @Override
        String description() {
            return behaviour;
        }

        @Override
        List<SpecificationNode> children() {
            return Collections.emptyList();
        }
    }

    static class SpecError extends SpecificationNode {
        private final String unit;
        private final Exception exception;

        public SpecError(String unit, Exception exception) {
            this.unit = unit;
            this.exception = exception;
        }

        @Override
        Optional<Test> test() {
            return Optional.of(() -> { throw exception; });
        }

        @Override
        String description() {
            return unit;
        }

        @Override
        List<SpecificationNode> children() {
            return Collections.emptyList();
        }
    }
}