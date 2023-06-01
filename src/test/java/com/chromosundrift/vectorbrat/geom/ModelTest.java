package com.chromosundrift.vectorbrat.geom;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModelTest {

    private static final Function<Line, Stream<Point>> ENDS = l -> Set.of(l.from(), l.to()).stream();
    private static final Logger logger = LoggerFactory.getLogger(ModelTest.class);

    @Test
    public void lines() {
        Color c = Color.BLUE;
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
        Model model = Pattern.boxGrid(cols, rows, Color.CYAN);
        LinkedList<Line> lines = new LinkedList<>();
        for (Polyline pl : model._polylines()) {
            lines.addAll(pl.lineList());
        }
        int boxSides = 4;
        lines.forEach(line -> System.out.println("line = " + line));
        assertEquals(cols * rows * boxSides, lines.size());
    }

    @Test
    public void colored() {
        Point greenOrigin = new Point(0f, 0f, Color.GREEN);
        Point blueMax = new Point(1f, 1f, Color.BLUE);
        Polyline foo1 = Polyline.open("foo", Color.RED, greenOrigin, blueMax);
        Point orangeMin = new Point(-1f, -1f, Color.ORANGE);
        Model foo = new Model("foo", List.of(foo1), List.of(orangeMin));
        Model colored = foo.colored(Color.MAGENTA);
        List<Polyline> offColorPolylines = colored.polylines()
                .filter(polyline -> !polyline.getColor().equals(Color.MAGENTA))
                .toList();
        assertEquals("polylines should have all been green", emptyList(), offColorPolylines);
        List<Point> offColorPoints = colored.points()
                .filter(polyline -> !polyline.getColor().equals(Color.MAGENTA))
                .toList();
        assertEquals("points should have all been green", emptyList(), offColorPoints);
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

    private static Set<Point> getLinePoints(Geom g) {
        return g.lines().flatMap(ENDS).collect(Collectors.toSet());
    }

    @Test
    public void denormalise() {
        Box box = new Box(0f, 0f, 1f, 1f);
        Polyline normalPolyBox = box.toPolyline("normal box", Color.WHITE);
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
}