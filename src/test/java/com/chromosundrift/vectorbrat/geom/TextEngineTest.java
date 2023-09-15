package com.chromosundrift.vectorbrat.geom;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class TextEngineTest {

    private static final Logger logger = LoggerFactory.getLogger(TextEngineTest.class);

    @Test
    public void color() {
        TextEngine te = new TextEngine(Rgb.GREEN, AsteroidsFont.INSTANCE);
        Model model = te.textLine("FOO");
        model.lines().forEach(line -> {
            TestUtils.assertPointColor("Wrong color for 'from' point", Rgb.GREEN, line.from());
            TestUtils.assertPointColor("Wrong color for 'to' point", Rgb.GREEN, line.to());
        });
    }

    @Test
    public void letterA() {
        TextEngine te = new TextEngine(Rgb.GREEN, AsteroidsFont.INSTANCE);
        Model model = te.textLine("0");
        // check all lines are in bounds for finer-grained failures
        model.lines().forEach(line -> {
            logger.info("checking line %s from".formatted(line));
            TestUtils.assertInRange("from", line.from());
            logger.info("checking line %s to".formatted(line));
            TestUtils.assertInRange("to", line.to());
        });
        // now check bounds are correct
        Box bounds = model.bounds().get();
        Box maxBox = new Box(-1f, -1f, 1f, 1f);
        assertEquals(maxBox, bounds);
    }

}
