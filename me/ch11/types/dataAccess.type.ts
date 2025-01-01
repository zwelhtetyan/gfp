import type { TaskEither } from "fp-ts/lib/TaskEither";
import type {
  Artist,
  Attraction,
  AttractionOrdering,
  LocationId,
  Movie,
} from ".";

type FindAttractionsFunc = (
  name: string,
  ordering: AttractionOrdering,
  limit: number
) => TaskEither<string, Attraction[]>;

type FindArtistsFromLocationFunc = (
  locationId: LocationId,
  limit: number
) => TaskEither<string, Artist[]>;

type FindMoviesAboutLocationFunc = (
  locationId: LocationId,
  limit: number
) => TaskEither<string, Movie[]>;

export type DataAccess = {
  readonly findArtistsFromLocation: FindArtistsFromLocationFunc;
  readonly findMoviesAboutLocation: FindMoviesAboutLocationFunc;
  readonly findAttractions: FindAttractionsFunc;
};
