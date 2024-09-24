import itertools

def infinite_numbers():
  x = 0
  while True:
    x = x + 1
    yield x


def odd_numbers(numbers):
  return filter(lambda x: x % 2 != 0, numbers)

infinite_odd_numbers = odd_numbers(infinite_numbers())
limited_result = itertools.islice(infinite_odd_numbers, 3)  

print(list(limited_result))