package site.bitinit.pnd.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import site.bitinit.pnd.web.entity.AppConf;

import java.util.List;

@Mapper
public interface AppConfMapper {

	@Select("select * from app_conf")
	List<AppConf> list();
}
