package lila.system

import model._
import memo._
import scalaz.effects._

final class InternalApi(repo: GameRepo, versionMemo: VersionMemo) {

  def talk(gameId: String, author: String, message: String): IO[Unit] = for {
    g1 ← repo game gameId
    g2 = g1 withEvents List(MessageEvent(author, message))
    _ ← repo.applyDiff(g1, g2)
    _ ← versionMemo put g2
  } yield ()

  def endGame(gameId: String): IO[Unit] = for {
    g1 ← repo game gameId
    g2 = g1 withEvents List(EndEvent())
    _ ← repo.applyDiff(g1, g2)
    _ ← versionMemo put g2
  } yield ()

  def updateVersion(gameId: String): IO[Unit] =
    repo game gameId flatMap versionMemo.put
}
