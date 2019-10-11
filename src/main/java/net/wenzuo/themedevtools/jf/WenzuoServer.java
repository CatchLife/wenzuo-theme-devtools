package net.wenzuo.themedevtools.jf;

import com.jfinal.config.JFinalConfig;
import com.jfinal.server.undertow.UndertowConfig;
import com.jfinal.server.undertow.UndertowKit;
import com.jfinal.server.undertow.UndertowServer;
import io.undertow.Version;

/**
 * @author Catch
 * @date 2019-10-11 17:31
 */
public class WenzuoServer extends UndertowServer {
	protected WenzuoServer(UndertowConfig undertowConfig) {
		super(undertowConfig);
	}

	public static void start(String jfinalConfigClass, int port, boolean devMode) {
		UndertowConfig undertowConfig = new UndertowConfig(jfinalConfigClass);
		undertowConfig.setPort(port);
		new WenzuoServer(undertowConfig).start();
	}

	@Override
	public synchronized void start() {
		if (configConsumer != null) {
			configConsumer.accept(config);
			configConsumer = null;            // 配置在整个生命周期只能调用一次
		}

		loadCommandLineParameter();

		try {
			// System.out.println("Starting Undertow Server on port: " + config.getPort());
			String msg = "Starting Wenzuo Theme Dev Tools..." + " -> http://" + config.getHost() + ":" + config.getPort();
			if (config.isSslEnable()) {
				msg = msg + ", https://" + config.getHost() + ":" + config.getSslConfig().getPort();
			}
			System.out.println(msg);
			long start = System.currentTimeMillis();
			doStart();
			System.out.println("Starting Complete in " + getTimeSpent(start) + " seconds. Welcome (^_^)\n");

			/**
			 * 使用 kill pid 命令或者 ctrl + c 关闭 JVM 时，调用 UndertowServer.stop() 方法，
			 * 以便触发 JFinalConfig.onStop();
			 *
			 * 注意：下方代码严格测试过，只支持 kill pid 不支持 kill -9 pid
			 */
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					WenzuoServer.this.stop();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			stopSilently();

			// 支持在 doStart() 中抛出异常后退出 JVM，例如端口被占用，否则在 linux 控制台 JVM 并不会退出
			System.exit(1);
		}
	}

	@Override
	public synchronized void stop() {
		if (started) {
			started = false;
		} else {
			return;
		}

		System.out.println("\nShutdown Wenzuo Theme Dev Tools...");
		long start = System.currentTimeMillis();
		try {
			if (hotSwapWatcher != null) {
				hotSwapWatcher.exit();
			}

			doStop();

		} catch (Exception e) {
			e.printStackTrace();
			stopSilently();
		} finally {
			System.out.println("Shutdown Complete in " + getTimeSpent(start) + " seconds. See you later (^_^)\n");
		}
	}
}