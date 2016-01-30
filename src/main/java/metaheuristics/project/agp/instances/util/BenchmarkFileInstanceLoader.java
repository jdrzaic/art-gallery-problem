package metaheuristics.project.agp.instances.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.*;

import com.vividsolutions.jts.geom.Coordinate;

import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Polygon;

/**
 * Class loading instances from benchmark files;
 * www.ic.unicamp.br/~cid/Problem-instances/Art-Gallery/AGPPG/index.html
 * @author jelena
 *
 */
public class BenchmarkFileInstanceLoader implements InstanceLoader{
	
	@Override
	public GalleryInstance load(String filename) {
		String content = getContent(filename);
		GalleryInstance gi = parseContent(content);
		return gi;
	}
	
	/**
	 * Method reads content of a file into a string.
	 * @param filename 
	 * @return content of a file.
	 */
	private String getContent(String filename) {
		FileInputStream inputStream = null;
		String everything = null;
		try {
			inputStream = new FileInputStream(filename);
		    everything = IOUtils.toString(inputStream);
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}finally {
		    try {
		    	if(inputStream != null) {
		    		inputStream.close();
		    	}
			} catch (IOException ignorable) {
				ignorable.getMessage();
			}
		}
		return everything;
	}

	/**
	 * Method parsing data from benchmark file.
	 * Instances that can be parsed are available on: 
	 * {@link www.ic.unicamp.br/~cid/Problem-instances/Art-Gallery/AGPPG/index.html}
	 * @param content content of benchmark file, as string.
	 * @return {@link GalleryInstance} created.
	 */
	private GalleryInstance parseContent(String content) {
		String[] comps = content.split("\\s+");
		List<Polygon> boundaries = new ArrayList<Polygon>();
		int i = 0;
		while(true) {
			int nOfVertices = Integer.valueOf(comps[i]);
			//todo new method
			Polygon p = new Polygon();
			for(int j = 1; j < 2 * nOfVertices + 1; j += 2) {
				double x = parseFraction(comps[j + i]);
				double y = parseFraction(comps[j + 1 + i]);
				Coordinate v = new Coordinate(x, y);
				p.addVertex(v);
			}
			//method end
			boundaries.add(p);
			if(i == 0) i++;
			i += 2 * nOfVertices + 1;
			if(i > comps.length - 1) break;
		}
		if(boundaries.size() == 1) {
			return new GalleryInstance(boundaries.get(0).getVertices());
		}else {
			return new GalleryInstance(boundaries.get(0).getVertices(), 
					boundaries.subList(1, boundaries.size()));
		}
	}

	/**
	 * Method parsing fraction from a string,
	 * and returning its double value.
	 * @param string fraction
	 * @return value of fraction.
	 */
	private double parseFraction(String string) {
		String[] nums = string.split("/");
		return Maths.round(Double.valueOf(nums[0]) / Double.valueOf(nums[1]), 10);
	}
	
}
