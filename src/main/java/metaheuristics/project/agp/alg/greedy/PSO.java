package metaheuristics.project.agp.alg.greedy;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import metaheuristics.project.agp.alg.Algorithm;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Camera;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

public class PSO implements Algorithm {

	public int populationNumPerTriang = 4;

	public int[] populationTestChange = { 1, 5, 10 };

	public int iteration = 30;

	public int[] iterationTestChange = {1, 5, 10 };

	public GalleryInstance gi;

	public GeometryFactory gf = new GeometryFactory();

	public List<Polygon> cover;

	public List<Camera> finalCameras;

	public double giArea;

	public double EPSILON = 0.01;

	public String[] testFiles = {
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-80-5.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-80-10.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-80-15.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-80-20.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-80-25.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-80-30.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-100-1.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-100-5.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-100-10.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-100-15.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-100-20.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-100-25.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-100-30.pol" };

	// "

	public void init(double epsilon, int iteracije, int population) {
		EPSILON = epsilon;
		iteration = iteracije;
		populationNumPerTriang = population;
	}

	public int ID = 0;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {
		try {
			// information about test output
			FileOutputStream fos = new FileOutputStream(new File(
					"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/results/TestingResults/results80-100.txt"));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			// points output with apropriate ID
			FileOutputStream fos2 = new FileOutputStream(new File(
					"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/results/TestingResults/points80-100.txt"));
			BufferedWriter bw2 = new BufferedWriter(
					new OutputStreamWriter(fos2));
			PSO pso = new PSO();
			for (String file : pso.testFiles) {
				System.out.println("Poceo sam za :  " + file);
				pso.gi = new BenchmarkFileInstanceLoader().load(file);
				for (int pop : pso.populationTestChange) {
					pso.populationNumPerTriang = pop;
					for (int iter : pso.iterationTestChange) {
						System.out.println("ID testa :  " + pso.ID);
						pso.iteration = iter;
						bw.write("ID:   " + pso.ID + "\n");
						bw.write("Test sample:  " + file + "\n");
						bw.write("Population number:  " + pop + "\n");
						bw.write("Number of iterations:  " + iter + "\n");
						List<TriangleOptimization> psoTriangles = new ArrayList<>();
						long time = System.currentTimeMillis();
						pso.findBestCameraPositions(psoTriangles, pso.gi);
						pso.calculateMinCameraNum(psoTriangles);
						bw.write("Proslo vrijeme: "
								+ (System.currentTimeMillis() - time)
								+ " \nBroj potrebnih kamera: "
								+ pso.cover.size() + "\n");
						bw.write("Kamere pokrivaju:  "
								+ pso.union.buffer(0).getArea() + "  od  "
								+ pso.giArea + "\n");
						bw.write("Points: \n");
						int newLine = 0;
						bw2.write("ID:   " + pso.ID + "\n");
						for (Camera cam : pso.finalCameras) {
							newLine++;
							bw2.write("(" + cam.x + ", " + cam.y + "),  ");
							if (newLine % 6 == 0) {
								bw2.write("\n");
							}
						}
						bw.write("\n");
						bw2.write("\n");
						pso.ID++;
					}
					bw.write("\n");
				}

			}
			bw.close();
			bw2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param psoTriangles
	 */
	public void findBestCameraPositions(List<TriangleOptimization> psoTriangles,
			GalleryInstance gi) {
		ConformingDelaunayTriangulationBuilder cdtb = new ConformingDelaunayTriangulationBuilder();

		cdtb.setSites(createPolygon(gi.getVertices()));
		GeometryCollection gc = (GeometryCollection) cdtb.getTriangles(gf);
		// printGeom(gc);
		// System.out.println(gc.union().getArea());
		// System.out.println(createPolygon(gi.getVertices()).getArea());
		// System.out.println(gc.getNumGeometries());
		Polygon gallery = createPolygon(gi.getVertices());

		for (int i = 0; i < gc.getNumGeometries(); ++i) {
			Polygon p = (Polygon) gc.getGeometryN(i);
			if (!gallery.contains(p)) {
				continue;
			}
			TriangleOptimization triangleOpt = new TriangleOptimization(gi,
					populationNumPerTriang, p, iteration);
			triangleOpt.process(gi);
			psoTriangles.add(triangleOpt);
		}

		giArea = gi.calculateArea();
		Collections.sort(psoTriangles);
	}

	/**
	 * @param psoTriangles
	 * 
	 */
	public Geometry union;

	public void calculateMinCameraNum(List<TriangleOptimization> psoTriangles) {
		cover = new ArrayList();
		finalCameras = new ArrayList<>();
		// provjera da li se dodavanjem kamere neznatno povecala vidljivost
		double unnecessaryCamCheck = 0;
		int br = 0;

		for (TriangleOptimization to : psoTriangles) {
			cover.add(to.visiblePolygon);
			finalCameras.add(to.getBest().getCam());
		}

		updateCoveredArea();

		double max = union.getArea();

		for (TriangleOptimization to : psoTriangles) {
			cover.remove(to.visiblePolygon);
			finalCameras.remove(to.getBest().getCam());
			updateCoveredArea();
			if (max - union.getArea() > EPSILON) {
				cover.add(to.visiblePolygon);
				finalCameras.add(to.getBest().getCam());
				updateCoveredArea();
			}
		}

		for (Camera c : finalCameras) {
			gi.addCamera(c);
		}
	}

	public void updateCoveredArea() {
		Polygon[] polygons = cover.toArray(new Polygon[cover.size()]);
		GeometryCollection polygonCollection = gf
				.createGeometryCollection(polygons);
		union = polygonCollection.buffer(0);
	}

	/**
	 * 
	 * @param bound
	 * @return
	 */
	Polygon createPolygon(List<Coordinate> bound) {
		Coordinate[] boundary = new Coordinate[bound.size() + 1];
		for (int i = 0; i < boundary.length - 1; ++i)
			boundary[i] = bound.get(i);
		boundary[boundary.length - 1] = bound.get(0);
		return gf.createPolygon(boundary);
	}

	@Override
	public void process(GalleryInstance gi) {
		List<TriangleOptimization> psoTriangles = new ArrayList<>();

		long time = System.currentTimeMillis();

		findBestCameraPositions(psoTriangles, gi);
		calculateMinCameraNum(psoTriangles);

		return;
	}

}
