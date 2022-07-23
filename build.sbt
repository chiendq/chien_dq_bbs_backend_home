ThisBuild / scalaVersion := "2.13.8"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root =
  (project in file("."))
    .enablePlugins(PlayScala)
    .settings(
      name := """chien_dq_bbs_backend_home""",
      libraryDependencies ++= Seq(
        guice,
        "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,

        // jdbc,
        jdbc,
        // database connection
        "mysql" % "mysql-connector-java" % "8.0.29",
        // scalikejdbc,
        "org.scalikejdbc" %% "scalikejdbc" % "3.3.5",
        // skinny orm
        "org.skinny-framework" %% "skinny-orm" % "3.1.0",

        // jwt
        // https://mvnrepository.com/artifact/com.github.jwt-scala/jwt-core
        "com.github.jwt-scala" %% "jwt-core" % "9.0.5",
        // hashing libraries
        "org.mindrot" % "jbcrypt" % "0.4"

      )
    )

//javacOptions ++= Seq("-source", "1.8")