import * as AP from "fp-ts/lib/Apply";
import * as A from "fp-ts/lib/Array";
import { pipe } from "fp-ts/lib/function";
import type { TaskEither } from "fp-ts/TaskEither";
import * as TE from "fp-ts/TaskEither";
import * as R from "ramda";
import * as pattern from "ts-pattern";
import type { Option } from "fp-ts/lib/Option";
import * as O from "fp-ts/lib/Option";
import { normalizeResponse } from "./utils/normalizeResponse";
import { type TravelGuide } from "./types";
import type { DataAccess } from "./types/dataAccess.type";
import { dataAccess } from "./api/dataAccess";

// Business Logic
// TODO: Try parallel
const travelGuide = (
  dataAccess: DataAccess,
  name: string
): TaskEither<string, Option<TravelGuide>> => {
  return pipe(
    dataAccess.findAttractions(name, "ByLocationPopulation", 1),
    TE.chain((attractions) =>
      pipe(
        attractions.map((attraction) =>
          pipe(
            AP.sequenceT(TE.ApplyPar)(
              dataAccess.findArtistsFromLocation(attraction.location.id, 2),
              dataAccess.findMoviesAboutLocation(attraction.location.id, 2)
            ),
            TE.map(([artists, movies]) => ({
              attraction,
              subjects: [...artists, ...movies],
            }))
          )
        ),
        TE.sequenceArray,
        TE.map((guides) => pipe(guides.toSorted(sortDecBy(guideScore)), A.head))
      )
    )
  );
};

const getDescFromGuide = R.path<Option<string>>(["attraction", "description"]);
const getSubsFromGuide = R.path(["subjects"]);
const sortDecBy = R.curry((fn) => R.comparator((a, b) => fn(a) > fn(b)));

const guideScore = (guide: TravelGuide) => {
  const description = getDescFromGuide(guide);
  const subjects = getSubsFromGuide(guide) as TravelGuide["subjects"];
  const subjectsSize = R.length(subjects);

  const { followers: totalFollowers, boxOffice: totalBoxOffice } =
    subjects.reduce(
      (acc: { followers: number; boxOffice: number }, subject) =>
        pattern
          .match(subject)
          .with({ type: "Artist" }, ({ followers }) => ({
            ...acc,
            followers: acc.followers + followers,
          }))
          .with({ type: "Movie" }, ({ boxOffice }) => ({
            ...acc,
            boxOffice: acc.boxOffice + boxOffice,
          }))
          .exhaustive(),
      { followers: 0, boxOffice: 0 }
    );

  const descScore = description && O.isSome(description) ? 40 : 0;
  const quantityScore = Math.min(40, subjectsSize * 10);
  const followersScore = Math.min(15, totalFollowers / 100000);
  const boxOfficeScore = Math.min(15, totalBoxOffice / 10_000_000);

  return R.sum([descScore, quantityScore, followersScore, boxOfficeScore]);
};

// Main app
const findTravelGuide = travelGuide(dataAccess, "Eiffel Tower");
const program = pipe(
  findTravelGuide,
  TE.fold(
    (error) => async () => console.error("Error:", error),
    (optionGuide) => async () =>
      pipe(
        optionGuide,
        O.fold(
          () => console.log("No guide found"),
          (guide) => console.log("Guide:", normalizeResponse(guide))
        )
      )
  )
);

// console.log(program());
