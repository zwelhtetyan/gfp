import { compose, flatten, map } from "ramda";

function Book(title: string, authors: string[]) {
  if (!new.target) {
    return new Book(title, authors);
  }

  this.title = title;

  this.authors = authors;
}

const book = Book("FP in Scala", ["Chiusano", "Bjarnason"]);

const books: (typeof Book)[] = [
  Book("FP in Scala", ["Chiusano", "Bjarnason"]),
  Book("The Hobbit", ["Tolkien"]),
];

function bookAdaptations(author: string): string[] {
  if (author === "Tolkien") {
    return ["An Unexpected Journey", "The Desolation of Smaug"];
  }

  return [];
}

function authors(books: (typeof Book)[]): string[] {
  return books.flatMap((b) => b.authors);
}

// console.log(authors(books).flatMap(bookAdaptations));

// Our task is to return a feed of movie recommendations based on books
function recommendationFeed(books: (typeof Book)[]): string[] {
  const list = [];

  books.forEach((book) =>
    book.authors.forEach((author) =>
      bookAdaptations(author).forEach((movie) => {
        list.push(
          `You may like ${movie} because you liked ${author}'s ${book.title}`
        );
      })
    )
  );

  return list;
}

function recommendationFeedDeclarative(books: (typeof Book)[]): string[] {
  return books.flatMap((book) =>
    book.authors.flatMap((author) =>
      bookAdaptations(author).flatMap(
        (movie) =>
          `You may like ${movie}, because you liked ${author}'s ${book.title}`
      )
    )
  );
}

// console.log(recommendationFeedDeclarative(books));
