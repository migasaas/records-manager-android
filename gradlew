#!/usr/bin/env sh
DEFAULT_JVM_OPTS="-Xmx1g -Dfile.encoding=UTF-8"
APP_HOME=$(dirname "$0")
case "`uname`" in
    CYGWIN*|MINGW*|MSYS*)
        if [ -n "$JAVA_HOME" ] ; then
            JAVACMD="$JAVA_HOME/bin/java"
        else
            JAVACMD="java"
        fi
        if [ ! -x "$JAVACMD" ] ; then
            echo "Error: JAVA_HOME is not defined correctly."
            echo "  We cannot execute $JAVACMD"
            exit 1
        fi
        exec "$JAVACMD" $DEFAULT_JVM_OPTS -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
        ;;
    *)
        # Assume Linux or macOS
        if [ -n "$JAVA_HOME" ] ; then
            JAVACMD="$JAVA_HOME/bin/java"
        else
            JAVACMD="java"
        fi
        if [ ! -x "$JAVACMD" ] ; then
            echo "Error: JAVA_HOME is not defined correctly."
            echo "  We cannot execute $JAVACMD"
            exit 1
        fi
        exec "$JAVACMD" $DEFAULT_JVM_OPTS -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
        ;;
esac