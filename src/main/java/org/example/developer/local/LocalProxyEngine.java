package org.example.developer.local;

import org.example.model.ReferenceLocation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.example.developer.local.SQLLite.localDeltaIndex;
import static org.example.remote.server.AWSNeptune.globalIndex;

public class LocalProxyEngine {

    // Tracks which files have uncommitted changes
    private final Set<String> maskedLocalFiles = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void updateLocalFileDelta(String filePath, Map<String, List<Integer>> localChanges) {

        // Mark this file as "dirty" to mask its old global locations
        maskedLocalFiles.add(filePath);

        // Inject the new modified locations into Local Delta Cache
        localChanges.forEach((symbol, lines) -> {
            for (int lineNum : lines) {
                ReferenceLocation loc = new ReferenceLocation(filePath, "accrul",
                        "plsql", "ModifiedUnit", lineNum, "ModifiedMethod");
                localDeltaIndex.computeIfAbsent(symbol, k -> new CopyOnWriteArrayList<>()).add(loc);
            }
        });
    }

    public List<ReferenceLocation> findAllReferences(String referencedSymbol) {
        long startTime = System.nanoTime();
        List<ReferenceLocation> combinedResults = new ArrayList<>();

        // 1. Gather baseline data (ignoring files modified locally)
        List<ReferenceLocation> baselineRefs = globalIndex.get(referencedSymbol);
        if (baselineRefs != null) {
            for (ReferenceLocation ref : baselineRefs) {
                if (!maskedLocalFiles.contains(ref.filePath)) {
                    combinedResults.add(ref);
                }
            }
        }

        // 2.Uncommitted additions
        List<ReferenceLocation> localRefs = localDeltaIndex.get(referencedSymbol);
        if (localRefs != null) {
            combinedResults.addAll(localRefs);
        }

        long durationNs = System.nanoTime() - startTime;
        System.out.printf(">> Query for [%s] resolved in %.4f ms\n", referencedSymbol, durationNs / 1_000_000.0);
        return combinedResults;
    }
}
