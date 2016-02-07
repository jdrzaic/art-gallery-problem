package metaheuristics.project.agp.alg.pso;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Triangle;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import metaheuristics.project.agp.alg.Algorithm;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Camera;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

public class PSO implements Algorithm {

	public int populationNumPerTriang = 5;

	public int iteration = 20;

	public GalleryInstance gi;

	public GeometryFactory gf = new GeometryFactory();

	public List<Polygon> cover;

	public List<Camera> finalCameras;

	public double giArea;

	public double EPSILON = 0.01;
	
	public double difference;

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
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2009a-simplerand/randsimple-100-30.pol" 
			};

	public void init(double epsilon, int iteracije, int population) {
		EPSILON = epsilon;
		iteration = iteracije;
		populationNumPerTriang = population;
	}

	public int ID = 0;

	/**
	 * 
	 * @param psoTriangles
	 */
	public void findBestCameraPositions(List<TriangleOptimization> psoTriangles, GalleryInstance gi) {
		Polygon gallery = createPolygon(gi.getVertices(), gi.getHoles());
		List<Polygon> polygons = createInitialTriangCover(gi, gallery);

		for (Polygon t : polygons) {
			if (!gallery.contains(t)) {
				continue;
			}
			TriangleOptimization triangleOpt = new TriangleOptimization(gi, populationNumPerTriang, t, iteration);
			triangleOpt.process(gi);
			psoTriangles.add(triangleOpt);
		}

		giArea = gi.calculateArea();
		difference = EPSILON * giArea;
		Collections.sort(psoTriangles);
	}
	
	private List<Polygon> createInitialTriangCover(GalleryInstance gi, Polygon main) {
		List<Polygon> ini = new ArrayList<>();
		ConformingDelaunayTriangulationBuilder cdtb =
				new ConformingDelaunayTriangulationBuilder();
		cdtb.setSites(main);
		GeometryCollection gc = (GeometryCollection)cdtb.getTriangles(gf);
		for(int i = 0; i < gc.getNumGeometries(); ++i) {
			Polygon p = (Polygon)gc.getGeometryN(i);
			ini.add(p);
		}
		return ini;
	}

	/**
	 * @param psoTriangles
	 * 
	 */
	public Geometry union;

	public void calculateMinCameraNum(List<TriangleOptimization> psoTriangles, GalleryInstance gi) {
		cover = new ArrayList<>();
		finalCameras = new ArrayList<>();
		// provjera da li se dodavanjem kamere neznatno povecala vidljivost

		for (TriangleOptimization to : psoTriangles) {
			cover.add(to.visiblePolygon);
		}

		updateCoveredArea();

		double max = union.getArea();

		for (TriangleOptimization to : psoTriangles) {
			cover.remove(to.visiblePolygon);
			updateCoveredArea();
			if (max - union.getArea() > difference) {
				cover.add(to.visiblePolygon);
				finalCameras.add(to.getBest().getCam());
				updateCoveredArea();
			}
		}

		gi.getCameras().addAll(finalCameras);
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
	Polygon createPolygon(List<Coordinate> bound, List<metaheuristics.project.agp.instances.components.Polygon> holes) {
		Coordinate[] boundary = new Coordinate[bound.size() + 1];
		for(int i = 0; i < boundary.length - 1; ++i) boundary[i] = bound.get(i);
		boundary[boundary.length - 1] = bound.get(0);
		LinearRing boundRing = gf.createLinearRing(boundary);
		LinearRing[] holesRing = new LinearRing[holes.size()];
		int index = 0;
		for(metaheuristics.project.agp.instances.components.Polygon h : holes) {
			Coordinate[] boundarHole = new Coordinate[h.getVertices().size() + 1];
			for(int i = 0; i < h.getVertices().size(); ++i) boundarHole[i] = h.getOnIndex(i);
			boundarHole[h.getVertices().size()] = h.getOnIndex(0);
			holesRing[index] = gf.createLinearRing(boundarHole);
			++index;
		}
		Polygon p = gf.createPolygon(boundRing, holesRing);
		return p;
	}
	
	
	@Override
	public void process(GalleryInstance gi) {
		List<TriangleOptimization> psoTriangles = new ArrayList<>();

		long time = System.currentTimeMillis();

		findBestCameraPositions(psoTriangles, gi);
		calculateMinCameraNum(psoTriangles, gi);

		return;
	}

}
