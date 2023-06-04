package com.chromosundrift.vectorbrat.laser;

import nl.sijpesteijn.ilda.ColorData;
import nl.sijpesteijn.ilda.CoordinateData;
import nl.sijpesteijn.ilda.CoordinateHeader;
import nl.sijpesteijn.ilda.IldaFormat;
import nl.sijpesteijn.ilda.IldaReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.geom.Line;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Pather;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.Polyline;
import com.chromosundrift.vectorbrat.geom.SimplePather;

public class Ilda {
    
    private static final Logger logger = LoggerFactory.getLogger(Ilda.class);

    /**
     * Constant defined by ILDA spec.
     */
    private static final String ILDA_PROTOCOL = "ILDA";

    /**
     * Add to ILDA values to get zero based range.
     */
    public static final int ILDA_OFFSET = 32768;

    /**
     * Range of ILDA coordinate values.
     */
    public static final float ILDA_SCALE = 65536f;

    public static void main(String[] args) throws IOException, URISyntaxException {

        Ilda ilda = new Ilda();
        IldaFormat ildaFormat = ilda.parse(new File("media/ilda/ILDA 30k.ild"));
        dump(ildaFormat);
    }

    IldaFormat parse(File f) throws IOException, URISyntaxException {
        IldaReader ir = new IldaReader();
        return ir.read(f);
    }

    public Pather loadModel(File ildaFile) throws IOException, URISyntaxException, VectorBratException {
        IldaFormat content = parse(ildaFile);
        return ildaToModel(content);
    }

    Pather ildaToModel(IldaFormat content) throws VectorBratException {
        List<CoordinateHeader> coordinateHeaders = content.getCoordinateHeaders();

        // reject ILDA files we don't know how to turn into a Model.
        // future: multi-frame ilda-files can be turned into a ModelAnimator (parametised by frame rate?)

        // one header for data frame, one empty termination header
        if (coordinateHeaders.size() != 2) {
            throw new VectorBratException("don't know how to deal with more than 2 headers");
        }
        if (coordinateHeaders.stream().filter(ch -> !ch.getProtocol().equals(ILDA_PROTOCOL)).findAny().isEmpty()) {
            throw new VectorBratException("non ILDA protocol present");
        }
        if (coordinateHeaders.get(1).getTotalPoints() != 0) {
            throw new VectorBratException("expecting last header to have no points");
        }

        CoordinateHeader ch = coordinateHeaders.get(0);
        List<Point> points = ch.getCoordinateData().stream().map(Ilda::toPoint).toList();
        return new SimplePather(points);
    }

    static Point toPoint(CoordinateData cd) {
        float x = (cd.getX() + ILDA_OFFSET) / ILDA_SCALE;
        float y = (cd.getY() + ILDA_OFFSET) / ILDA_SCALE;
        ColorData colorData = cd.getColorData();
        // turn colours into normalised floats
        float red = colorData.getRed1() / 255f;
        float green = colorData.getGreen1() / 255f;
        float blue = colorData.getBlue1() / 255f;
        return new Point(x, y, red, green, blue);
    }

    private static void dump(IldaFormat content) {
        List<CoordinateHeader> coordinateHeaders = content.getCoordinateHeaders();
        for (CoordinateHeader coordinateHeader : coordinateHeaders) {
            logger.info("coordinateHeader = " + coordinateHeader);
            int index = 0;
            for (CoordinateData dat : coordinateHeader.getCoordinateData()) {
                logger.info(" Frame " + coordinateHeader.getFrameNumber() + ": point " + index++
                        + ": " + dat.getX() + ", " + dat.getY() + ", " + dat.getZ() + " " + dat.getColorData().toString().trim());
            }
        }
        if (content.getColorHeader() != null) {
            logger.info(content.getColorHeader().toString());
        } else {
            logger.info("no colour header");
        }
        assert (content.getCoordinateHeaders() != null);
        assert (content.getColorHeader().getTotalColors() == content.getColorData().size());
    }
}
