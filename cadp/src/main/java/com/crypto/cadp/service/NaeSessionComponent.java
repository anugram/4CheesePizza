package com.crypto.cadp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ingrian.internal.ilc.IngrianLogService;
import com.ingrian.security.nae.IngrianProvider;
import com.ingrian.security.nae.IngrianProvider.Builder;
import com.ingrian.security.nae.NAESession;

@Component
public class NaeSessionComponent {
	private static Logger LOGGER = LoggerFactory.getLogger(NaeSessionService.class);
	private NAESession session;

	@SuppressWarnings("unused")
	public void createSession() {
		String userName = "admin";
		String password = "ChangeIt01!";

		try {
			IngrianLogService logService = new IngrianLogService() {
				@Override
				public void debug(String arg0) {
					LOGGER.debug(arg0);
				}

				@Override
				public void debug(String arg0, Throwable arg1) {
					LOGGER.debug(arg0, arg1);
				}

				@Override
				public void error(String arg0) {
					LOGGER.error(arg0);
				}

				@Override
				public void error(String arg0, Throwable arg1) {
					LOGGER.error(arg0, arg1);
				}

				@Override
				public void info(String arg0) {
					LOGGER.info(arg0);
				}

				@Override
				public boolean isDebugEnabled() {
					return true;
				}

				@Override
				public boolean isErrorEnabled() {
					return true;
				}

				@Override
				public boolean isInfoEnabled() {
					return true;
				}

				@Override
				public boolean isTraceEnabled() {
					return true;
				}

				@Override
				public boolean isWarnEnabled() {
					return true;
				}

				@Override
				public void trace(String arg0) {
					LOGGER.trace(arg0);
				}

				@Override
				public void warn(String arg0) {
					LOGGER.warn(arg0);
				}

				@Override
				public void warn(String arg0, Throwable arg1) {
					LOGGER.warn(arg0, arg1);
				}
			};
			System.setProperty(
					"com.ingrian.security.nae.CADP_for_JAVA_Properties_Conf_Filename",
					"CADP_for_JAVA.properties");
			
			IngrianProvider builder = new Builder()
					.addLoggerService(logService)
					.addConfigFileInputStream(
							getClass()
							.getClassLoader()
							.getResourceAsStream("CADP_for_JAVA.properties"))
					.build();
			
			session = NAESession.getSession(userName, password.toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public NAESession getSession() {
		return session;
	}
}
