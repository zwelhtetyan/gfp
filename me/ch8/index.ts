import type { IO } from "fp-ts/IO";

const now: IO<number> = () => new Date().getTime();
