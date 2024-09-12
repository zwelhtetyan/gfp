import * as E from "fp-ts/Either";
import { pipe } from "fp-ts/lib/function";

// List("Breaking Bad (2008-2013)", "The Wire (2002-2008)", "Mad Men (2007-2015)")

interface TvShow {
  title: string;
  start: number;
  end: number;
}

const extractName = (rawShow: string): E.Either<string, string> => {
  const bracketOpen = rawShow.indexOf("(");

  if (bracketOpen > 0) {
    return E.right(rawShow.slice(0, bracketOpen).trim());
  }

  return E.left(`Can't extract name from ${rawShow}`);
};

const yearEither = (str: string): E.Either<string, number> => {
  const maybeYear = parseInt(str, 10);
  return isNaN(maybeYear) ? E.left(`Can't parse ${str}`) : E.right(maybeYear);
};

const extractStartYear = (rawShow: string): E.Either<string, number> => {
  const bracketOpen = rawShow.indexOf("(");
  const dash = rawShow.indexOf("-");

  return pipe(
    rawShow.substring(bracketOpen + 1, dash),
    E.fromPredicate(
      () => bracketOpen != -1 && dash > bracketOpen + 1,
      () => `Can't extract start year from ${rawShow}`
    ),
    E.chain(yearEither)
  );
};

const extractEndYear = (rawShow: string): E.Either<string, number> => {
  const bracketClose = rawShow.indexOf(")");
  const dash = rawShow.indexOf("-");

  return pipe(
    rawShow.substring(dash + 1, bracketClose),
    E.fromPredicate(
      () => dash != -1 && bracketClose > dash + 1,
      () => `Can't extract end year from ${rawShow}`
    ),
    E.chain(yearEither)
  );
};

const extractSingleYear = (rawShow: string): E.Either<string, number> => {
  const bracketOpen = rawShow.indexOf("(");
  const bracketClose = rawShow.indexOf(")");
  const dash = rawShow.indexOf("-");

  return pipe(
    rawShow.substring(bracketOpen + 1, bracketClose),
    E.fromPredicate(
      () => dash == -1 && bracketOpen != -1 && bracketClose > bracketOpen + 1,
      () => `Can't extract single year from ${rawShow}`
    ),
    E.chain(yearEither)
  );
};

// const parseShow = (rawShow: string): E.Either<string, TvShow> => {};

const parseShow = (rawShow: string): E.Either<string, TvShow> => {
  return pipe(
    extractName(rawShow),
    E.chain((title) =>
      pipe(
        extractStartYear(rawShow),
        E.orElse(() => extractSingleYear(rawShow)),
        E.chain((start) =>
          pipe(
            extractEndYear(rawShow),
            E.orElse(() => extractSingleYear(rawShow)),
            E.map((end) => ({
              title,
              start,
              end,
            }))
          )
        )
      )
    )
  );
};

// All or Nothing error handling
const parseShows = (rawShows: string[]): E.Either<string, TvShow[]> => {
  return rawShows.reduce(
    (acc: E.Either<string, TvShow[]>, curr: string) =>
      pipe(
        acc,
        E.chain((shows) =>
          pipe(
            parseShow(curr),
            E.map((show) => [...shows, show])
          )
        )
      ),
    E.right([])
  );
};

// console.log(parseShow("Breaking Bad (2013)"));

const validShows = parseShows([
  "Breaking Bad (2008-2013)",
  "The Wire (2002-2008)",
  "Mad Men (2007-2015)",
]);

if (E.isRight(validShows)) {
  console.log(validShows.right);
}
