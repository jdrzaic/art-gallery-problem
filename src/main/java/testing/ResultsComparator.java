package testing;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import metaheuristics.project.agp.alg.Algorithm;

public class ResultsComparator {

	public void compareResults(String benchmarkResults, String customResults) {
		try {
			BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(benchmarkResults), "UTF-8"));
			BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(customResults), "UTF-8"));
			String inputB = null;
			ArrayList<String> benchmarkData = new ArrayList<>();
			while((inputB = br1.readLine()) != null) {
				if(inputB.contains("All Vertices") != true) continue;
				String[] parsed = inputB.split("\\s+");
				benchmarkData.add(parsed[0].substring(1, parsed[0].length() - 1) + ".pol " + parsed[5]);
			}
			ArrayList<String> customData = new ArrayList<>();
			String inputC = null;
			while((inputC = br2.readLine()) != null) {
				String[] parsed = inputC.split("\\s+");
				customData.add(parsed[0] + " " + parsed[3]);
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter("toGraph/agp2009aToGraphhyb.txt"));
			for(String b : benchmarkData) {
				for(String c : customData) {
					if(c.startsWith(b.split("\\s+")[0])) {
						int diff = Integer.valueOf(b.split("\\s+")[1]) - Integer.valueOf(c.split("\\s+")[1]);
						bw.write(b.split("\\s+")[0] + " " + diff + System.getProperty("line.separator"));
					}
				}
			}
			br1.close();
			bw.close();
			br2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ResultsComparator rc = new ResultsComparator();
		rc.compareResults("toGraph/agp2009a-simplerand.csv", "agp2009a-simplerand-hybrid/agp2009a-simplerand.txt");
	}
}
