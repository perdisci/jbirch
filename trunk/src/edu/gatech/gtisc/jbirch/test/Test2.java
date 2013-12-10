/*
 *  This file is part of JBIRCH.
 *
 *  JBIRCH is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JBIRCH is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JBIRCH.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/*
 *  Test2.java
 *  Copyright (C) 2009 Roberto Perdisci (roberto.perdisci@gmail.com)
 */

package edu.gatech.gtisc.jbirch.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import edu.gatech.gtisc.jbirch.cftree.CFTree;


/**
 * Usage example:
 * java -Xss100M -cp JBIRCH.jar -javaagent:JBIRCH.jar edu.gatech.gtisc.jbirch.test.Test2 0.5 10 <dataset_file>
 * 
 * @author Roberto Perdisci (roberto.perdisci@gmail.com)
 * @version v0.1
 *
 */
public class Test2 {
	
	public static void main(String[] args) throws Exception {
	
		int maxNodeEntries = 100;
		double distThreshold = Double.parseDouble(args[0]); // initial distance threshold (= sqrt(radius))
		int distFunction = CFTree.D0_DIST;
		boolean applyMergingRefinement = true;
		int memoryLimit = Integer.parseInt(args[1]); // in MB
		int memoryLimitPeriodicCheck = 10000; // verify memory usage after every 10000 inserted instances 
		String datasetFile = args[2];
		
		// This initializes the tree
		CFTree birchTree = new CFTree(maxNodeEntries,distThreshold,distFunction,applyMergingRefinement);
		
		// comment the following three lines, if you do not want auto rebuild based on memory usage constraints
		// if auto-rebuild is not active, you need to set distThreshold by hand
		birchTree.setAutomaticRebuild(true); 
		birchTree.setMemoryLimitMB(memoryLimit);
		birchTree.setPeriodicMemLimitCheck(memoryLimitPeriodicCheck); // verify memory usage after every memoryLimitPeriodicCheck
		
		// Read one instace at a time from the dataset
		// Dataset format: each line contain a set of value  v1 v2 v3... separated by spaces
		BufferedReader in = new BufferedReader(new FileReader(datasetFile));
		String line = null;
		while((line=in.readLine())!=null) {
			String[] tmp = line.split("\\s");
			
			double[] x = new double[tmp.length];
			for(int i=0; i<x.length; i++) {
				x[i] = Double.parseDouble(tmp[i]);
			}
			
			// training birch, one instance at a time...
			boolean inserted = birchTree.insertEntry(x);
			if(!inserted) {
				System.err.println("ERROR: NOT INSERTED!");
				System.exit(1);
			}
		}
		in.close();
		birchTree.finishedInsertingData();

		// get the results
		ArrayList<ArrayList<Integer>> subclusters = birchTree.getSubclusterMembers();
		
		// print the index of instances in each subcluster
		for(ArrayList<Integer> subclust : subclusters) {
			System.out.println(Arrays.toString(subclust.toArray(new Integer[0])));
		}
	}
}
