import threading

counter = 0
counter_lock = threading.Lock()

def increment_counter():
    global counter
    for _ in range(1000):
        with counter_lock:  # Lock access to the counter
            counter += 1
        print(threading.current_thread().name, counter)

thread1 = threading.Thread(target=increment_counter)
thread2 = threading.Thread(target=increment_counter)
thread3 = threading.Thread(target=increment_counter)

thread1.start()
thread2.start()
thread3.start()

thread1.join()
thread2.join()
thread3.join()

print(f"Final counter value: {counter}")
