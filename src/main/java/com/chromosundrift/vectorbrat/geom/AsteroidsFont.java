package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chromosundrift.vectorbrat.Config.SAMPLE_MAX;
import static com.chromosundrift.vectorbrat.Config.SAMPLE_MIN;

import com.chromosundrift.vectorbrat.Config;

public class AsteroidsFont implements Typeface {

    private static final Model MISSING_CHAR = createMissingChar();
    Map<Character, Model> chars;

    public AsteroidsFont() {
        chars = buildCharMap();
    }

    private static Model createMissingChar() {
        List<Polyline> polylines = new ArrayList<>();
        polylines.add(Polyline.closed("[]", Color.WHITE,
                new Point(SAMPLE_MIN, SAMPLE_MIN),
                new Point(SAMPLE_MIN, SAMPLE_MAX),
                new Point(SAMPLE_MAX, SAMPLE_MAX),
                new Point(SAMPLE_MAX, SAMPLE_MIN)
        ));
        polylines.add(Polyline.open("\\", Color.WHITE,
                new Point(SAMPLE_MIN, SAMPLE_MIN),
                new Point(SAMPLE_MAX, SAMPLE_MAX))
        );
        polylines.add(Polyline.open("/", Color.WHITE,
                new Point(SAMPLE_MAX, SAMPLE_MIN),
                new Point(SAMPLE_MIN, SAMPLE_MAX))
        );
        return new GlobalModel("[X]", polylines);
    }

