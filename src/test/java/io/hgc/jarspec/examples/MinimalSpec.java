package io.hgc.jarspec.examples;

import io.hgc.jarspec.JarSpecJUnitRunner;
import io.hgc.jarspec.Specification;
import io.hgc.jarspec.UnitSpec;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(JarSpecJUnitRunner.class)
public class MinimalSpec implements UnitSpec {
    @Override
    public Specification specification() {
        return describe("A specification", () ->
            it("contains a statement with a test", () -> assertTrue(true))
        );
    }
}