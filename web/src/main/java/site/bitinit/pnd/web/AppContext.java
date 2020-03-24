package site.bitinit.pnd.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import site.bitinit.pnd.web.dao.AppConfMapper;
import site.bitinit.pnd.web.entity.AppConf;
import site.bitinit.pnd.web.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppContext {

	@Autowired
	private AppConfMapper appConfMapper;

	private Map<String, String> confs = new HashMap<>();

	@EventListener(ApplicationReadyEvent.class)
	public void initialAfterApplicationReady() {
		loadConfs();
	}

	public void loadConfs() {
		List<AppConf> appConfs = appConfMapper.list();
		appConfs.forEach(wechatAppConf -> confs.put(wechatAppConf.getConfKey(), wechatAppConf.getConfValue()));
	}

	public String getConf(String key) {
		return getConf(key, null);
	}

	public String getConf(String key, String defaultValue) {
		String value = confs.get(key);
		if (!StringUtils.isBlank(value)) {
			return value;
		}
		if (!StringUtils.isBlank(defaultValue)) {
			return defaultValue;
		}

		throw new RuntimeException("not exist config:" + key);
	}
}
