package com.chromosundrift.vectorbrat.anim;

import com.chromosundrift.vectorbrat.geom.Geom;
import com.chromosundrift.vectorbrat.geom.Vec2;

/**
 * Thing with velocity.
 *
 * @param object   thing moving.
 * @param velocity 2d velocity.
 * @param <T>      a {@link Geom}
 */
public record Mover<T extends Geom>(T object, Vec2 velocity) {
}
