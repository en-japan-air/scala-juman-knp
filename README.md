# scala-juman-knp

[![Build Status](https://travis-ci.org/en-japan/scala-juman-knp.svg?branch=master)](https://travis-ci.org/en-japan/scala-juman-knp)
[![Coverage Status](https://coveralls.io/repos/github/en-japan/scala-juman-knp/badge.svg?branch=master)](https://coveralls.io/github/en-japan/scala-juman-knp?branch=master)

Scala wrapper around [JUMAN](http://nlp.ist.i.kyoto-u.ac.jp/index.php?cmd=read&page=JUMAN) and [KNP](http://lotus.kuee.kyoto-u.ac.jp/~john/pyknp.html)(http://nlp.ist.i.kyoto-u.ac.jp/EN/?KNP).
Inspired by [pyknp](http://lotus.kuee.kyoto-u.ac.jp/~john/pyknp.html) ([source](https://github.com/en-japan/pyknp)).

## How to install

In your `build.sbt`:
```
resolvers += "en-japan Maven OSS" at "http://dl.bintray.com/en-japan/maven-oss"

libraryDependencies += "com.enjapan" %% "scala-juman-knp" % "0.0.5"
```

## How to use

The library uses cats' `Xor` for exceptions handling. It will return a `Xor.Left` if the knp output could not be parsed.

### Normal mode

This will launch an instance of juman and knp for each parsing.
Example:
```scala
import com.enjapan.knp.KNPCli

val knp = new KNPCli()
val blist = knp("京都大学に行った。")

blist.foreach(_.root.traverse(println))
```

Equivalent to:
```shell
echo '京都大学に行った。' | juman | knp -tab
```


### Client mode

To use this you need to have instances of both juman and knp servers running in the background.
```scala
import com.enjapan.knp.KNP

KNP.withClient() { knp =>
  (1 to 1000).foreach { _ =>
    val blist = knp.parse("京都大学に行った。")
    blist.foreach(_.root.traverse(println))
  }
}
```

### Server mode

This will launch both server instances of juman and knp for the time of the evaluation of the bloc.
Do not use background threads or futures in this scope or the server will shutdown before the end of the evaluation of the bloc.

```scala
import com.enjapan.knp.KNP

KNP.withServerBackedClient { knp =>
  (1 to 1000).foreach { _ =>
    val blist = knp.parse("京都大学に行った。")
    blist.foreach(_.root.traverse(println))
  }
}
```