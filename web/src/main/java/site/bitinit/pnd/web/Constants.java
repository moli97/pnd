package site.bitinit.pnd.web;

/**
 * @author john
 * @date 2020-01-05
 */
public interface Constants {

	String API_VERSION = "/v1";

	String ACCESS_TOKEN = "accessToken";

	String PND_HOME = "pnd.homeDir";

	String USE_MYSQL = "pnd.useMysql";
	String MYSQL_URL = "pnd.mysql.url";
	String MYSQL_USERNAME = "pnd.mysql.username";
	String MYSQL_PASSWORD = "pnd.mysql.password";

	String MAX_FILE_UPLOAD_SIZE = "pnd.max.uploadFile.size";
	String MAX_REQUEST_SIZE = "pnd.max.request.size";

	/**
	 * config key
	 */
	String UPLOAD_ROOT_KEY = "upload.root.key";

	String UPLOAD_ROOT_DEFAULT = "/data/default/";
}
