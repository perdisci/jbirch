Please take a look at [the TestX.java files](https://code.google.com/p/jbirch/source/browse/trunk/src/edu/gatech/gtisc/jbirch/test/), which should give you a good idea on how to run JBIRCH.

I get this question a lot: "what is the format of the dataset?"

The short answer is: _whatever format you like_, as long as in the end you translate each object into a _pattern vector_ of Doubles.

For example, you can read a space-separated dataset as follows:

```
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
```