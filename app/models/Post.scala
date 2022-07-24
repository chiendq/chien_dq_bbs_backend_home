package models

import org.joda.time.DateTime
import scalikejdbc.WrappedResultSet
import skinny.orm.{Alias, SkinnyMapperWithId}

case class PostId(value: Long) extends AnyVal

case class Post(id: PostId,
                title: String,
                content: String,
                thumbnail: String,
                authorName: String,
                createdAt: DateTime,
                updatedAt: DateTime)

object Post extends SkinnyMapperWithId[PostId, Post] {
  override def defaultAlias: Alias[Post] = createAlias("p")

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Post]): Post = Post(
    id = PostId(rs.get(n.id)),
    title = rs.get(n.title),
    content = rs.get(n.content),
    thumbnail = rs.get(n.thumbnail),
    authorName = rs.get(n.authorName),
    createdAt = rs.get(n.createdAt),
    updatedAt = rs.get(n.updatedAt))

  override def idToRawValue(id: PostId): Any = id.value

  override def rawValueToId(rawValue: Any): PostId = PostId(rawValue.toString.toLong)
}