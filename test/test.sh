#!/bin/bash

mkdir files && cp -r samples files/ && cp -r test files/ && cp -r src files/ \
  && docker run --rm -ti -v $(pwd)/files:/home/janusgraph/janusgraph/files \
  yihongwang/janusgraph-console bin/gremlin.sh -e files/test/ModelImporterTest.groovy \
  && rm -rf files
