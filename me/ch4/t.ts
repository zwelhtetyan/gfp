import { curry } from "ramda";

const list = ["ada", "haskell", "scala", "java", "rust"];
const list2 = ["football", "f1", "hockey", "basketball"];

const score = (word: string) => word.replaceAll("a", "").length;

const bonus = (word: string) => (word.includes("c") ? 5 : 0);

const penalty = (word: string) => (word.includes("s") ? 7 : 0);

// 1. Sort the list in descending order based on their scores
// 2. Sort the list in descending order based on their scores + bonus
// 3. Sort the list in descending order based on their scores + bonus + penalty
// 4. Return number of score in descending order
// 5. Return the list of element that score higher than 2

const rankedWords = (scoreFn: (word: string) => number, word: string[]) => {
  return word.sort((a, b) => scoreFn(b) - scoreFn(a));
};

/// current
const highScoringWords = (
  wordScore: (val: string) => number
): ((greaterThan: number) => (word: string[]) => string[]) => {
  return (greaterThan) => (word) =>
    word.filter((w) => wordScore(w) > greaterThan);
};

const wordWithScore = highScoringWords((w) => score(w) + bonus(w) - penalty(w));
const word1WithHigherScoreThan = wordWithScore(1);
const word2WithHigherScoreThan = wordWithScore(2);

// Return the sum of scores of the provided list as input

// The data id need:
// 1: List of score
// 2: score calculation function
// summing function

type WordScore = (s: string) => number;
const cumulativeScoreWord = (wordScore: WordScore, word: string[]) => {
  return word.reduce((prev, curr) => prev + wordScore(curr), 0);
};

const curryCumulativeScore = curry(cumulativeScoreWord)(score);

console.log(curryCumulativeScore(list));
console.log(curryCumulativeScore(list2));

// console.log(word1WithHigherScoreThan(list));
// console.log(word2WithHigherScoreThan(list2));

// another way with currying, thanks to ramda
// const highScoringWords = (
//   scoreFn: (w: string) => number,
//   greaterThan: number,
//   word: string[]
// ) => {
//   return word.filter((w) => scoreFn(w) > greaterThan);
// };

// const curryHighScoringWords = curry(highScoringWords);
// const curryHighScoringWordsWithScore = curryHighScoringWords(score);
// const curryHighScoringWordsWithScoreGreaterThanOne =
//   curryHighScoringWordsWithScore(1);

// console.log(curryHighScoringWordsWithScoreGreaterThanOne(list));
// console.log(curryHighScoringWordsWithScoreGreaterThanOne(list2));

////////////////////////////////////////////////////////////////////////////////
