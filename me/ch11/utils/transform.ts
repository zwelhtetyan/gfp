import * as R from "ramda";
import * as O from "fp-ts/Option";
import * as E from "fp-ts/Either";
import {
  isoLocationId,
  type Artist,
  type Attraction,
  type Movie,
} from "../types";
import { pipe } from "fp-ts/lib/function";

type TTransform = {
  toAttraction: (data: unknown) => Attraction;
  toArtist: (data: unknown) => Artist;
  toMovie: (data: unknown) => Movie;
};

// Attraction
const extractValueFrom = (props: string[]) =>
  R.path<string>([...props, "value"]);
const extractAttractionName = extractValueFrom(["attractionLabel"]);
const extractAttractionDesc = extractValueFrom(["description"]);
const extractLocationName = extractValueFrom(["locationLabel"]);
const extractLocationPopulation = extractValueFrom(["population"]);
const extractLocationId = (obj: any) => {
  const extractId = R.compose(R.last, R.split("/"));
  const locationUri = extractValueFrom(["location"])(obj) as string;
  return R.compose(extractId)(locationUri);
};

// Artist
const extractArtistName = extractValueFrom(["artistLabel"]);
const extractArtistFollowers = extractValueFrom(["followers"]);

// Movie
const extractMovieName = extractValueFrom(["subjectLabel"]);
const extractMovieBoxOffice = extractValueFrom(["boxOffice"]);

export const transform: TTransform = {
  toAttraction: (data) => ({
    name: extractAttractionName(data) as string,
    description: O.fromNullable(extractAttractionDesc(data)),
    location: {
      id: isoLocationId.wrap(extractLocationId(data) as string),
      name: extractLocationName(data) as string,
      population: Number(extractLocationPopulation(data)),
    },
  }),
  toArtist: (data) => ({
    type: "Artist",
    name: extractArtistName(data) as string,
    followers: Number(extractArtistFollowers(data)),
  }),
  toMovie: (data) => ({
    type: "Movie",
    name: extractMovieName(data) as string,
    boxOffice: Number(extractMovieBoxOffice(data)),
  }),
};

export const extractData = <T>(data: unknown): E.Either<string, T> =>
  pipe(
    O.fromNullable(R.path(["results", "bindings"])(data)),
    E.fromOption(() => "Invalid response or missing data"),
    E.map((data) => data as T)
  );
