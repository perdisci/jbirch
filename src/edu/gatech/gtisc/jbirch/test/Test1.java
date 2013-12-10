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
 *  Test1.java
 *  Copyright (C) 2009 Roberto Perdisci (roberto.perdisci@gmail.com)
 */

package edu.gatech.gtisc.jbirch.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import edu.gatech.gtisc.jbirch.cftree.CFTree;

/**
 * Usage example:
 * java -Xss100M -cp JBIRCH.jar -javaagent:JBIRCH.jar edu.gatech.gtisc.jbirch.test.Test1 100 0.5 1 true <dataset_file>
 * 
 * @author Roberto Perdisci (roberto.perdisci@gmail.com)
 * @version v0.1
 *
 */
public class Test1 {

	public static void main(String[] args) throws Exception {
		int maxNodeEntries = Integer.parseInt(args[0]);
		double distThreshold = Double.parseDouble(args[1]);
		int distFunction = Integer.parseInt(args[2]);
		boolean applyMergingRefinement = Boolean.parseBoolean(args[3]);
		String datasetFile = args[4];
		
		CFTree birchTree = new CFTree(maxNodeEntries,distThreshold,CFTree.D0_DIST,applyMergingRefinement);
		birchTree.setMemoryLimit(100*1024*1024);
		
		BufferedReader in = new BufferedReader(new FileReader(datasetFile));
		
		String line = null;
		while((line=in.readLine())!=null) {
			String[] tmp = line.split("\\s");
			
			double[] x = new double[tmp.length];
			for(int i=0; i<x.length; i++) {
				x[i] = Double.parseDouble(tmp[i]);
			}
			
			boolean inserted = birchTree.insertEntry(x);
			if(!inserted) {
				System.err.println("NOT INSERTED!");
				System.exit(1);
			}
		}
		
		System.out.println("*************************************************");
		System.out.println("*************************************************");
		birchTree.printCFTree();
		System.out.println("*************************************************");
		System.out.println("*************************************************");
		
		System.out.println("****************** LEAVES *******************");
		birchTree.printLeafEntries();
		System.out.println("****************** END *******************");
		
		// System.out.println("****************** INDEXES *******************");
		// birchTree.printLeafIndexes();
		// System.out.println("****************** END *******************");
		System.out.println("Total CF-Nodes = " + birchTree.countNodes());
		System.out.println("Total CF-Entries = " + birchTree.countEntries());
		System.out.println("Total CF-Leaf_Entries = " + birchTree.countLeafEntries());
		
		
		
		CFTree oldTree = birchTree;
		CFTree newTree = null;
		double newThreshold = distThreshold;
		for(int i=0; i<10; i++) {
			newThreshold = oldTree.computeNewThreshold(oldTree.getLeafListStart(), distFunction, newThreshold);
			System.out.println("new Threshold ["+i+"] = " + newThreshold);
			
			newTree = oldTree.rebuildTree(maxNodeEntries, newThreshold, distFunction, true, false);
			System.out.println("Total CF-Nodes in new Tree["+i+"] = " + newTree.countNodes());
			System.out.println("Total CF-Entries in new Tree["+i+"] = " + newTree.countEntries());
			System.out.println("Total CF-Leaf_Entries in new Tree["+i+"] = " + newTree.countLeafEntries());	
			System.out.println("Total CF-Leaf_Entries lambdaSS in new Tree["+i+"] = " + newTree.computeSumLambdaSquared());
			
			oldTree = newTree;
			
		}
		
		ArrayList<ArrayList<Integer>> members = newTree.getSubclusterMembers();
		
	}
	
}
