package testing;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import metaheuristics.project.agp.alg.Algorithm;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy.InitialSet;
import metaheuristics.project.agp.alg.greedy.heuristics.A7;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

public class Tester {
	
	static BenchmarkFileInstanceLoader bfil = new BenchmarkFileInstanceLoader();

	public static void testAlgorithm(Algorithm alg, String root) {
		Path start = Paths.get(root);
		try {
			Files.walkFileTree(start, new TestWalker(alg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class TestWalker implements FileVisitor<Path> {

		public TestWalker(Algorithm alg) {
			this.alg = alg;
		}
		
		Algorithm alg;
		Path currentToWrite = null;
		StringBuilder sb = new StringBuilder();
		Writer bw = null;
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			currentToWrite = Paths.get("test_res/" + dir.getFileName() + ".txt");
			File f  = new File("test_res/" + dir.getFileName() + ".txt");
			f.getParentFile().mkdirs(); 
			f.createNewFile();
			bw = new BufferedWriter(
					 new OutputStreamWriter(
					 new BufferedOutputStream(
					 new FileOutputStream("test_res/" + dir.getFileName() + ".txt")),"UTF-8"));
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if((file.getFileName().toString()).compareTo(".DS_Store") == 0) return FileVisitResult.CONTINUE;
			System.out.println(file.toString());
			try {
				sb.append(createResult(alg, file));
			} catch(IllegalArgumentException e) {
				System.err.println(file.toString());
				return FileVisitResult.CONTINUE;
			}
			sb.append(System.getProperty("line.separator"));
			/*File f  = new File("test_res/" + file.getFileName() + ".newtxt");
			f.getParentFile().mkdirs(); 
			f.createNewFile();
			bw = new BufferedWriter(
					 new OutputStreamWriter(
					 new BufferedOutputStream(
					 new FileOutputStream("test_res/" + file.getFileName() + ".newtxt")),"UTF-8"));
			try {
				bw.write(createResult(alg, file));
			} catch(IllegalArgumentException e) {
				System.err.println(file.toString());
				return FileVisitResult.CONTINUE;
			}
			bw.flush();
			bw.close();*/
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			bw.write(sb.toString());
			bw.close();
			return FileVisitResult.CONTINUE;
		}
		
	}

	public static String createResult(Algorithm alg, Path file) {
		StringBuilder sb = new StringBuilder();
		GalleryInstance gi = bfil.load(file.toString());
		gi.setCameras(alg);
		sb.append(file.getFileName() + "   " + gi.cameraNum());
		return sb.toString();
	}
	
	public static void main(String[] args) {
		Tester.testAlgorithm(new HeuristicGreedy(InitialSet.TRIANGULATION_COVER, new A7()), "agp2009a-simplerand");
	}
}
