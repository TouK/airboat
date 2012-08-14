package codereview

import org.apache.log4j.Logger
import org.apache.maven.scm.log.ScmLogger

class Log4jScmLogger implements ScmLogger {

    Logger log = Logger.getLogger(Log4jScmLogger.class)

    @Override
    boolean isDebugEnabled() {
        return log.isDebugEnabled()
    }

    @Override
    void debug(String content) {
        log.debug(content)
    }

    @Override
    void debug(String content, Throwable error) {
        log.debug(content, error)
    }

    @Override
    void debug(Throwable error) {
        log.debug(null, error)
    }

    @Override
    boolean isInfoEnabled() {
        return log.isInfoEnabled()
    }

    @Override
    void info(String content) {
        log.info(content)
    }

    @Override
    void info(String content, Throwable error) {
        log.info(content, error)
    }

    @Override
    void info(Throwable error) {
        log.info('', error)
    }

    @Override
    boolean isWarnEnabled() {
        return log.isWarnEnabled()
    }

    @Override
    void warn(String content) {
        log.warn(content)
    }

    @Override
    void warn(String content, Throwable error) {
        log.warn(content, error)
    }

    @Override
    void warn(Throwable error) {
        log.warn(null, error)
    }

    @Override
    boolean isErrorEnabled() {
        log.isErrorEnabled()
    }

    @Override
    void error(String content) {
        log.error(content)
    }

    @Override
    void error(String content, Throwable error) {
        log.error(content, error)
    }

    @Override
    void error(Throwable error) {
        log.error(null, error)
    }
}
