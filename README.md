# JanusGraphBench
The Benchmark tool for JanusGraph performance
# Proposal 
###Populate entire graphdb with a configuration json. 
#####We should provide a default configuration that specify these elements (* is for now):  
	1. Vertices:
		* label names
		* Number of different label types
		* size (small, medium, large)
	2. Edges:
		* label names
		* Number of different label types
		* size (small, medium, large)
	3. Relation(Edge) pattern?
	4. Properties
		* Number of properties for:
		  - vertices(look at some existing data)
		  - edges(look at some existing data)
		* data types
	5. Index
		* which key should be indexed?
		* types of index (composite or mixed)?
	
###Performance Metric:
	1. How fast can we do these types of transactions (With pre-defined default or customized transaction scenarios):
		* create vertices
		* create Edges
		* Query (How to drive? See options below)
		- delete?
		- update?
		- Insert(same as create vertices?)
	2. Metric to measure:
		* Avg. TPS
		* Avg. KB/sec?(A vertex can have any # of properties and edges so its size can vary)
		* Avg. latency
	3. known variables:
		- Transactions/sec VS Threads(users)
		- Number of Transactions for each commit
###Monitor:
	* nmon
	* gclog
	
	
###Hardware
####Single node:
	* 1 x cassandra (composit index)
	- 1 x cassandra + ES (with mixed index)
####Multiple nodes:
	* Cluster of 3 cassandra later?
	- Other data store? (hbase)
###Which interface should the driver use to generate data and send queries:
####Options:
	* Send APIs through Gremlin servers (web socket or REST)?
	* Stand alone Java Application?
###Codename:
	- Hermes?
###Ohter tests:
  1. https://github.com/njeirath/titan-perf-tester 
  2. https://www.slideshare.net/NakulJeirath/addressing-performance-issues-in-titancassandra
  3. https://aws.amazon.com/blogs/big-data/performance-tuning-your-titan-graph-database-on-aws/
  4. https://www.datastax.com/dev/blog/boutique-graph-data-with-titan
  5. http://s3.thinkaurelius.com/docs/titan/0.5.1/hadoop-performance-tuning.html
