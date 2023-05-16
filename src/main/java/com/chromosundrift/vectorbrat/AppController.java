package com.chromosundrift.vectorbrat;

import java.util.List;

public interface AppController {
    List<String> getAnimators();

    void setAnimator(String name) throws VectorBratException;
}
