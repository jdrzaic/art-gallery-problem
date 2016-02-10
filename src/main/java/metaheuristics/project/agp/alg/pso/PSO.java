package metaheuristics.project.agp.alg.pso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import metaheuristics.project.agp.alg.Algorithm;
import metaheuristics.project.agp.algorithms.pso.components.TriangleOptimization;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Camera;

public class PSO implements Algorithm {

	/**
	 * Algorithm population number.
	 */
	public int population = 3;

	/**
	 * Algorithm iteration number.
	 */
	public int iteration = 15;
	
	/**
	 * Algorithm toleration percentage.
	 */
	public double toleration = 0.01;

	/**
	 * Gallery instance.
	 */
	public GalleryInstance gi;

	/**
	 * Geometry factory.
	 */
	public GeometryFactory gf = new GeometryFactory();

	public List<Camera> finalCameras;

	public double giArea;

	public double eps;
	

	/**
	 * 
	 * @param psoTriangles
	 */
	public void findBestCameraPositions(List<TriangleOptimization> psoTriangles, GalleryInstance gi) {
		Polygon gallery = createPolygon(gi.getVertices(), gi.getHoles());
		List<Polygon> polygons = createInitialTriangCover(gi, gallery);
		List<Polygon> cover = new ArrayList<>();
		Geometry initUnion = null;
		int k = 0;
		double area = 0;
		for (Polygon t : polygons) {
			TriangleOptimization triangleOpt = new TriangleOptimization(gi, population, t, iteration);
			if(!gf.createPoint(triangleOpt.getBest().getCam()).within(gallery)){
				continue;
			}

			if(initUnion != null){
				area = initUnion.getArea();
			}
			cover.add(triangleOpt.visiblePolygon);
			initUnion = updateCoveredArea(cover);
			if(initUnion.getArea() - area < 0.007 * giArea){
				cover.remove(triangleOpt.visiblePolygon);
				initUnion = updateCoveredArea(cover);
				continue;
			}
			k++;
			triangleOpt.process(gi);
			psoTriangles.add(triangleOpt);
		}
		System.out.println(k);

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

	public void calculateMinCameraNum(List<TriangleOptimization> psoTriangles, GalleryInstance gi) {
		List<Polygon> cover = new ArrayList<>();
		finalCameras = new ArrayList<>();
		Geometry union = null;

		for (TriangleOptimization to : psoTriangles) {
			cover.add(to.visiblePolygon);
		}

		union = updateCoveredArea(cover);
		double max = union.getArea();
		Collections.sort(psoTriangles);

		for (TriangleOptimization to : psoTriangles) {
			cover.remove(to.visiblePolygon);
			union = updateCoveredArea(cover);
			double dif = max - union.getArea();
			if (dif  > eps) {
				cover.add(to.visiblePolygon);
				finalCameras.add(to.getBest().getCam());
				union = updateCoveredArea(cover);
			}
		}

		gi.getCameras().addAll(finalCameras);
	}

	public Geometry updateCoveredArea (List<Polygon> cover) {
		Polygon[] polygons = cover.toArray(new Polygon[cover.size()]);
		GeometryCollection polygonCollection = gf
				.createGeometryCollection(polygons);
		return polygonCollection.buffer(0);
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
		
		giArea = gi.calculateArea();
		eps = toleration * giArea;
		
		findBestCameraPositions(psoTriangles, gi);
		calculateMinCameraNum(psoTriangles, gi);
		return;
	}
	
	/**
	 * Method for changin initial parameters: toleration percentage, iteration and population number.
	 * @param epsilon
	 * @param iteracije
	 * @param population
	 */
	public void init(double epsilon, int iteracije, int population) {
		this.toleration = epsilon;
		this.iteration = iteracije;
		this.population = population;
	}

}
