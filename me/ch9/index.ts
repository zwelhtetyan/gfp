import { of } from "rxjs";

const s = of([1, 2, 3]);

s.subscribe({
  next: (v) => console.log(v),
  complete: () => console.log("complete"),
});
