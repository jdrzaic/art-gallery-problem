package metaheuristics.project.agp.alg.greedy.heuristics;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public abstract class Heuristic {

	public abstract double utilValue(Geometry polygon, Geometry cover,
			GeometryFactory gf);
}
