class Data {
  int count = 0;
}

class IncThread extends Thread {
  Data data;

  IncThread(Data data) {
    this.data = data;
  }

  public void run() {
    for (int i = 0; i < 100000; i++) {
      synchronized (data) {
        data.count++;
      }
    }
  }
}

public class MutualExclusion {
  public static void main(String[] args) {
    Data data = new Data();

    IncThread t1 = new IncThread(data);
    IncThread t2 = new IncThread(data);
    IncThread t3 = new IncThread(data);

    t1.start();
    t2.start();
    t3.start();

    try {
      t1.join();
      t2.join();
      t3.join();
      System.out.println("Data " + data.count);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}