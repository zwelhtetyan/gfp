// 1. Playlist has a name, a kind, and a list of songs.
// 2. There are three kinds of playlists: curated by a user, based on a
// particular artist, and based on a specific set of genres.
// 3. A song has an artist and a name.
// 4. A user has a name.
// 5. An artist has a name.
// 6. There are only three music genres: use your three favorite genres.

import scala.io.Codec.string2codec

object model {
  enum MusicGenre {
    case ROCK 
    case HIP_HOP
    case POP
  }

  enum Kind {
    case CuratedByUser(user: User)
    case BasedOnArtist(artist: Artist)
    case BasedOnGenres(genres: Set[MusicGenre])
  }

  opaque type Artist = String
  object Artist {
    def apply(value: String): Artist = value
    extension(a: Artist) def name: String = a
  }

  opaque type User = String
  object User {
    def apply(value: String): User = value
    extension(a: User) def name: String = a
  }

  case class Song(name: String, artist: Artist)

  case class Playlist(name: String, kind: Kind, songs: List[Song])

  def gatherSongs(playlists: List[Playlist], artist: Artist, genre: MusicGenre): List[Song] =
    playlists.foldLeft(List.empty[Song])((songs, playlist) =>
      val matchingSongs = playlist.kind match {
        case Kind.CuratedByUser(user) => playlist.songs.filter(_.artist == artist)
        case Kind.BasedOnArtist(playlistArtist) => if (playlistArtist == artist) playlist.songs
          else List.empty
        case Kind.BasedOnGenres(genres) => if (genres.contains(genre)) playlist.songs
          else List.empty
      }
      songs.appendedAll(matchingSongs)
  )


  // def gatherSongs(playlists: List[Playlist], artist: Artist, genre: MusicGenre): List[Song] = {
  //   playlists.flatMap(playlist => {
  //     playlist.kind match {
  //       case Kind.CuratedByUser(user) => playlist.songs.filter(_.artist == artist)
  //       case Kind.BasedOnArtist(_artist) => if (_artist == artist) playlist.songs else List.empty
  //       case Kind.BasedOnGenres(_genres) => if (_genres.contains(genre)) playlist.songs else List.empty
  //     }
  //   })
  // }
}
