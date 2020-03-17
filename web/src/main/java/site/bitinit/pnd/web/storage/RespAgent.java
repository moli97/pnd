package site.bitinit.pnd.web.storage;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RespAgent {

	private static int count = 0;

	private String accessToken;

	private Map<String, Object> datas = new HashMap<>();

	public RespAgent() {
		log.error("创建次数：" + ++count + "创建时间：>>" + System.currentTimeMillis());
	}

	public RespAgent put(String key, Object data) {
		datas.put(key, data);
		return this;
	}

	public RespAgent putAll(Map<String, Object> result) {
		datas.putAll(result);
		return this;
	}

	public RespAgent withAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public <T> T get(String key) {
		return (T) datas.get(key);
	}

	public Object remove(String key) {
		return datas.remove(key);
	}

	public Map<String, Object> getDatas() {
		return datas;
	}

	private static ThreadLocal<RespAgent> threadLocal = new ThreadLocal<>();

	public static void set(RespAgent respAgent) {
		if (respAgent != null) {
			threadLocal.set(respAgent);
		}
	}

	public static RespAgent get() {
		if (threadLocal.get() == null) {
			RespAgent.set(new RespAgent());
		}
		return threadLocal.get();
	}

	public static void remove() {
		threadLocal.remove();
	}
}
