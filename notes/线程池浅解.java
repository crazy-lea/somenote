/**
     * 构建线程池
     * @param corePoolSize  核心线程池大小
     * @param maximumPoolSize   最大线程池大小
     * @param keepAliveTime 线程池中超过corePoolSize数目的空闲线程最大存活时间
     * @param unit  keepAliveTime时间单位
     * @param workQueue 阻塞任务队列
     * @param threadFactory 线程工厂
     * @param handler 当提交任务数超过maxmumPoolSize+workQueue之和时，任务会交给RejectedExecutionHandler来处理，如果不自定义
	 * 源码中给defaultHandler(private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();)，
	 * AbortPolicy()是ThreadPoolExecutor的内部类，实现了RejectedExecutionHandler接口，
	 * 重写的rejectedExecution方法中直接抛出RejectedExecutionException
     */
    public  ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {
    }

    /**
     *
     * @return
     */
    public static ExecutorService newCachedThreadPool() {
        /**
         * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
         * 核心线程池容量为0，最大线程池数量为Integer.MAX_VALUE，线程工作结束后的等待时间为60s
         * SynchronousQueue没有容量，是无缓冲等待队列，是一个不存储元素的阻塞队列，会直接将任务交给消费者，
         * 必须等队列中的添加元素被消费后才能继续添加新的元素。
         * 允许创建的线程数量为 Integer.MAX_VALUE ，可能会创建大量线程，从而导致OOM。
         */
        return new java.util.concurrent.ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());}

    public static ExecutorService newFixedThreadPool(int nThreads) {
        /**
         * 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
         * 核心线程数量和最大线程池数量相等（由传入参数决定），不设置线程存活时间，即当线程池满了，
         * 再有线程即进入LinkedBlockingQueue阻塞队列。
         * LinkedBlockingQueue是一个无界缓存等待队列。
         * 当前执行的线程数量达到corePoolSize的数量时，剩余的元素会在阻塞队列里等待。
         * （所以在使用此阻塞队列时maximumPoolSizes就相当于无效了），每个线程完全独立于其他线程。
         * 生产者和消费者使用独立的锁来控制数据的同步，即在高并发的情况下可以并行操作队列中的数据。
         * 允许请求的队列长度为 Integer.MAX_VALUE,可能堆积大量的请求，从而导致OOM。
         */
            return new java.util.concurrent.ThreadPoolExecutor(nThreads, nThreads,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }

    public static ExecutorService newSingleThreadExecutor() {
        /**
         * newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
         * DelegatedExecutorService类是Executors的一个静态内部类，职责：一个包装类（代理类），用来仅暴露ExecutorService实现类中的ExecutorService的方法
         * FinalizableDelegatedExecutorService继承DelegatedExecutorService类并增加了一个finalize方法，
         * finalize方法会在虚拟机利用垃圾回收清理对象时被调用，换言之，FinalizableDelegatedExecutorService的实例
         * 即使不手动调用shutdown方法关闭现称池，虚拟机也会帮你完成此任务，不过从严谨的角度出发，
         * 我们还是应该手动调用shutdown方法，毕竟Java的finalize不是C++的析构函数，必定会被调用，
         * Java虚拟机不保证finalize一定能被正确调用，因此我们不应该依赖于它。
         * 允许请求的队列长度为 Integer.MAX_VALUE,可能堆积大量的请求，从而导致OOM。
         */
        return new Executors.FinalizableDelegatedExecutorService
                (new java.util.concurrent.ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>()));
    }
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        //直接返回一个ScheduledThreadPoolExecutor
        return new ScheduledThreadPoolExecutor(corePoolSize);
    }

    /**
     * ScheduledThreadPoolExecutor
     *    extends ThreadPoolExecutor
     *    implements ScheduledExecutorService()
     * @param corePoolSize 核心线程池大小
     */
    public ScheduledThreadPoolExecutor(int corePoolSize) {
        //super->ThreadPoolExecutor
        //允许创建的线程数量为 Integer.MAX_VALUE ，可能会创建大量线程，从而导致OOM。
        super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
                new ScheduledThreadPoolExecutor.DelayedWorkQueue());
    }

    //(了解)ScheduledExecutorService extends ExecutorService
	//ScheduledExecutorService(interface)的主要作用就是可以将定时任务与线程池功能结合使用,其内部只有四个方法

    /**
     *  在给定延时之后创建并执行一次性的操作
     * @param command 执行的任务
     * @param delay 延时
     * @param unit delay的单位
     * @return
     */
    public ScheduledFuture<?> schedule(Runnable command,
                                       long delay, TimeUnit unit);

    /**
     * 创建并执行在给定延迟后启用的 ScheduledFuture
     * @param callable 任务
     * @param delay 延时
     * @param unit  delay单位
     * @param <V>
     * @return ScheduledFuture：
     */
    public <V> ScheduledFuture<V> schedule(Callable<V> callable,
                                           long delay, TimeUnit unit);
    /**
     *   创建并执行一个在给定初始延迟后首次启用的定期操作，后续操作具有给定的周期；
     *   也就是将在 initialDelay 后开始执行，然后在 initialDelay+period 后执行，
     *   接着在 initialDelay + 2 * period 后执行，依此类推。
     * @param command 要执行的任务
     * @param initialDelay 首次执行的延迟时间
     * @param period 连续执行的时间间隔
     * @param unit initialDelay和period的单位
     * @return ScheduledFuture
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                  long initialDelay,
                                                  long period,
                                                  TimeUnit unit);

    /**
     * 创建并执行一个在给定初始延迟后首次启用的定期操作，随后，在每一次执行终止和下一次执行开始之间都存在给定的延迟。
     * @param command 要执行的任务
     * @param initialDelay 首次执行的延迟时间
     * @param delay 从一次执行的终止到下一次执行的开始之间的延迟
     * @param unit initialDelay和delay的单位
     * @return ScheduledFuture
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                     long initialDelay,
                                                     long delay,
                                                     TimeUnit unit);
													 
													 
	ExNote:
	线程池的三种队列区别：SynchronousQueue、LinkedBlockingQueue 和ArrayBlockingQueue:
	简单来讲：SynchronousQueue 没有容量，无缓冲等待队列，直接将任务交给消费者 
	LinkedBlockingQueue 无界缓存等待队列，其构造方法 public LinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }
	ArrayBlockingQueue:有界缓存等待队列，可以指定缓存队列的大小
	参考链接：https://blog.csdn.net/qq_26881739/article/details/80983495
	
	执行execute()方法和submit()方法的区别是什么呢？
	(线程池执行的两个方法,注意Runable并非不能通过submit方法来提交，但execute只可以用来执行Runable)
	1)execute() 方法用于提交不需要返回值的任务，所以无法判断任务是否被线程池执行成功与否；
	2)submit()方法用于提交需要返回值的任务。线程池会返回一个future类型的对象，通过这个future对象可以判断任务是否执行成功，
	并且可以通过future的get()方法来获取返回值，get()方法会阻塞当前线程直到任务完成，而使用 get（long timeout，TimeUnit unit）
	方法则会阻塞当前线程一段时间后立即返回，这时候有可能任务没有执行完。
	Ps:ThreadPoolExecutor extends AbstractExecutorService
	AbstractExecutorService implements ExecutorService(内中定义了submit()方法)
	ExecutorService extends Executor(内中只定义了一个未实现execute()方法)
	
		
	BlockingQueue中offer、put、add的一些比较
	（定义线程池时，在重写RejectedExeceptionHandler的rejectedExecution(Runnable r, ThreadPoolExecutor executor)方法时候可以用到，
	例如：executor.getQueue().put(r); 通过这种方式来对超过线程最大容量和阻塞队列之和的线程进行阻塞，使程序不抛出RejectedExecutionException
	）
	put 将指定元素插入此队列中，将等待可用的空间.通俗点说就是>maxSize 时候，阻塞，直到能够有空间插入元素。
	add: 和collection的add一样，没什么可以说的。如果当前没有可用的空间，则抛出 IllegalStateException。 
	offer: 将指定元素插入此队列中（如果立即可行且不会违反容量限制），成功时返回 true，如果当前没有可用的空间，则返回 false，不会抛异常。