package org.example;

import org.example.developer.local.LocalProxyEngine;
import org.example.model.ReferenceLocation;
import org.example.remote.server.CsvDataIngestor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyEnginePoC {

    // --- Execution Simulation ---
    public static void main(String[] args) {

        /*
            This represents the ingestion of baseline data to the graph db
            AWS S3 -> SQS -> Fargate -> Neptune
         */
        System.out.println("=== Step 1: Ingesting Base Line Data ===");
        String csvFile = "src/main/resources/REF9925X-calls_with_files_.csv";
        CsvDataIngestor csvDataIngestor = new CsvDataIngestor();
        csvDataIngestor.ingestCsvDataViaPipeline(csvFile);
        System.out.println("Global Baseline Ingested successfully");


        /*
            This represents developer changing a code file in his local machine via IDE
            Local File Watcher -> LPE -> SQL Lite
         */
        LocalProxyEngine localProxyEngine = new LocalProxyEngine();
        System.out.println("=== Step 2: Simulating Uncommitted Local Save (Developer Modifies File) ===");
        // Developer moved the reference from lines 63/64 to line 75 inside 'accrul/source'
        Map<String, List<Integer>> localMockChanges = new HashMap<>();
        localMockChanges.put("Language_sys.Translate_Constant", List.of(75));
        localProxyEngine.updateLocalFileDelta("accrul/source/accrul/database/AccLibCurrencyAmount.plsvc",localMockChanges );


        /*
            This represents the On Demand Query from IDE e.g.
         */
        System.out.println("=== Step 3: Running On Demand Query ===");
        List<ReferenceLocation> refs = localProxyEngine.findAllReferences("Language_sys.Translate_Constant".toLowerCase());
        System.out.printf("Found %d References...%n", refs.size());
        refs.forEach(r -> System.out.println(" Found reference -> " + r));
    }
}