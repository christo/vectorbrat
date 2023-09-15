package com.chromosundrift.vectorbrat.geom;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class ModelTest {

    private static final Function<Line, Stream<Point>> ENDS = l -> Set.of(l.from(), l.to()).stream();
    private static final Logger logger = LoggerFactory.getLogger(ModelTest.class);

    private static Set<Point> getLinePoints(Geom g) {
        return g.lines().flatMap(ENDS).collect(Collectors.toSet());
    }

    @Test
    public void lines() {
        Rgb c = Rgb.BLUE;
        int n = 10; // n polylines each with n points
        List<Polyline> polylines = Pattern.sineWaves(c, n);
        Model gm = new Model("foo", polylines);
        long nLines = gm.lines().count();
        assertEquals(n * (n - 1), nLines);
    }

    @Test
    public void testGridLines() {
        int cols = 4;
        int rows = 4;
        Model model = Pattern.boxGrid(cols, rows, Rgb.CYAN);
        List<Line> lines = model.lines().toList();
        int boxSides = 4;
        lines.forEach(line -> System.out.println("line = " + line));
        assertEquals(cols * rows * boxSides, lines.size());
    }

    @Test
    public void colored() {
        Point greenOrigin = new Point(0f, 0f, Rgb.GREEN);
        Point blueMax = new Point(1f, 1f, Rgb.BLUE);
        Polyline foo1 = Polyline.open("foo", Rgb.RED, greenOrigin, blueMax);
        Point orangeMin = new Point(-1f, -1f, Rgb.ORANGE);
        Model foo = new Model("foo", List.of(foo1), List.of(orangeMin));
        Model colored = foo.coloured(Rgb.MAGENTA);
        List<Line> offColorLines = colored.lines()
                .filter(line -> !line.from().getColor().equals(Rgb.MAGENTA) || !line.to().getColor().equals(Rgb.MAGENTA))
                .toList();
        assertEquals("lines should have all been magenta", emptyList(), offColorLines);
        List<Point> offColorPoints = colored.isoPoints()
                .filter(point -> !point.getColor().equals(Rgb.MAGENTA))
                .toList();
        assertEquals("points should have all been magenta", emptyList(), offColorPoints);
    }

    @Test
    public void normalise() {
        Model model = Pattern.boundingBox().normalise();
        logger.info("model: " + model);
        model.lines().forEach(line -> {
            logger.info("checking line %s from".formatted(line));
            TestUtils.assertInNormalRange(line.from());
            logger.info("checking line %s to".formatted(line));
            TestUtils.assertInNormalRange(line.to());
        });
        // check specific point values are at corners
        Set<Point> linePoints = getLinePoints(model);
        Set<Point> corners = Set.of(
                new Point(0f, 0f),
                new Point(0f, 1f),
                new Point(1f, 0f),
                new Point(1f, 1f));
        assertEquals("line end points should be four corners", corners, linePoints);
    }

    @Test
    public void denormalise() {
        Box box = new Box(0f, 0f, 1f, 1f);
        Polyline normalPolyBox = box.toPolyline("normal box", Rgb.WHITE);
        Model m = new Model("just normal box", List.of(normalPolyBox)).denormalise();
        m.lines().forEach(line -> {
            logger.info("checking line %s from".formatted(line));
            TestUtils.assertInRange(line.from().toString(), line.from());
            logger.info("checking line %s to".formatted(line));
            TestUtils.assertInRange(line.to().toString(), line.to());
        });
        // check specific point values
        Set<Point> expectedPoints = getLinePoints(new Box(-1f, -1f, 1f, 1f));
        assertEquals(expectedPoints, getLinePoints(m));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void bounds() {
        Box actual = Pattern.boundingBox(Rgb.RED).bounds().get();
        assertEquals(new Box(-1f, -1f, 1f, 1f), actual);
    }
}