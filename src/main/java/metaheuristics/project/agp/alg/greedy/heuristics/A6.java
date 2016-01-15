package metaheuristics.project.agp.alg.greedy.heuristics;

import java.util.HashMap;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import metaheuristics.project.agp.instances.components.Camera;

public class A6 extends Heuristic {

	@Override
	public double utilValue(Polygon polygon, Geometry union, GeometryFactory gf) {
		
		Geometry rest;
		try {
			rest = polygon.difference(union);
		}catch(Exception e) {
			return 0;
		}
		return rest.getLength();
	}

}
