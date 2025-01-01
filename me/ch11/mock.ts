import * as O from "fp-ts/Option";
import { isoLocationId, type TravelGuide } from "./types";

export const mockAttractionsData = {
  attraction: {
    type: "uri",
    value: "http://www.wikidata.org/entity/Q23018405",
  },
  attractionLabel: {
    "xml:lang": "en",
    type: "literal",
    value: "USS LCI(L)-713",
  },
  location: {
    type: "uri",
    value: "http://www.wikidata.org/entity/Q6106",
  },
  locationLabel: {
    "xml:lang": "en",
    type: "literal",
    value: "Portland",
  },
  population: {
    datatype: "http://www.w3.org/2001/XMLSchema#decimal",
    type: "literal",
    value: "652503",
  },
};

export const mockArtistsData = {
  artist: {
    type: "uri",
    value: "http://www.wikidata.org/entity/Q930112",
  },
  artistLabel: {
    "xml:lang": "en",
    type: "literal",
    value: "The Decemberists",
  },
  followers: {
    datatype: "http://www.w3.org/2001/XMLSchema#decimal",
    type: "literal",
    value: "579018",
  },
};

export const mockMoviesData = {
  subject: {
    type: "uri",
    value: "http://www.wikidata.org/entity/Q723679",
  },
  subjectLabel: {
    "xml:lang": "en",
    type: "literal",
    value: "Star Trek Generations",
  },
  boxOffice: {
    datatype: "http://www.w3.org/2001/XMLSchema#decimal",
    type: "literal",
    value: "118071125",
  },
};

export const mockGuide: TravelGuide = {
  attraction: {
    name: "los angela",
    location: {
      id: isoLocationId.wrap("11"),
      name: "US",
      population: 1111111,
    },
    description: O.some("hello"),
  },
  subjects: [
    { type: "Artist", name: "Billie Eilish", followers: 11111111 },
    { type: "Movie", name: "The pursuit of Happyness", boxOffice: 11111111 },
  ],
};
