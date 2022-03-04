import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.ThreadLocalRandom;

// implementation of MCSLock taken from textbook
class MCSLock implements Lock
{
  AtomicReference<QNode> tail;
  ThreadLocal<QNode> myNode;
  
  public MCSLock()
  {
    tail = new AtomicReference<QNode>(null);
    myNode = new ThreadLocal<QNode>()
    {
      protected QNode initialValue()
      {
        return new QNode();
      }
    };
  }
  
  public void lock()
  {
    QNode qnode = myNode.get();
    QNode pred = tail.getAndSet(qnode);
    if (pred != null)
    {
      qnode.locked = true;
      pred.next = qnode;
      while (qnode.locked) {}
    }
  }
  
  public void unlock()
  {
    QNode qnode = myNode.get();
    if (qnode.next == null)
    {
      if (tail.compareAndSet(qnode, null))
        return;
      while (qnode.next == null) {}
    }
    qnode.next.locked = false;
    qnode.next = null;
  }
  // implementation of MCSLock taken from textbook
  
  // overrides needed to implement Lock interface
  // errors are thrown until these are put in
  @Override
  public boolean tryLock()
  {
    lock();
    return true;
  }
  
  @Override
  public boolean tryLock(long arg0, TimeUnit arg1) throws InterruptedException
  {
    lock();
    return true;
  }
  
  @Override
  public void lockInterruptibly() throws InterruptedException
  {
    lock();
  }
  
  @Override
  public Condition newCondition()
  {
    return null;
  }
  
  class QNode
  {
    volatile boolean locked = false;
    volatile QNode next = null;
  }
  
}

class Guest implements Runnable
{
  static Lock lock;
  private int id;
  
  public Guest(int id)
  {
    this.id = id;
  }
  
  public void run()
  {
    try
    {
      boolean view = true;
      while(view)
      {
        lock.lock();
        System.out.println("Guest " + id + " is viewing the vase....");
        TimeUnit.SECONDS.sleep(1);
        lock.unlock();
        int viewAgain = ThreadLocalRandom.current().nextInt(0, 3);
        if(viewAgain == 1)
          view = true;
        else
          view = false;
      }
      
    }
    catch (Exception e)
    {
      System.out.println("Exception has occured" + e);
    }
    
    return;
  }
  
  public static void main(String args[])
  {
    lock = new MCSLock();
    
    try
    {
      Thread guest1 = new Thread(new Guest(1));
      Thread guest2 = new Thread(new Guest(2));
      Thread guest3 = new Thread(new Guest(3));
      Thread guest4 = new Thread(new Guest(4));
      Thread guest5 = new Thread(new Guest(5));
      Thread guest6 = new Thread(new Guest(6));
      Thread guest7 = new Thread(new Guest(7));
      Thread guest8 = new Thread(new Guest(8));
      
      guest1.start();
      guest2.start();
      guest3.start();
      guest4.start();
      guest5.start();
      guest6.start();
      guest7.start();
      guest8.start();
      
      guest1.join();
      guest2.join();
      guest3.join();
      guest4.join();
      guest5.join();
      guest6.join();
      guest7.join();
      guest8.join();
      
      System.out.println("Guests are done viewing the vase.");
    }
    catch (Exception e)
    {
      System.out.println("Exception has occured" + e);
    }
  }
}