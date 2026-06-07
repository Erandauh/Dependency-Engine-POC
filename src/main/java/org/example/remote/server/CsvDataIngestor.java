package org.example.remote.server;

import org.example.model.ReferenceLocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.example.remote.server.AWSNeptune.globalIndex;

public class CsvDataIngestor {

    public void ingestCsvDataViaPipeline(String csvFilePath) {
        File file = new File(csvFilePath);
        if (!file.exists()) {
            System.err.println("Ingestion failed: File does not exist at " + csvFilePath);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {     // Skip CSV Header row
                    isHeader = false;
                    continue;
                }

                // Splitting by comma - assuming clean CSV structure for PoC
                String[] tokens = line.split(",");
                if (tokens.length < 8) continue;

                String filePath         = tokens[0].trim();
                String component        = tokens[1].trim();
                String language         = tokens[2].trim();
                String logicalUnit      = tokens[3].trim();
                int lineNumber          = tokens[4].trim().isEmpty() ? 0 : Integer.parseInt(tokens[4].trim());
                String callerSymbol     = tokens[5].trim();
                String referencedSymbol = tokens[8].trim().toLowerCase();
                /*
                    Column 8 is referenced_symbol is the key, because we need to search by that
                    e.g. :- When asks to search all the references for Company_Finance_API.Get_Currency_Code
                 */

                ReferenceLocation location = new ReferenceLocation(
                        filePath, component, language, logicalUnit, lineNumber, callerSymbol
                );

                // ComputeIfAbsent guarantees atomic thread-safe initialization of the L2 Cache array
                globalIndex.computeIfAbsent(referencedSymbol, k -> new CopyOnWriteArrayList<>())
                        .add(location);
            }
            System.out.printf("Successfully hydrated L2 Cache with %d base symbols.\n", globalIndex.size());
        } catch (Exception e) {
            System.err.println("Error processing ingestion file [" + csvFilePath + "]: " + e.getMessage());
        }
    }
}
