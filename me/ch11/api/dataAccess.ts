import { pipe } from "fp-ts/lib/function";
import * as TE from "fp-ts/lib/TaskEither";
import { createFetcherTE } from ".";
import { extractData, transform } from "../utils/transform";
import type { DataAccess } from "../types/dataAccess.type";
import type {
  ArtistResponseType,
  AttractionResponseType,
  MovieResponseType,
} from "../types/response.type";

export const dataAccess: DataAccess = {
  findAttractions: (name, ordering, limit) => {
    const orderBy = (() => {
      switch (ordering) {
        case "ByName":
          return "?attractionLabel";
        case "ByLocationPopulation":
          return "DESC(?population)";
        default:
          return "DESC(?population)";
      }
    })();

    const query = `SELECT DISTINCT ?attraction ?attractionLabel ?description ?location ?locationLabel ?population WHERE {
        ?attraction wdt:P31 wd:Q570116;
            rdfs:label ?attractionLabel;
            wdt:P131 ?location.
        FILTER(LANG(?attractionLabel) = "en").
        OPTIONAL {
          ?attraction schema:description ?description.
          FILTER(LANG(?description) = "en").
        }
        ?location wdt:P1082 ?population;
          rdfs:label ?locationLabel;
        FILTER(LANG(?locationLabel) = "en").
        FILTER(CONTAINS(?attractionLabel, "${name}")).
      } ORDER BY ${orderBy} LIMIT ${limit}
    `;

    const fetcher = createFetcherTE(query)(
      `Can't get attractions with the name-${name}`
    );

    return pipe(
      fetcher,
      TE.chainEitherK(extractData<AttractionResponseType[]>),
      TE.map((data) => data.map(transform.toAttraction))
    );

    // return TE.right([
    //   {
    //     name: "Attraction 1",
    //     description: O.some("Attraction 1 description"),
    //     location: {
    //       id: isoLocationId.wrap(1),
    //       name: "Attraction 1 location",
    //       population: 1,
    //     },
    //   },
    //   {
    //     name: "Attraction 2",
    //     description: O.some("Attraction 2 description"),
    //     location: {
    //       id: isoLocationId.wrap(2),
    //       name: "Attraction 2 location",
    //       population: 2,
    //     },
    //   },
    // ]);
  },
  findArtistsFromLocation: (locationId, limit) => {
    const query = `SELECT DISTINCT ?artist ?artistLabel ?followers WHERE {
      ?artist wdt:P136 ?genre;
              wdt:P8687 ?followers;
              rdfs:label ?artistLabel.
      FILTER(LANG(?artistLabel) = "en").
      ?artist wdt:P740 wd:${locationId}
    } ORDER BY DESC(?followers) LIMIT ${limit}
  `;

    const fetcher = createFetcherTE(query)(
      `Can't get artists from the locationId-${locationId}`
    );

    console.log("start fetching artists");

    return pipe(
      fetcher,
      TE.chainEitherK(extractData<ArtistResponseType[]>),
      TE.map((data) => {
        console.log("done fetching artists");

        return data.map(transform.toArtist);
      })
    );

    // return TE.right([
    //   { type: "Artist", name: "Artist 1", followers: 1 },
    //   { type: "Artist", name: "Artist 2", followers: 2 },
    //   { type: "Artist", name: "Artist 3", followers: 3 },
    // ]);
  },
  findMoviesAboutLocation: (locationId, limit) => {
    const query = `SELECT DISTINCT ?subject ?subjectLabel ?boxOffice WHERE {
      ?subject wdt:P31 wd:Q11424;
            wdt:P2142 ?boxOffice;
            rdfs:label ?subjectLabel.
        ?subject wdt:P840 wd:${locationId}
        FILTER(LANG(?subjectLabel) = "en").
      } ORDER BY DESC(?boxOffice) LIMIT ${limit}
    `;

    const fetcher = createFetcherTE(query)(
      `Can't get movies from the locationId-${locationId}`
    );

    console.log("start fetching movies");

    return pipe(
      fetcher,
      TE.chainEitherK(extractData<MovieResponseType[]>),
      TE.map((data) => {
        console.log("done fetching movies");

        return data.map(transform.toMovie);
      })
    );

    // return TE.right([
    //   { type: "Movie", name: "Movie 1", boxOffice: 1 },
    //   { type: "Movie", name: "Movie 2", boxOffice: 2 },
    //   { type: "Movie", name: "Movie 3", boxOffice: 3 },
    // ]);
  },
};
