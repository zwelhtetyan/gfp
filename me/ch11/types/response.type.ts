import type { GenerateFromConfig } from "./utils.types";

// Field Types
type BasicField = {
  type: string;
  value: string;
};

type LiteralField = BasicField & {
  "xml:lang": string;
};

type DecimalField = BasicField & {
  datatype: string;
};

// Field type map
export type FieldTypeMap = {
  uri: BasicField;
  literal: LiteralField;
  decimal: DecimalField;
};
export type TConfigMap = Record<string, keyof FieldTypeMap>;
export type GenerateFieldType<T extends keyof FieldTypeMap> = FieldTypeMap[T];

// Helper function for creating config with type inference
const createConfig = <T extends Record<string, keyof FieldTypeMap>>(
  config: T
) => config;

const attractionConfig = createConfig({
  attraction: "uri",
  location: "uri",
  locationLabel: "literal",
  population: "decimal",
});
type AttractionConfig = typeof attractionConfig;

const artistConfig = createConfig({
  artist: "uri",
  artistLabel: "literal",
  followers: "decimal",
});
type ArtistConfig = typeof artistConfig;

const movieConfig = createConfig({
  subject: "uri",
  subjectLabel: "literal",
  boxOffice: "decimal",
});
type MovieConfig = typeof movieConfig;

export type AttractionResponseType = GenerateFromConfig<
  FieldTypeMap,
  AttractionConfig
>;
export type ArtistResponseType = GenerateFromConfig<FieldTypeMap, ArtistConfig>;
export type MovieResponseType = GenerateFromConfig<FieldTypeMap, MovieConfig>;
