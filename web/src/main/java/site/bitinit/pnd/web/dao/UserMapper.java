package site.bitinit.pnd.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import site.bitinit.pnd.web.entity.User;

@Mapper
public interface UserMapper {

	@Select("select * from user where username = #{username}")
	User findByName(@Param("username") String username);
}
