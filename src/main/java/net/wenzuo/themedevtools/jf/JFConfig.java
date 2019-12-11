package net.wenzuo.themedevtools.jf;

import com.jfinal.config.*;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.template.Engine;
import net.wenzuo.themedevtools.App;
import net.wenzuo.themedevtools.Config;
import net.wenzuo.themedevtools.jf.directive.*;
import net.wenzuo.themedevtools.watcher.ThemeWatcher;

import java.io.File;

/**
 * @author Catch
 * @date 2019-10-11 16:02
 */
public class JFConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {
		PropKit.append(App.prop);
		me.setJsonFactory(MixedJsonFactory.me());
		me.setInjectDependency(true);
		me.setJsonDatePattern("yyyy-MM-dd HH:mm:ss");
		Engine.setFastMode(true);
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/site", SiteController.class, "");
	}

	@Override
	public void configEngine(Engine me) {
		me.setBaseTemplatePath("theme");
		me.setDevMode(true);
		me.setDatePattern("yyyy-MM-dd HH:mm:ss");
		me.addDirective("hmr", HmrDirective.class, true);
		me.addDirective("article", ArticleDirective.class, true);
		me.addDirective("articles", ArticlesDirective.class, true);
		me.addDirective("categories", CategoriesDirective.class, true);
		me.addDirective("tags", TagsDirective.class, true);
		me.addDirective("menus", MenusDirective.class, true);
	}

	@Override
	public void configPlugin(Plugins me) {

	}

	@Override
	public void configInterceptor(Interceptors me) {

	}

	@Override
	public void configHandler(Handlers me) {
		me.add(new GlobalHandler());
	}

	@Override
	public void onStart(){
		Config.init();
		ThemeWatcher.watch();
	}
}
