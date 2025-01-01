// Requirement as types

import type { Option } from "fp-ts/lib/Option";
import { iso, type Newtype } from "newtype-ts";

export interface LocationId
  extends Newtype<{ readonly LocationId: unique symbol }, string> {}
export const isoLocationId = iso<LocationId>();

export type Location = {
  id: LocationId;
  name: string;
  population: number;
};

export type Attraction = {
  name: string;
  description: Option<string>;
  location: Location;
};

export type Artist = { type: "Artist"; name: string; followers: number };
export type Movie = { type: "Movie"; name: string; boxOffice: number };

export type PopCultureSubject = Artist | Movie;

export type TravelGuide = {
  attraction: Attraction;
  subjects: PopCultureSubject[];
};

export type AttractionOrdering = "ByName" | "ByLocationPopulation";
