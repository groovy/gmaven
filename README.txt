$Id$

Building
========

Simply run Maven ;-)

    mvn install


Integration Testing
===================

To invoke integration tests using the SHITTY plugin you need to:

    mvn -Dit


Site Generation
===============

You need to perform a full build first, before site generation will function correctly:

    mvn install site

To generate the full site locally for review:

    mvn install site-deploy -DstageDistributionUrl=file:`pwd`/dist

NOTE: Looks like something is whacky somewhere, and you will *need* to
      run the site goals with the install goal for the gmaven-examples
      site to generate correctly.

Release Muck
============

NOTE: This does not really work ATM, not sure why... but release:prepare barfs
      while tagging, so have to finish up the rest of the details by hand.
      Hopefully for the next release I'll get this sorted out... or write my
      own damn plugin to deal with this crap.

First sanity check:

    mvn -Drelease release:prepare -DdryRun | tee release-prepare-dry.log

If all looks happy, first clean up:

    mvn release:clean
    rm *.log

And then:

    mvn -Drelease release:prepare | tee release-prepare.log
    mvn -Drelease release:perform | tee release-perform.log

NOTE: It appears that release:prepare needs -Dusername=xxx and -Dpassword=xxx to
      work correctly... :-(

The generated website is not deployed as part of the release process ATM, so
keep the release tree around and run site build from there.

