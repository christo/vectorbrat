package com.chromosundrift.vectorbrat;

import java.util.Set;

public interface AppController {
    Set<String> getAnimators();

    void setAnimator(String name) throws VectorBratException;
}
