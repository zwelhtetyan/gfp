import * as TE from "fp-ts/lib/TaskEither";
import * as R from "ramda";

const domain = "https://query.wikidata.org";
const path = "/sparql";
const endpoint = R.concat(domain, path);
const prefixes = `
PREFIX wd: <http://www.wikidata.org/entity/>
PREFIX wdt: <http://www.wikidata.org/prop/direct/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX schema: <http://schema.org/>
`;

const createURL = R.concat(endpoint);

export const executeQuery = async <T>(query: string): Promise<T> => {
  const _query = R.concat(prefixes, query);
  const searchParam = new URLSearchParams({
    query: _query,
    format: "json",
  }).toString();

  const url = createURL(`?${searchParam}`);

  const headers = {
    Accept: "application/sparql-results+json",
    "User-Agent": "ch11/1.0",
  };

  return (await fetch(url, { headers })).json() as T;
};

export const createFetcherTE =
  <E extends string, A extends unknown>(query: string) =>
  (error: E): TE.TaskEither<E, A> =>
    TE.tryCatch(
      () => executeQuery<A>(query),
      () => error
    );
