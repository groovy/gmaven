$Id$

Building
========

Simply run Maven ;-)

    mvn install


Integration Testing
===================

To invoke integration tests using the SHITTY plugin you need to:

    mvn -Dshit


IDE Configuration
=================

To generate IDEA configuration w/sources & javadocs:

    mvn -Pidea

Or the basic configuration:

    mvn idea:idea


Site Generation
===============

You need to perform a full build first, before site generation will function correctly:

    mvn install site

To generate the full site locally for review:

    mvn install site-deploy -Dstage.distributionUrl=file:`pwd`/dist

NOTE: Looks like something is whacky somewhere, and you will *need* to
      run the site goals with the install goal for the example-plugins
      site to generate correctly.

And then open up the main index in a browser, as in:

    open dist/site/index.html

Uber build:

    (find . -name target -type d | xargs rm -rf) && rm -rf dist && mvn -Dshit install site-deploy -Dstage.distributionUrl=file:`pwd`/dist && open dist/site/index.html