    static Map<Character, Model> buildCharMap() {

        Map<Character, Model> charMap = new HashMap<>();

        Letter A = new Letter();
        A.addLine(0, 6, 0, 1);
        A.addLine(0, 1, 2, 0);
        A.addLine(2, 0, 4, 1);
        A.addLine(4, 1, 4, 6);
        A.addLine(4, 3, 0, 3);


        charMap.put('A', A.toModel('A'));

        Letter B = new Letter();
        B.addLine(0, 0, 0, 6);
        B.addLine(0, 6, 3, 6);
        B.addLine(3, 6, 4, 5);
        B.addLine(4, 5, 4, 4);
        B.addLine(4, 4, 3, 3);
        B.addLine(3, 3, 0, 3);
        B.addLine(0, 3, 3, 3);
        B.addLine(3, 3, 4, 2);
        B.addLine(4, 2, 4, 1);
        B.addLine(4, 1, 3, 0);
        B.addLine(3, 0, 0, 0);
        charMap.put('B', B.toModel('B'));

        Letter C = new Letter();
        C.addLine(4, 0, 0, 0);
        C.addLine(0, 0, 0, 6);
        C.addLine(0, 6, 4, 6);

        charMap.put('C', C.toModel('C'));

        Letter D = new Letter();
        D.addLine(0, 0, 3, 0);
        D.addLine(3, 0, 4, 2);
        D.addLine(4, 2, 4, 4);
        D.addLine(4, 4, 3, 6);
        D.addLine(3, 6, 0, 6);
        D.addLine(0, 6, 0, 0);

        charMap.put('D', D.toModel('D'));

        Letter E = new Letter();
        E.addLine(4, 0, 0, 0);
        E.addLine(0, 0, 0, 6);
        E.addLine(0, 6, 4, 6);
        E.addLine(4, 3, 0, 3);

        charMap.put('E', E.toModel('E'));

        Letter F = new Letter();
        F.addLine(4, 0, 0, 0);
        F.addLine(0, 0, 0, 6);
        F.addLine(0, 3, 4, 3);


        charMap.put('F', F.toModel('F'));

        Letter G = new Letter();
        G.addLine(4, 1, 4, 0);
        G.addLine(4, 0, 0, 0);
        G.addLine(0, 0, 0, 6);
        G.addLine(0, 6, 4, 6);
        G.addLine(4, 6, 4, 3);
        G.addLine(4, 3, 2, 3);

        charMap.put('G', G.toModel('G'));

        Letter H = new Letter();
        H.addLine(0, 0, 0, 6);
        H.addLine(0, 3, 4, 3);
        H.addLine(4, 0, 4, 6);

        charMap.put('H', H.toModel('H'));

        Letter I = new Letter();
        I.addLine(0, 0, 4, 0);
        I.addLine(2, 0, 2, 6);
        I.addLine(0, 6, 4, 6);

        charMap.put('I', I.toModel('I'));

        Letter J = new Letter();
        J.addLine(0, 4, 1, 6);
        J.addLine(1, 6, 4, 6);
        J.addLine(4, 6, 4, 0);
        J.addLine(4, 0, 2, 0);

        charMap.put('J', J.toModel('J'));

        Letter K = new Letter();
        K.addLine(0, 0, 0, 6);
        K.addLine(0, 3, 4, 0);
        K.addLine(0, 3, 4, 6);

        charMap.put('K', K.toModel('K'));

        Letter L = new Letter();
        L.addLine(0, 0, 0, 6);
        L.addLine(0, 6, 4, 6);

        charMap.put('L', L.toModel('L'));

        Letter M = new Letter();
        M.addLine(0, 6, 0, 0);
        M.addLine(0, 0, 2, 2);
        M.addLine(2, 2, 4, 0);
        M.addLine(4, 0, 4, 6);

        charMap.put('M', M.toModel('M'));

        Letter N = new Letter();
        N.addLine(0, 6, 0, 0);
        N.addLine(0, 0, 0, 1);
        N.addLine(0, 1, 4, 5);
        N.addLine(4, 5, 4, 6);
        N.addLine(4, 6, 4, 0);

        charMap.put('N', N.toModel('N'));

        Letter O = new Letter();
        O.addLine(0, 0, 4, 0);
        O.addLine(4, 0, 4, 6);
        O.addLine(4, 6, 0, 6);
        O.addLine(0, 6, 0, 0);

        charMap.put('O', O.toModel('O'));

        Letter P = new Letter();
        P.addLine(0, 6, 0, 0);
        P.addLine(0, 0, 4, 0);
        P.addLine(4, 0, 4, 3);
        P.addLine(4, 3, 0, 3);

        charMap.put('P', P.toModel('P'));

        Letter Q = new Letter();
        Q.addLine(0, 0, 4, 0);
        Q.addLine(4, 0, 4, 4);
        Q.addLine(4, 4, 2, 6);
        Q.addLine(2, 6, 0, 6);
        Q.addLine(0, 6, 0, 0);
        Q.addLine(2, 4, 4, 6);

        charMap.put('Q', Q.toModel('Q'));

        Letter R = new Letter();
        R.addLine(0, 6, 0, 0);
        R.addLine(0, 0, 4, 0);
        R.addLine(4, 0, 4, 3);
        R.addLine(4, 3, 0, 3);
        R.addLine(0, 3, 4, 6);

        charMap.put('R', R.toModel('R'));

        Letter S = new Letter();
        S.addLine(4, 0, 0, 0);
        S.addLine(0, 0, 0, 3);
        S.addLine(0, 3, 4, 3);
        S.addLine(4, 3, 4, 6);
        S.addLine(4, 6, 0, 6);

        charMap.put('S', S.toModel('S'));

        Letter T = new Letter();
        T.addLine(0, 0, 4, 0);
        T.addLine(2, 0, 2, 6);

        charMap.put('T', T.toModel('T'));

        Letter U = new Letter();
        U.addLine(0, 0, 0, 5);
        U.addLine(0, 5, 1, 6);
        U.addLine(1, 6, 3, 6);
        U.addLine(3, 6, 4, 5);
        U.addLine(4, 5, 4, 0);

        charMap.put('U', U.toModel('U'));

        Letter V = new Letter();
        V.addLine(0, 0, 2, 6);
        V.addLine(2, 6, 4, 0);

        charMap.put('V', V.toModel('V'));

        Letter W = new Letter();
        W.addLine(0, 0, 0, 6);
        W.addLine(0, 6, 2, 4);
        W.addLine(2, 4, 4, 6);
        W.addLine(4, 6, 4, 0);

        charMap.put('W', W.toModel('W'));

        Letter X = new Letter();
        X.addLine(0, 0, 4, 6);
        X.addLine(4, 0, 0, 6);


        charMap.put('X', X.toModel('X'));

        Letter x = new Letter();
        x.addLine(0, 0, 4, 6);
        x.addLine(4, 0, 0, 6);

        charMap.put('x', x.toModel('x'));

        Letter Y = new Letter();
        Y.addLine(0, 0, 2, 2);
        Y.addLine(2, 2, 4, 0);
        Y.addLine(2, 2, 2, 6);

        charMap.put('Y', Y.toModel('Y'));

        Letter Z = new Letter();
        Z.addLine(0, 0, 4, 0);
        Z.addLine(4, 0, 0, 6);
        Z.addLine(0, 6, 4, 6);

        charMap.put('Z', Z.toModel('Z'));

        Letter l0 = new Letter();
        l0.addLine(0, 6, 4, 0);
        l0.addLine(4, 0, 0, 0);
        l0.addLine(0, 0, 0, 6);
        l0.addLine(0, 6, 4, 6);
        l0.addLine(4, 6, 4, 0);

        charMap.put('0', l0.toModel('0'));

        Letter l1 = new Letter();
        l1.addLine(0, 0, 2, 0);
        l1.addLine(2, 0, 2, 6);
        l1.addLine(0, 6, 4, 6);

        charMap.put('1', l1.toModel('1'));

        // only fixed up to this point
        Letter l2 = new Letter();
        l2.addLine(0, 0, 4, 0);
        l2.addLine(4, 0, 4, 3);
        l2.addLine(4, 3, 0, 3);
        l2.addLine(0, 3, 0, 6);
        l2.addLine(0, 6, 4, 6);

        charMap.put('2', l2.toModel('2'));


        Letter l3 = new Letter();
        l3.addLine(0, 0, 4, 0);
        l3.addLine(4, 0, 4, 6);
        l3.addLine(0, 3, 4, 3);
        l3.addLine(0, 6, 4, 6);

        charMap.put('3', l3.toModel('3'));

        Letter l4 = new Letter();
        l4.addLine(0, 0, 0, 3);
        l4.addLine(0, 3, 4, 3);
        l4.addLine(4, 0, 4, 6);


        charMap.put('4', l4.toModel('4'));

        Letter l5 = new Letter();
        l5.addLine(4, 0, 0, 0);
        l5.addLine(0, 0, 0, 3);
        l5.addLine(0, 3, 4, 3);
        l5.addLine(4, 3, 4, 6);
        l5.addLine(4, 6, 0, 6);

        charMap.put('5', l5.toModel('5'));

        Letter l6 = new Letter();
        l6.addLine(0, 0, 4, 0);
        l6.addLine(0, 0, 0, 6);
        l6.addLine(0, 3, 4, 3);
        l6.addLine(4, 3, 4, 6);
        l6.addLine(0, 6, 4, 6);

        charMap.put('6', l6.toModel('6'));


        Letter l7 = new Letter();
        l7.addLine(0, 0, 4, 0);
        l7.addLine(4, 0, 4, 6);

        charMap.put('7', l7.toModel('7'));

        Letter l8 = new Letter();
        l8.addLine(0, 0, 4, 0);
        l8.addLine(4, 0, 4, 3);
        l8.addLine(4, 3, 0, 3);
        l8.addLine(0, 3, 0, 0);
        l8.addLine(4, 6, 4, 3);
        l8.addLine(0, 3, 0, 6);
        l8.addLine(0, 6, 4, 6);

        charMap.put('8', l8.toModel('8'));


        Letter l9 = new Letter();
        l9.addLine(0, 0, 0, 3);
        l9.addLine(0, 3, 4, 3);
        l9.addLine(0, 0, 4, 0);
        l9.addLine(4, 0, 4, 6);

        charMap.put('9', l9.toModel('9'));

        Letter lex = new Letter();
        lex.addLine(2, 0, 2, 4);
        lex.addLine(2, 5.5f, 2, 6);

        charMap.put('!', lex.toModel('!'));

        Letter lcol = new Letter();
        lcol.addLine(2, 1, 2, 3);
        lcol.addLine(2, 4, 2, 6);

        charMap.put(':', lcol.toModel(':'));

        Letter lper = new Letter();
        lper.addLine(2, 5, 2, 6);

        charMap.put('.', lper.toModel('.'));

        Letter lhypen = new Letter();
        lhypen.addLine(1, 3, 3, 3);

        charMap.put('-', lhypen.toModel('-'));

        Letter lhash = new Letter();

        lhash.addLine(1, 1, 1, 5);
        lhash.addLine(0, 2, 4, 2);
        lhash.addLine(0, 4, 4, 4);
        lhash.addLine(3, 1, 3, 5);

        charMap.put('#', lhash.toModel('#'));

        Letter lcomma = new Letter();

        lcomma.addLine(2, 5, 2, 6);

        charMap.put(',', lcomma.toModel(','));

        Letter lfslash = new Letter();

        lfslash.addLine(0, 6, 4, 0);

        charMap.put('/', lfslash.toModel('/'));

        Letter lfqmark = new Letter();


        lfqmark.addLine(0, 0, 4, 0);
        lfqmark.addLine(4, 0, 4, 3);
        lfqmark.addLine(4, 3, 2, 3);
        lfqmark.addLine(2, 3, 2, 4);
        lfqmark.addLine(2, 5, 2, 6);
        charMap.put('?', lfqmark.toModel('?'));

        charMap.put(' ', new Letter().toModel(' '));
        return charMap;
    }

