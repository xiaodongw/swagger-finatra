Publish to Maven Central

1. Publish to Sonatype
       
        gradle clean uploadArchives
        
2. Promote to Maven Central
    * Go to https://oss.sonatype.org/
    * Close the staging repository if there is no problem.
    * Release the repository if close succeeded.
    
