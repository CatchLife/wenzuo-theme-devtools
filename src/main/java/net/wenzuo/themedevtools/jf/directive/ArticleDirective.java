package net.wenzuo.themedevtools.jf.directive;

import com.jfinal.kit.Kv;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import net.wenzuo.themedevtools.App;

import java.util.Map;

/**
 * @author Catch
 * @date 2019-08-28 下午3:07
 */
public class ArticleDirective extends BaseDirective {

	@Override
	public void exec(Env env, Scope scope, Writer writer) {
		int len = exprList.length();
		if (len == 0) {
			error(writer, "#article(id|flag) 需要指定一个参数");
			return;
		} else if (len == 1) {
			String para1 = (String) exprList.getExpr(0).eval(scope);
			Map article = null;
			article = App.get("/article", Kv.by("para", para1), "article", Map.class);
			if (article == null) {
				error(writer, "该文章不存在");
				return;
			}
			if (!App.isOwner && ((int) article.get("status") == 2)) {
				error(writer, "您没有该文章的访问权限");
				return;
			}
			scope.setLocal("article", article);
		} else {
			error(writer, "#article(id|flag) 只能有一个参数");
			return;
		}
		stat.exec(env, scope, writer);
		scope.removeLocal("article");
	}

}
