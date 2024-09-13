import { List } from "immutable";
import { iso, type Newtype } from "newtype-ts";
import { match } from "ts-pattern";

interface Artist {
  name: string;
  genre: Genre;
  origin: Location;
  periodInYear: PeriodInYear;
}

type ActiveBetween = { start: number; end: number };
type PeriodInYear =
  | { type: "stillActive"; since: number; periods?: ActiveBetween[] }
  | { type: "yearsInPeriods"; periods: ActiveBetween[] };

enum Genre {
  HipHop = "HipHop",
  Pop = "Pop",
  Rock = "Rock",
}

interface Location
  extends Newtype<{ readonly Location: unique symbol }, string> {}
const isoLocation = iso<Location>();

const periodOverlapsWithPeriods = (
  searchPeriod: ActiveBetween,
  periods: ActiveBetween[]
): boolean => {
  return periods.some(
    (p) => p.start <= searchPeriod.end && p.end >= searchPeriod.start
  );
};

const wasActive = (artist: Artist, searchPeriod: ActiveBetween): boolean => {
  return match(artist.periodInYear)
    .with(
      { type: "stillActive" },
      ({ since, periods }) =>
        since <= searchPeriod.end ||
        (periods ? periodOverlapsWithPeriods(searchPeriod, periods) : false)
    )
    .with({ type: "yearsInPeriods" }, ({ periods }) =>
      periodOverlapsWithPeriods(searchPeriod, periods)
    )
    .exhaustive();
};

const activeLength = (artist: Artist, currentYear: number): number => {
  const periods = match(artist.periodInYear)
    .with({ type: "stillActive" }, ({ since, periods }) =>
      List(periods).push({ start: since, end: currentYear }).toArray()
    )
    .with({ type: "yearsInPeriods" }, ({ periods }) => periods)
    .exhaustive();

  return periods.map((p) => p.end - p.start).reduce((a, b) => a + b, 0);
};

type SearchBy =
  | { type: "genre"; genres: Set<Genre> }
  | { type: "location"; locations: Set<Location> }
  | { type: "activeYear"; period: ActiveBetween }
  | { type: "activeLength"; howLong: number; until: number };

const searchByGenre = (artist: Artist, genres: Set<Genre>): boolean => {
  return genres.size === 0 || genres.has(artist.genre);
};

const searchByLocation = (
  artist: Artist,
  locations: Set<Location>
): boolean => {
  return locations.size === 0 || locations.has(artist.origin);
};

const searchArtists = (artists: Artist[], searchBy: SearchBy[]): Artist[] => {
  return artists.filter((artist) => {
    return searchBy.every((searchCriteria) =>
      match(searchCriteria)
        .with({ type: "genre" }, ({ genres }) => searchByGenre(artist, genres))
        .with({ type: "location" }, ({ locations }) =>
          searchByLocation(artist, locations)
        )
        .with({ type: "activeYear" }, ({ period }) => wasActive(artist, period))
        .with(
          { type: "activeLength" },
          ({ howLong, until }) => activeLength(artist, until) >= howLong
        )
        .exhaustive()
    );
  });
};

// create 3 artists with different periods, genres, and locations
const artists: Artist[] = [
  {
    name: "Drake",
    genre: Genre.HipHop,
    origin: isoLocation.wrap("Los Angeles, CA"),
    periodInYear: { type: "stillActive", since: 2012 },
  },
  {
    name: "Bruno Mars",
    genre: Genre.Pop,
    origin: isoLocation.wrap("New York, NY"),
    periodInYear: {
      type: "yearsInPeriods",
      periods: [{ start: 2015, end: 2020 }],
    },
  },
  {
    name: "The Weeknd",
    genre: Genre.HipHop,
    origin: isoLocation.wrap("San Francisco, CA"),
    periodInYear: { type: "stillActive", since: 2019 },
  },
];

const result = searchArtists(artists, [
  { type: "genre", genres: new Set([Genre.HipHop]) },
  {
    type: "location",
    locations: new Set([isoLocation.wrap("Los Angeles, CA")]),
  },
  { type: "activeYear", period: { start: 2015, end: 2020 } },
  { type: "activeLength", howLong: 5, until: 2022 },
]);

console.log(result);
