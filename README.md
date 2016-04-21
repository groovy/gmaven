
As stated on <http://www.mojohaus.org/plugins.html> this plugin has been discontinued.

# Building

Simply run Maven ;-)

    mvn install

# Integration Testing

To invoke integration tests using the invoker plugin you need to:

    mvn -Dit install

# Site Generation

You need to perform a full build first, before site generation will function correctly:

    mvn install site

To generate the full site locally for review:

    mvn install site-deploy -DstageDistributionUrl=file:`pwd`/dist

NOTE: Looks like something is whacky somewhere, and you will *need* to
      run the site goals with the install goal for the gmaven-examples
      site to generate correctly.

## Notes

    To create site docs:
        C:\work\gmaven>mvn clean install
        C:\work\gmaven>mvn site:site org.codehaus.gmaven.support:filter-plugin:1.1-SNAPSHOT:site site:deploy -Preports,local-site

    Use profile 'reports' to generate the full set of reports.  This build takes considerably longer than without.

    Use profile 'local-site' to build site docs to ${user.home}\.m2\gmaven\maven-generated\...  This builds the site docs to the
    local file system, which is much faster than building to DAV.  Files will still need to be copied to DAV site using some other tool,
    or you can run without this profile to deploy to DAV the old-fashioned way (slow but easy).

    The filter-plugin performs standard Maven filtering on /target/site, which is a lot less cumbersome than trying to
    work within the rules for filtering imposed by the maven-site-plugin.

    NOTE:
    You can upload the site docs for the GMaven-Plugin projects like this:
        C:\work\gmaven\gmaven-plugin>mvn clean site:site org.codehaus.gmaven.support:filter-plugin:1.1-SNAPSHOT:site site:deploy -Preports

    This takes about 5 minutes, which is much faster than generating and uploading the documentation for all the projects.

# Release Muck

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

NOTE: It appears that release:prepare needs -Dusername=xxx and -Dpassword=xxx to work correctly... :-(

The generated website is not deployed as part of the release process ATM, so
keep the release tree around and run site build from there.

