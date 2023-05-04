package com.chromosundrift.vectorbrat.javafx;

import com.chromosundrift.vectorbrat.VectorBrat;

public class MainFx {
    public static void main(String[] args) {
        // work around the javafx module loading mechanism, per:
        // https://github.com/javafxports/openjdk-jfx/issues/236#issuecomment-426583174
        VectorBrat.main(args);
    }
}
