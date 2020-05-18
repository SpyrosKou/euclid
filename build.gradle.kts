plugins {
   id("us.ihmc.ihmc-build") version "0.20.2"
   id("us.ihmc.ihmc-ci") version "5.9"
}

ihmc {
   group = "us.ihmc"
   version = "0.14.2"
   vcsUrl = "https://github.com/ihmcrobotics/euclid"
   openSource = true

   configureDependencyResolution()
   configurePublications()
}

mainDependencies {
   api("org.ejml:dense64:0.30")
}

geometryDependencies {
   api(ihmc.sourceSetProject("main"))
}

frameDependencies {
   api(ihmc.sourceSetProject("main"))
   api(ihmc.sourceSetProject("geometry"))
}

shapeDependencies {
   api(ihmc.sourceSetProject("main"))
   api(ihmc.sourceSetProject("geometry"))
}

frameShapeDependencies {
   api(ihmc.sourceSetProject("main"))
   api(ihmc.sourceSetProject("geometry"))
   api(ihmc.sourceSetProject("shape"))
   api(ihmc.sourceSetProject("frame"))
}

testDependencies {
   api(ihmc.sourceSetProject("geometry"))
   api(ihmc.sourceSetProject("frame"))
   api(ihmc.sourceSetProject("shape"))
   api(ihmc.sourceSetProject("frame-shape"))

   api("org.pitest:pitest:1.4.3")
   api("org.pitest:pitest-command-line:1.4.3")
}