    @Override
    public Model getChar(char c) {
        Model model = chars.get(c);
        return (model == null) ? MISSING_CHAR : model;
    }

    /**
     * Current returns a constant gap.
     * TODO support proportional spacing
     */
    @Override
    public float gap(char c1, char c2) {
        return 0.1f;
    }

    static class Letter {

        List<Polyline> polylines = new ArrayList<>();
        float MIN_X = 0f;
        float MIN_Y = 0f;
        float MAX_X = 4f;
        float MAX_Y = 6f;

        /**
         * X and Y coordinates are in the range 0-4 and 0-6 respectively.
         */
        void addLine(float x1, float y1, float x2, float y2) {
            // map to the model coordinate range
            float mx1 = (x1 - MIN_X) * Config.SAMPLE_RANGE / MAX_X + SAMPLE_MIN;
            float my1 = (y1 - MIN_Y) * Config.SAMPLE_RANGE / MAX_Y + SAMPLE_MIN;
            float mx2 = (x2 - MIN_X) * Config.SAMPLE_RANGE / MAX_X + SAMPLE_MIN;
            float my2 = (y2 - MIN_Y) * Config.SAMPLE_RANGE / MAX_Y + SAMPLE_MIN;
            polylines.add(Polyline.closed("", Color.WHITE, new Point(mx1, my1), new Point(mx2, my2)));
        }

        public Model toModel(Character c) {
            return new GlobalModel(c.toString(), polylines);
        }
    }

}
