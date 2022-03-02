import java.util.concurrent.atomic.AtomicReference;

public class MCSLock implements Lock
{
  AtomicReference tail;
  
  public void lock()
  {
    QNode qnode = new QNode();
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
    if (qnode.next == null)
    {
      if (tail.compareAndSet(qnode, null))
        return;
      while (qnode.next == null) {}
    }
    qnode.next.locked = false;
  }
  
  class QNode
  {
    boolean locked = false;
    QNode next = null;
  }
}