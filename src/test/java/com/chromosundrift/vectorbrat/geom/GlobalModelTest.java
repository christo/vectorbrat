package com.chromosundrift.vectorbrat.geom;

import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class GlobalModelTest {

    @Test
    public void lines() {
        Color c = Color.BLUE;
        int n = 10; // n polylines each with n points
        List<Polyline> polylines = Pattern.sineWaves(c, n);
        GlobalModel gm = new GlobalModel("foo", polylines);
        long nLines = gm.lines().count();
        Assert.assertEquals(n * (n - 1), nLines);
    }

}