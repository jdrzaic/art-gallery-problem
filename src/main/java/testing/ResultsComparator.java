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
				String[] parsed = inputB.split("\\s+");
				benchmarkData.add(parsed[0] + ".pol " + parsed[2]);
			}
			ArrayList<String> customData = new ArrayList<>();
			String inputC = null;
			while((inputC = br2.readLine()) != null) {
				String[] parsed = inputC.split("\\s+");
				customData.add(parsed[0] + " " + parsed[3]);
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter("test_results_and_samples/graphFin/GBBtest.txt"));
			for(String b : benchmarkData) {
				for(String c : customData) {
					System.out.println("pair");
					System.out.println(b.split("\\s+")[0]);
					System.out.println(c.split("\\s+")[0]);
					if(c.startsWith(b.split("\\s+")[0])) {
						System.out.println("match\n");
						bw.write(b.split("\\s+")[0] + " " + b.split("\\s+")[1] + " " + c.split("\\s+")[1] + System.getProperty("line.separator"));
					}
				}
			}
			bw.flush();
			br1.close();
			bw.close();
			br2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void compareResultsOur(String benchmarkResults, String customResults) {
		try {
			BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(benchmarkResults), "UTF-8"));
			BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(customResults), "UTF-8"));
			String inputB = null;
			ArrayList<String> benchmarkData = new ArrayList<>();
			while((inputB = br1.readLine()) != null) {
				String[] parsed = inputB.split("\\s+");
				benchmarkData.add(parsed[0] + " " + parsed[3]);
			}
			ArrayList<String> customData = new ArrayList<>();
			String inputC = null;
			while((inputC = br2.readLine()) != null) {
				String[] parsed = inputC.split("\\s+");
				customData.add(parsed[0] + " " + parsed[3]);
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter("test_results_and_samples/graphFin/2009greedy_hybrid.txt"));
			for(String b : benchmarkData) {
				for(String c : customData) {
					System.out.println("pair");
					System.out.println(b.split("\\s+")[0]);
					System.out.println(c.split("\\s+")[0]);
					if(c.startsWith(b.split("\\s+")[0])) {
						System.out.println("match\n");
						bw.write(b.split("\\s+")[0] + " " + b.split("\\s+")[1] + " " + c.split("\\s+")[1] + System.getProperty("line.separator"));
					}
				}
			}
			bw.flush();
			br1.close();
			bw.close();
			br2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ResultsComparator rc = new ResultsComparator();
		//rc.compareResultsOur("test_results_and_samples/results/TestingResults/2009a-simplerand-greedy-union-98.8/agp2009a-simplerand.txt", 
			//	"test_results_and_samples/results/TestingResults/2009a-simplerand-hybrid-union-99.0/agp2009a-simplerand300.txt");
		rc.compareResults("test_results_and_samples/results/dataResults/randsimple.csv", 
				"test_results_and_samples/results/TestingResults/GBBTestingFiles.txt");
	}
}
