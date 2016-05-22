val ant = "org.apache.ant" % "ant" % "1.9.4"
val apacheCommons = "org.apache.commons" % "commons-lang3" % "3.1"
val commonsIo = "commons-io" % "commons-io" % "1.3.2"
val gson = "com.google.code.gson" % "gson" % "2.2.3"
val goPluginLibrary = "cd.go.plugin" % "go-plugin-api" % "14.4.0" % Provided

val junit = "junit" % "junit" % "4.10" % Test
val junitInterface = "com.novocode" % "junit-interface" % "0.11" % Test
val hamcrest = "org.hamcrest" % "hamcrest-all" % "1.3" % Test
val mockito = "org.mockito" % "mockito-all" % "1.9.0" % Test

val appVersion = sys.env.get("SNAP_PIPELINE_COUNTER") orElse sys.env.get("GO_PIPELINE_LABEL") getOrElse "1.0.0-SNAPSHOT"

lazy val root = project in file(".") aggregate dockerTask

lazy val commonSettings = Seq(
  organization := "com.stacktoheap",
  version := appVersion,
  unmanagedBase := file(".") / "lib",
  libraryDependencies ++= Seq(
    apacheCommons, commonsIo, goPluginLibrary, gson, junit, hamcrest, mockito
  ),
  variables in EditSource += ("version", appVersion),
  targetDirectory in EditSource <<= baseDirectory(_ / "target" / "transformed"),
  sources in EditSource <++= baseDirectory.map(d => (d / "template" / "plugin.xml").get),
  unmanagedResourceDirectories in Compile += { baseDirectory.value / "target" / "transformed" }
)

lazy val dockerTask = (project in file("docker-task")).
  settings(commonSettings: _*).
  settings(
    name := "docker-task",
    crossPaths := false,
    autoScalaLibrary := false,
    javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
  )
