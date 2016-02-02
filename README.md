# scala-juman-knp

Scala wrapper around [JUMAN](http://nlp.ist.i.kyoto-u.ac.jp/index.php?cmd=read&page=JUMAN) and [KNP](http://nlp.ist.i.kyoto-u.ac.jp/EN/?KNP).

## How to install
To be deployed on bintray.
For now:
```
git clone git@github.com:en-japan/scala-juman-knp.git 
sbt publish-local
```

And in `build.sbt`
```
libraryDepencies += "en-japan" %% "scala-juman-knp" % "0.0.1"
```

## How to use

Example:
```scala
import com.enjapan.knp.KNP

val knp = new KNP()
val blist = knp( "京都大学に行った。")

blist.root.traverse(println)
```