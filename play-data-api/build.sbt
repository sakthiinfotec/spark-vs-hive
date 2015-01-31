name := "play-data-api"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "1.1.0",
	"org.apache.hadoop" % "hadoop-core" % "1.2.1" excludeAll(ExclusionRule(organization = "org.eclipse.jetty"), ExclusionRule(organization = "org.mortbay.jetty")),
	"org.apache.hbase" % "hbase" % "0.98.8-hadoop1",
	"org.apache.hbase" % "hbase-client" % "0.98.8-hadoop1" excludeAll(ExclusionRule(organization = "org.mortbay.jetty")),
	"org.apache.hbase" % "hbase-common" % "0.98.8-hadoop1",
	"org.apache.hbase" % "hbase-server" % "0.98.8-hadoop1" excludeAll(ExclusionRule(organization = "org.mortbay.jetty")),
	"com.google.guava" % "guava" % "14.0.1",
	"ca.uhn.hapi" % "hapi-base" % "2.2",
	"ca.uhn.hapi" % "hapi-structures-v251" % "2.2",
	"au.com.bytecode" % "opencsv" % "2.4",
  javaJdbc,
  javaEbean,
  cache
)     

play.Project.playJavaSettings
