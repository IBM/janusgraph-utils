#!/usr/bin/python
import sys
import simplejson as json

def replicate_vertex(conf,pos, i):
	p = conf["VertexTypes"][pos]["columns"]["T{}-P1".format(pos+1)]
	for x in range(2, i+1):
		new_key = "T{}-P{}".format(pos+1, str(x))
		conf["VertexTypes"][pos]["columns"][new_key] = p
	return conf

def replicate_edge(conf, pos, i):
  p = conf["EdgeTypes"][pos]["columns"]["E{}-P1".format(pos+1)]
  for x in range(2, i+1):
    new_key ='E{}-P{}'.format(pos+1,str(x))
    conf["EdgeTypes"][pos]["columns"][new_key] = p
  return conf

def main():
	f = open(sys.argv[1], "r")
	j = json.load(f)
	json.dump(replicate_vertex(j,0, int(sys.argv[3])), open(sys.argv[2], "w"))
	json.dump(replicate_vertex(j,1, 2*int(sys.argv[3])), open(sys.argv[2], "w"))
	json.dump(replicate_vertex(j,2, 2*int(sys.argv[3])), open(sys.argv[2], "w"))

	json.dump(replicate_edge(j, 0, int(sys.argv[3])), open(sys.argv[2], "w"))
	json.dump(replicate_edge(j, 1, int(sys.argv[3])), open(sys.argv[2], "w"))

if __name__ == "__main__":
    main()
