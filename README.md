vizipedia [![Build Status](https://travis-ci.org/rwalsh849/vizipedia.png)](https://travis-ci.org/rwalsh849/vizipedia)
=========

Vizipedia is a web application which performs the dynamic, in-browser ranking of Wikipedia pages based on their link structure through use of the PageRank algorithm.

WikipediaParser
---------------

Usage: 

    java -Xms2048m -Xmx4096m -XX:+UseConcMarkSweepGC -cp ".:/opt/kaggle-common/lib/guava-15.0.jar" net.vizipedia.WikipediaParser
    -rawMappingFile /opt/wikidumps/firstset/enwiki-latest-page.sql/enwiki-latest-page.sql
    -processedMappingFile /opt/wikidumps/pagenames_to_pageids_mapping.txt
    -rawLinkFile /opt/wikidumps/firstset/enwiki-latest-pagelinks.sql/enwiki-latest-pagelinks.sql
    -processedLinkFile /opt/wikidumps/linkstructure.txt -forceOverwrite false