package com.chromosundrift.vectorbrat.processing;

import processing.core.PApplet;
import xyscope.XYscope;

public class PMain extends PApplet {
    public static void main(String[] args) {
        PApplet.main(PMain.class, args);
    }

    XYscope xy;         // create XYscope instance

    float t = 0;

    String txt = "Vitalise";

    public void settings() {
        size(512, 512);
    }

    public void setup() {
        frameRate(600);
        xy = new XYscope(this, "es-9_X_Y", 96000);
        xy.laser("es-9_R_Z", "es-9_G_B");

        // list all available fonts
        //println("Available Hershey Fonts:\n" + join(xy.fonts, ", ")); // list available fonts

        // set random font
        //xy.textFont(xy.fonts[floor(random(xy.fonts.length))]);
        //xy.textFont("astrology");
    }

    public void draw() {
        background(0);
        xy.clearWaves();
        xy.steps(50); // set segment multiplier

        // draw text
        pushMatrix();
        translate(width/2f, height/2f);
        rotate(t/120);
        xy.textSize(100);
        xy.textAlign(CENTER, CENTER);
        xy.text(txt, 0,0);

        popMatrix();
        t+= 0.1;
        xy.buildWaves();

        //xy.drawWaveform(); // wavetable
        xy.drawXY(); // scope viewer
    }
}
