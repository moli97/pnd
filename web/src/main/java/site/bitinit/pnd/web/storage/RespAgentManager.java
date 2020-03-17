package site.bitinit.pnd.web.storage;

public class RespAgentManager {

	private static ThreadLocal<RespAgent> threadLocal = new ThreadLocal<RespAgent>();

	/**
	 * 当前线程加入request
	 *
	 * @param respAgent
	 */
	public static void set(RespAgent respAgent) {
		if (respAgent != null) {
			threadLocal.set(respAgent);
		}
	}

	/**
	 * 当前线程获取request,在API接口中可以直接调用这个方法获取当前线程的request对象
	 */
	public static RespAgent get() {
		return threadLocal.get();
	}

	/**
	 * 清理request，释放空间
	 */
	public static void removeHttpServletRequest() {
		threadLocal.remove();
	}
}
