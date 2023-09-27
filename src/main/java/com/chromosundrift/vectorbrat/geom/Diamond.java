package com.chromosundrift.vectorbrat.geom;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Immutable diamond shape (diamonds are forever).
 */
public final class Diamond extends Pointless implements Geom {

    private final Polyline lines;
    private final Box bounds;
    private Model model;

    /**
     * Constructs a diamond shape with the given width and height centred at 0,0
     *
     * @param width width in normalised units
     * @param height height in normalised units
     */
    public Diamond(float width, float height, Rgb colour) {
        this.bounds = new Box(width / 2, height / 2, width / -2, height / -2);

        // shape like this:
        //
        //   /\
        //   \/

        this.lines = Polyline.closed("diamond", colour,
                new Point(0, height/-2),
                new Point(width/2, 0),
                new Point(0, height / 2),
                new Point(width/-2, 0)
        );
        this.model = new Model("diamond", List.of(this.lines));
    }

    @Override
    public Optional<Point> closest(Point other) {
        // because all lines are doubly connected to the same points,
        // we only need to check the heads of each line
        return lines.lines().map(Line::from).min(other.dist2Point());
    }

    @Override
    public Stream<Line> lines() {
        return lines.lines();
    }

    @Override
    public Optional<Box> bounds() {
        return Optional.of(bounds);
    }

    public Model toModel() {
        return model;
    }

    @Override
    public Stream<Rgb> colours() {
        return lines.colours();
    }

    @Override
    public boolean inBounds() {
        return lines.inBounds();
    }

    @Override
    public boolean inBounds(float minX, float minY, float maxX, float maxY) {
        return lines.inBounds(minX, minY, maxX, maxY);
    }

    @Override
    public boolean inBounds(Box bounds) {
        return inBounds(bounds.minMin.x(), bounds.minMin.y(), bounds.maxMax.x(), bounds.maxMax.y());
    }

}
