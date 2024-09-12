import { iso, type Newtype } from "newtype-ts";

interface Artist {
  name: string;
  genre: Genre;
  origin: Location;
  periodInYear: PeriodInYear;
}
type PeriodInYear =
  | { type: "stillActive"; start: number }
  | { type: "activeBetween"; start: number; end: number };

enum Genre {
  HipHop = "HipHop",
  Pop = "Pop",
  Rock = "Rock",
}

interface Location
  extends Newtype<{ readonly Location: unique symbol }, string> {}
const isoLocation = iso<Location>();

const wasActive = (artist: Artist, startYear: number, endYear: number) => {
  switch (artist.periodInYear.type) {
    case "stillActive":
      return artist.periodInYear.start <= endYear;
    case "activeBetween":
      const { end, start } = artist.periodInYear;
      return start <= endYear && end >= startYear;
  }
};

const activeLength = (artist: Artist, currentYear: number) => {
  switch (artist.periodInYear.type) {
    case "stillActive":
      return currentYear - artist.periodInYear.start;
    case "activeBetween":
      return artist.periodInYear.end - artist.periodInYear.start;
  }
};

type SearchBy =
  | { type: "genre"; genres: Set<Genre> }
  | { type: "location"; locations: Set<Location> }
  | { type: "activeYear"; start: number; end: number };

const searchByGenre = (artist: Artist, genres: Set<Genre>): boolean => {
  return genres.size === 0 || genres.has(artist.genre);
};

const searchByLocation = (
  artist: Artist,
  locations: Set<Location>
): boolean => {
  return locations.size === 0 || locations.has(artist.origin);
};

const searchByActiveYear = (
  artist: Artist,
  startYear: number,
  endYear: number
): boolean => {
  return wasActive(artist, startYear, endYear);
};

const searchArtists = (
  artists: Artist[],
  searchBy: Set<SearchBy>
): Artist[] => {
  return artists.filter((artist) => {
    return [...searchBy].every((searchCriteria) => {
      switch (searchCriteria.type) {
        case "genre":
          return searchByGenre(artist, searchCriteria.genres);
        case "location":
          return searchByLocation(artist, searchCriteria.locations);
        case "activeYear":
          const { start, end } = searchCriteria;
          return searchByActiveYear(artist, start, end);
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
    periodInYear: { type: "stillActive", start: 2012 },
  },
  {
    name: "Bruno Mars",
    genre: Genre.Pop,
    origin: isoLocation.wrap("New York, NY"),
    periodInYear: { type: "activeBetween", start: 2015, end: 2020 },
  },
  {
    name: "The Weeknd",
    genre: Genre.HipHop,
    origin: isoLocation.wrap("San Francisco, CA"),
    periodInYear: { type: "stillActive", start: 2019 },
  },
];

const result = searchArtists(
  artists,
  new Set<SearchBy>([
    { type: "genre", genres: new Set([Genre.HipHop]) },
    {
      type: "location",
      locations: new Set([isoLocation.wrap("Los Angeles, CA")]),
    },
    // { type: "activeYear", start: 2015, end: 2020 },
  ])
);

console.log(result);
