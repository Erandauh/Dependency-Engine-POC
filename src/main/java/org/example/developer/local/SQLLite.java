package org.example.developer.local;

import org.example.model.ReferenceLocation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SQLLite {

    // Local Delta Cache (SQLite (In-Memory DB)): Tracks uncommitted developer changes in the workspace
    public static Map<String, List<ReferenceLocation>> localDeltaIndex = new ConcurrentHashMap<>();
}
