package net.wenzuo.themedevtools.jf.directive;

import cn.hutool.core.date.DateUtil;
import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.TemplateException;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.ParseException;
import com.jfinal.template.stat.Scope;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * #date 日期格式化输出指令
 *
 * 三种用法：
 * 1：#date(createAt) 用默认 datePattern 配置，输出 createAt 变量中的日期值
 * 2：#date(createAt, "yyyy-MM-dd HH:mm:ss") 用第二个参数指定的 datePattern，输出 createAt 变量中的日期值
 * 3：#date() 用默认 datePattern 配置，输出 “当前” 日期值
 *
 * 注意：
 * 1：#date 指令中的参数可以是变量，例如：#date(d, p) 中的 d 与 p 可以全都是变量
 * 2：默认 datePattern 可通过 Engine.setDatePattern(...) 进行配置
 */
public class DateDirective extends Directive {

	private Expr valueExpr;
	private Expr datePatternExpr;
	private int paraNum;

	public void setExprList(ExprList exprList) {
		this.paraNum = exprList.length();
		if (paraNum > 2) {
			throw new ParseException("Wrong number parameter of #date directive, two parameters allowed at most", location);
		}

		if (paraNum == 0) {
			this.valueExpr = null;
			this.datePatternExpr = null;
		} else if (paraNum == 1) {
			this.valueExpr = exprList.getExpr(0);
			this.datePatternExpr = null;
		} else if (paraNum == 2) {
			this.valueExpr = exprList.getExpr(0);
			this.datePatternExpr = exprList.getExpr(1);
		}
	}

	public void exec(Env env, Scope scope, Writer writer) {
		if (paraNum == 1) {
			outputWithoutDatePattern(env, scope, writer);
		} else if (paraNum == 2) {
			outputWithDatePattern(env, scope, writer);
		} else {
			outputToday(env, writer);
		}
	}

	private void outputToday(Env env, Writer writer) {
		write(writer, new Date(), env.getEngineConfig().getDatePattern());
	}

	private void outputWithoutDatePattern(Env env, Scope scope, Writer writer) {
		Object value = valueExpr.eval(scope);
		if (value instanceof String){
			SimpleDateFormat sdf=new SimpleDateFormat(env.getEngineConfig().getDatePattern());
			try {
				value=sdf.parse((String) value);
			} catch (java.text.ParseException ignored) {
			}
		}
		if (value instanceof Date) {
			write(writer, (Date)value, env.getEngineConfig().getDatePattern());
		} else if (value != null) {
			throw new TemplateException("The first parameter date of #date directive must be Date type", location);
		}
	}

	private void outputWithDatePattern(Env env, Scope scope, Writer writer) {
		Object value = valueExpr.eval(scope);
		if (value instanceof String){
			SimpleDateFormat sdf=new SimpleDateFormat(env.getEngineConfig().getDatePattern());
			try {
				value=sdf.parse((String) value);
			} catch (java.text.ParseException ignored) {
			}
		}
		if (value instanceof Date) {
			Object datePattern = this.datePatternExpr.eval(scope);
			if (datePattern instanceof String) {
				write(writer, (Date)value, (String)datePattern);
			} else {
				throw new TemplateException("The sencond parameter datePattern of #date directive must be String", location);
			}
		} else if (value != null) {
			throw new TemplateException("The first parameter date of #date directive must be Date type", location);
		}
	}

	private void write(Writer writer, Date date, String datePattern) {
		try {
			writer.write(date, datePattern);
		} catch (IOException e) {
			throw new TemplateException(e.getMessage(), location, e);
		}
	}
}
