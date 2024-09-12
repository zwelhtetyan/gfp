const obj = { name: "zwel", age: 23 };

const fun = (obj: any) => {
  obj.name = "hello";
  return obj;
};

console.log({ obj, newObj: fun(obj) });
