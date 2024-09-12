import { iso, type Newtype } from "newtype-ts";

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
  switch (artist.periodInYear.type) {
    case "stillActive":
      return (
        artist.periodInYear.since <= searchPeriod.end ||
        (artist.periodInYear.periods
          ? periodOverlapsWithPeriods(searchPeriod, artist.periodInYear.periods)
          : false)
      );
    case "yearsInPeriods":
      return periodOverlapsWithPeriods(
        searchPeriod,
        artist.periodInYear.periods
      );
  }
};

const activeLength = (artist: Artist, currentYear: number): number => {
  let periods: ActiveBetween[] = [];
  switch (artist.periodInYear.type) {
    case "stillActive":
      const prevPeriods = artist.periodInYear.periods || [];
      periods = [
        ...prevPeriods,
        { start: artist.periodInYear.since, end: currentYear },
      ];
      break;
    case "yearsInPeriods":
      periods = [...artist.periodInYear.periods];
      break;
  }

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
    return [...searchBy].every((searchCriteria) => {
      switch (searchCriteria.type) {
        case "genre":
          return searchByGenre(artist, searchCriteria.genres);
        case "location":
          return searchByLocation(artist, searchCriteria.locations);
        case "activeYear":
          return wasActive(artist, searchCriteria.period);
        case "activeLength":
          return (
            activeLength(artist, searchCriteria.until) >= searchCriteria.howLong
          );
      }
    });
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
