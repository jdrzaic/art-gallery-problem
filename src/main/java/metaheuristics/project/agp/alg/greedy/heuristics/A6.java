package metaheuristics.project.agp.alg.greedy.heuristics;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;


public class A6 extends Heuristic {

	@Override
	public double utilValue(Geometry polygon, Geometry union, GeometryFactory gf) {
		
		Geometry rest;
		try {
			rest = polygon.difference(union);
		}catch(Exception e) {
			return 0;
		}
		return rest.getLength();
	}

}
