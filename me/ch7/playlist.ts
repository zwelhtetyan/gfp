import { iso, type Newtype } from "newtype-ts";

enum MusicGenre {
  HipHop,
  Pop,
  Rock,
}

interface User extends Newtype<{ readonly User: unique symbol }, string> {}
const isoUser = iso<User>();

interface Artist extends Newtype<{ readonly Artist: unique symbol }, string> {}
const isoArtist = iso<Artist>();

type PlaylistKind =
  | { type: "basedOnUser"; user: User }
  | { type: "basedOnArtist"; artist: Artist }
  | { type: "basedOnGenre"; genres: Set<MusicGenre> };

interface Song {
  artist: Artist;
  name: string;
}

interface Playlist {
  name: string;
  kind: PlaylistKind;
  songs: Song[];
}

const extractSongBaseOnKind = (
  playlist: Playlist,
  artist: Artist,
  genre: MusicGenre
) => {
  switch (playlist.kind.type) {
    case "basedOnUser":
      return playlist.songs.filter((song) => song.artist === artist);
    case "basedOnArtist":
      return playlist.kind.artist === artist ? playlist.songs : [];
    case "basedOnGenre":
      return playlist.kind.genres.has(genre) ? playlist.songs : [];
  }
};

const gatherSongs = (
  playlists: Playlist[],
  artist: Artist,
  genre: MusicGenre
): Song[] => {
  return playlists.flatMap((playlist) =>
    extractSongBaseOnKind(playlist, artist, genre)
  );
};

// const gatherSongsV2 = (
//   playlists: Playlist[],
//   artist: Artist,
//   genre: MusicGenre
// ): Song[] => {
//   return playlists.reduce(
//     (acc: Song[], playlist) =>
//       acc.concat(extractSongBaseOnKind(playlist, artist, genre)),
//     []
//   );
// };

// create three playlists base on different kinds
const playlists: Playlist[] = [
  {
    name: "HipHop Weekly",
    kind: { type: "basedOnGenre", genres: new Set([MusicGenre.HipHop]) },
    songs: [
      { artist: isoArtist.wrap("Drake"), name: "Views" },
      { artist: isoArtist.wrap("Bruno Mars"), name: "No Woman, No Cry" },
    ],
  },
  {
    name: "Rock Weekly",
    kind: { type: "basedOnArtist", artist: isoArtist.wrap("The Weeknd") },
    songs: [
      { artist: isoArtist.wrap("The Weeknd"), name: "After Hours" },
      { artist: isoArtist.wrap("Post Malone"), name: "Closer" },
    ],
  },
  {
    name: "Pop Weekly",
    kind: {
      type: "basedOnUser",
      user: isoUser.wrap("John Legend"),
    },
    songs: [
      { artist: isoArtist.wrap("Maroon 5"), name: "Sugar" },
      { artist: isoArtist.wrap("Avicii"), name: "Wake Me Up" },
    ],
  },
];

console.log(
  gatherSongs(playlists, isoArtist.wrap("The Weeknd"), MusicGenre.Rock)
);
