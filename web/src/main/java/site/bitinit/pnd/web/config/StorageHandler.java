package site.bitinit.pnd.web.config;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StorageHandler {

	private ConcurrentHashMap<String, ImmutablePair<String, Long>> map;

	public String get(String k) {
		check();
		ImmutablePair<String, Long> pair = map.get(k);
		if (pair == null || pair.getRight() < System.currentTimeMillis()) {
			return null;
		}
		return pair.getLeft();
	}

	public void put(String k, String v) {
		put(k, v, 7 * 24 * 60 * 60 * 1000L);
	}

	public void put(String k, String v, Long existTime) {
		check();
		map.put(k, new ImmutablePair<>(v, existTime + System.currentTimeMillis()));
	}

	public void remove(String k) {
		check();
		map.remove(k);
	}

	public void clearInvalid() {
		List<String> keys = Lists.newArrayList(map.keySet());
		for (String key : keys) {
			ImmutablePair<String, Long> pair = map.get(key);
			if (pair == null || pair.getRight() < System.currentTimeMillis()) {
				remove(key);
			}
		}
	}

	private void check() {
		if (map == null) {
			map = new ConcurrentHashMap<>();
		}
	}

}
