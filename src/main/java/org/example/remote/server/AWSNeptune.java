package org.example.remote.server;

import org.example.model.ReferenceLocation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
    This represents the Graph DB
 */
public class AWSNeptune {

    public static Map<String, List<ReferenceLocation>> globalIndex = new ConcurrentHashMap<>();
}
