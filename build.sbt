import com.lightbend.lagom.core.LagomVersion
organization in ThisBuild := "com.example"

scalaVersion in ThisBuild := "2.12.8"

//val akkaManagementVersion = "1.0.0"
//val akkaDiscoveryKubernetesApi = "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % akkaManagementVersion
//val lagomJavadslAkkaDiscovery = "com.lightbend.lagom" %% "lagom-javadsl-akka-discovery-service-locator" % LagomVersion.current

val lombok = "org.projectlombok" % "lombok" % "1.18.8"
val cassandraExtras = "com.datastax.cassandra" % "cassandra-driver-extras" % "3.0.0"

val ocpSoftPrettyTime = "org.ocpsoft.prettytime" % "prettytime" % "3.2.7.Final"
val webJarsFoundation = "org.webjars" % "foundation" % "6.2.3"
val webJarsFoundationIconFonts = "org.webjars" % "foundation-icon-fonts" % "d596a3cfb3"
val jBCrypt = "de.svenkubiak" % "jBCrypt" % "0.4"


lazy val root = (project in file("."))
  .settings(name := "online-auction-java")
  .aggregate(
    tools, testkit, security,
    itemApi, itemImpl,
    biddingApi, biddingImpl,
    userApi, userImpl,
    transactionApi, transactionImpl,
    searchApi, searchImpl,
    webGateway)
  .settings(commonSettings)

lazy val security = (project in file("security"))
  .settings(commonSettings)
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lagomJavadslServer % Optional
    )
  )


lazy val testkit = (project in file("testkit"))
  .settings(commonSettings)
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lagomJavadslPersistenceCassandra
    )
  )
  .dependsOn(tools)

lazy val itemApi = (project in file("item-api"))
  .settings(commonSettings)
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )
  .dependsOn(security, tools)

lazy val itemImpl = (project in file("item-impl"))
  .settings(commonSettings)
  .enablePlugins(LagomJava)
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslTestKit,
      lagomJavadslKafkaBroker,
      cassandraExtras/*,
      lagomJavadslAkkaDiscovery,
      akkaDiscoveryKubernetesApi*/
    )
  )
  //.settings(lagomForkedTestSettings: _*)
  .dependsOn(
    tools,
    testkit % "test",
    itemApi,
    biddingApi
  )

lazy val biddingApi = (project in file("bidding-api"))
  .settings(commonSettings)
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )
  .dependsOn(security)

lazy val biddingImpl = (project in file("bidding-impl"))
  .settings(commonSettings)
  .enablePlugins(LagomJava)
  .dependsOn(biddingApi, itemApi)
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslTestKit,
      lagomJavadslKafkaBroker/*,
      lagomJavadslAkkaDiscovery,
      akkaDiscoveryKubernetesApi*/
    ),
    maxErrors := 10000

  )

lazy val searchApi = (project in file("search-api"))
  .settings(commonSettings)
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )
  .dependsOn(security, tools)

lazy val searchImpl = (project in file("search-impl"))
  .settings(commonSettings)
  .enablePlugins(LagomJava)
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslTestKit,
      lagomJavadslKafkaClient,
      lombok/*,
      lagomJavadslAkkaDiscovery,
      akkaDiscoveryKubernetesApi*/
    ),
    testOptions in Test += Tests.Argument(TestFrameworks.JUnit, elasticsearch)
  )
  .dependsOn(tools, searchApi, itemApi, biddingApi)

lazy val tools = (project in file("tools"))
  .settings(commonSettings)
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )


lazy val transactionApi = (project in file("transaction-api"))
  .settings(commonSettings)
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )
  .dependsOn(security, itemApi)

lazy val transactionImpl = (project in file("transaction-impl"))
  .settings(commonSettings)
  .enablePlugins(LagomJava)
  .dependsOn(
    transactionApi,
    itemApi,
    tools,
    testkit % "test"
  ).settings(
  version := "1.0.0-SNAPSHOT",
  libraryDependencies ++= Seq(
    lagomJavadslPersistenceCassandra,
    lagomJavadslTestKit,
    lagomJavadslKafkaBroker,
    cassandraExtras/*,
    lagomJavadslAkkaDiscovery,
    akkaDiscoveryKubernetesApi*/
  )
)

lazy val userApi = (project in file("user-api"))
  .settings(commonSettings)
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )
  .dependsOn(security, tools)

lazy val userImpl = (project in file("user-impl"))
  .settings(commonSettings)
  .enablePlugins(LagomJava)
  .dependsOn(userApi, tools,
    testkit % "test"
  )
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslTestKit,
      jBCrypt,
      lagomJavadslKafkaBroker,
      cassandraExtras,
      lombok/*,
      lagomJavadslAkkaDiscovery,
      akkaDiscoveryKubernetesApi*/
    )
  )

lazy val webGateway = (project in file("web-gateway"))
  .settings(commonSettings)
  .enablePlugins(PlayJava, LagomPlay)
  .disablePlugins(PlayLayoutPlugin) // use the standard sbt layout... src/main/java, etc.
  .dependsOn(tools, transactionApi, biddingApi, itemApi, searchApi, userApi, searchApi)
  .settings(
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslClient,
       ocpSoftPrettyTime ,
       webJarsFoundation ,
       webJarsFoundationIconFonts /*,
       lagomJavadslAkkaDiscovery,
       akkaDiscoveryKubernetesApi*/
    ),

    PlayKeys.playMonitoredFiles ++= (sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value,

  
  )


def elasticsearch: String = {
  val enableElasticsearch = sys.props.getOrElse("enableElasticsearch", default = "false")
  if (enableElasticsearch == "true") {
    "--include-categories=com.example.auction.search.impl.ElasticsearchTests"
  } else {
    "--exclude-categories=com.example.auction.search.impl.ElasticsearchTests"
  }
}

def commonSettings = Seq(
  javacOptions in (Compile,compile) ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-parameters")
)

def dockerSettings = Seq(
  dockerUpdateLatest := true,
  dockerBaseImage := "adoptopenjdk/openjdk8",
  dockerUsername := sys.props.get("docker.username"),
  dockerRepository := sys.props.get("docker.registry")
)

lagomCassandraCleanOnStart in ThisBuild := true

// ------------------------------------------------------------------------------------------------

// register 'elastic-search' as an unmanaged service on the service locator so that at 'runAll' our code
// will resolve 'elastic-search' and use it. See also com.example.com.ElasticSearch
lagomUnmanagedServices in ThisBuild += ("elastic-search" -> "http://127.0.0.1:9200")
