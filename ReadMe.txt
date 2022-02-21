# COP4520-Assignment-2B

The solution to this problem of viewing the Minotaur's Crystal Vase is the third strategy.
This problem boils down to dealing with threads contesting with each other for a lock, each solution being a different kind of lock.

The first strategy is using a test-and-set lock, which we studied and determined to be a poor solution.
It will results in the threads all fighting constantly for control of the lock, flooding the communication bus with traffic, and killing performance.

The second strategy is a using a test-and-test-and-set lock, which is better, but still not ideal.
This will result in the threads being pleasent while they wait, but once the lock opens up there is a mad dash to get the lock, and this can result in many threads starving.

The third strategy is using a queue-based lock, and as we studied in class, this is the best way to deal with this problem.
This will force the threads to enter the lock in a specific order, and also to not starve each other in some mad dash.
It keeps the communication bus cleared, and it is easy on the computer as a whole.
