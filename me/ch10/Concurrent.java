import java.util.HashMap;

class Concurrent {
  public static void main(String[] args) throws InterruptedException {
    concurrent();
  }

  static void concurrent() throws InterruptedException {
    // var cityCheckIns = new ConcurrentHashMap<String, Integer>(); // safe-thread
    var cityCheckIns = new HashMap<String, Integer>(); // not safe-thread

    Runnable task = () -> {
      for (int i = 0; i < 1000; i++) {
        var cityName = i % 2 == 0 ? "Cairo" : "Auckland";
        synchronized (cityCheckIns) { // synchronization is needed to make it safe-thread
          cityCheckIns.compute(cityName,
              (city, checkIns) -> checkIns != null ? checkIns + 1 : 1);
        }
      }
    };

    new Thread(task).start();
    new Thread(task).start();

    // main thread is the ranking computation thread (a simulation that shows the
    // problem)
    Thread.sleep(300);
    System.out.println("[no synchronization] Computing ranking based on: " + cityCheckIns);
  }
}
