import * as O from "fp-ts/lib/Option";

export const normalizeResponse = <T>(value: T): any => {
  // Handle arrays
  if (Array.isArray(value)) {
    return value
      .map((item) => normalizeResponse(item))
      .filter((item) => item !== undefined);
  }

  // Handle objects
  if (value && typeof value === "object") {
    // Handle Option type - check if it has _tag property
    if ("_tag" in value) {
      return O.isNone(value as any) ? undefined : (value as any).value;
    }

    // Handle regular objects
    return Object.fromEntries(
      Object.entries(value as Record<string, unknown>)
        .map(([key, val]) => [key, normalizeResponse(val)])
        .filter(([_, val]) => val !== undefined)
    );
  }

  // Return primitive values as is
  return value;
};
