package com.alexkasko.installer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

/**
 * Supertype for plugin, encapsulates settings
 *
 * @author alexkasko
 * Date: 5/3/12
 */
public abstract class SettingsMojo extends AbstractMojo {
    // Application parameters

    /**
     * Application directories to be included into installer, relative to ${project.baseDir}/src/main
     *
     * @parameter expression="${installer.appDataDirs}"
     * @required
     */
    protected List<String> appDataDirs;

    // JRE parameters

    /**
     * Path to windows JRE
     *
     * @parameter expression="${installer.jreDir}" default-value="${java.home}"
     */
    protected File jreDir;
    /**
     * Whether JRE is 64 bit
     *
     * @parameter expression="${installer.use64BitJre}" default-value="false"
     */
    protected boolean use64BitJre;

    // IzPack parameters

    /**
     * Application name displayed in installer
     *
     * @parameter expression="${installer.izpackAppName}" default-value="${project.name}"
     */
    protected String izpackAppName;
    /**
     * Application version displayed in installer
     *
     * @parameter expression="${installer.izpackAppVersion}" default-value="${project.version}"
     */
    protected String izpackAppVersion;
    /**
     * Installer language, see http://izpack.org/documentation/installation-files.html#the-localization-element-locale
     *
     * @parameter expression="${installer.izpackLang}" default-value="eng"
     */
    protected String izpackLang;
    /**
     * Installer compress option, values are 'raw' (default), 'deflate' and 'bzip2'
     *
     * @parameter expression="${installer.izpackCompress}" default-value="raw"
     */
    protected String izpackCompress;
    /**
     * Default installation directory
     *
     * @parameter expression="${installer.izpackDefaultInstallDir}" default-value="$USER_HOME\\${project.name}"
     */
    protected String izpackDefaultInstallDir;
    /**
     * Application Files pack name
     *
     * @parameter expression="${installer.izpackAppFilesPackName}" default-value="Application Files"
     */
    protected String izpackAppFilesPackName;
    /**
     * Application Files pack description
     *
     * @parameter expression="${installer.izpackAppFilesPackDescription}" default-value="Necessary application files"
     */
    protected String izpackAppFilesPackDescription;
    /**
     * JRE pack name
     *
     * @parameter expression="${installer.izpackJREPackName}" default-value="Java Runtime Environment"
     */
    protected String izpackJREPackName;
    /**
     * JRE pack description
     *
     * @parameter expression="${installer.izpackJREPackName}" default-value="Java Runtime Environment"
     */
    protected String izpackJREPackDescription;
    /**
     * Windows Service pack name
     *
     * @parameter expression="${installer.izpackWindowsServicePackName}" default-value="Windows Service Installer"
     */
    protected String izpackWindowsServicePackName;
    /**
     * Windows Service pack description
     *
     * @parameter expression="${installer.izpackWindowsServicePackDescription}"
     * default-value="Application will be installed as Windows Service using prunsrv tool from Apache Commons Daemon project, see http://commons.apache.org/daemon"
     */
    protected String izpackWindowsServicePackDescription;

    // Prunsrv parameters

    /**
     * For non-ASCII service display name and description non-ASCII characters must be encoded in
     * .bat files in appropriate MS-DOS CpXXX encoding
     *
     * @parameter expression="${installer.prunsrvScriptsEncoding}" default-value="UTF-8"
     */
    protected String prunsrvScriptsEncoding;
    /**
     * Name of the launcher JAR file
     *
     * @parameter expression="${installer.prunsrvLauncherJarFile}" default-value="${project.artifactId}.jar"
     */
    protected String prunsrvLauncherJarFile;
    /**
     * Name of the launcher JAR file
     *
     * @parameter expression="${installer.prunsrvStartupMode}" default-value="auto"
     */
    protected String prunsrvStartupMode;
    /**
     * Windows service name
     *
     * @parameter expression="${installer.prunsrvServiceName}" default-value="${project.artifactId}"
     */
    protected String prunsrvServiceName;
    /**
     * Start class that will be used with prunsrv
     *
     * @parameter expression="${installer.prunsrvStartClass}" default-value="com.alexkasko.installer.StandardLauncher"
     */
    protected String prunsrvStartClass;
    /**
     * Application class implementing com.alexkasko.installer.DaemonLauncher.
     * Will be called by StandardLauncher (prunsrvStartClass) by default
     *
     * @parameter expression="${installer.prunsrvDaemonLauncherClass}"
     * @required
     */
    protected String prunsrvDaemonLauncherClass;
    /**
     * Input params for prunsrvStartMethod
     * Default value 'start;${installer.prunsrvDaemonLauncherClass}' will be substituted in getter
     *
     * @parameter expression="${installer.prunsrvStartParams}"
     */
    protected String prunsrvStartParams;
    /**
     * Stop class that will be used with prunsrv
     * Default value '${installer.prunsrvDaemonLauncherClass}' will be substituted in getter
     *
     * @parameter expression="${installer.prunsrvStopClass}" default-value="com.alexkasko.installer.StandardLauncher"
     */
    protected String prunsrvStopClass;
    /**
     * Input params for prunsrvStopParams
     * Default value 'stop;${installer.prunsrvDaemonLauncherClass}' will be substituted in getter
     *
     * @parameter expression="${installer.prunsrvStopParams}"
     */
    protected String prunsrvStopParams;
    /**
     * JVM options, delimiter is ';', use separate maven options for XMs, XMx and XSs
     *
     * @parameter expression="${installer.prunsrvJvmOptions}"
     */
    protected String prunsrvJvmOptions = "";
    /**
     * JVM XMs option in MB
     *
     * @parameter expression="${installer.prunsrvJvmMs}" default-value="64"
     */
    protected int prunsrvJvmMs;
    /**
     * JVM XMx option in MB
     *
     * @parameter expression="${installer.prunsrvJvmMx}" default-value="1024"
     */
    protected int prunsrvJvmMx;
    /**
     * JVM XSs option in KB
     *
     * @parameter expression="${installer.prunsrvJvmSs}" default-value="512"
     */
    protected int prunsrvJvmSs;
    /**
     * Windows service display name
     *
     * @parameter expression="${installer.prunsrvDisplayName}" default-value="${project.name}"
     */
    protected String prunsrvDisplayName;
    /**
     * Windows service description
     *
     * @parameter expression="${installer.prunsrvDescription}" default-value="${project.name}"
     */
    protected String prunsrvDescription;
    /**
     * Prunsrv service stop timeout in seconds
     *
     * @parameter expression="${installer.prunsrvStopTimeout}" default-value="0"
     */
    protected int prunsrvStopTimeout;
    /**
     * Prunsrv log path, relative to APP_ROOT
     *
     * @parameter expression="${installer.prunsrvLogPath}" default-value="logs"
     */
    protected String prunsrvLogPath;
    /**
     * Prunsrv log prefix
     *
     * @parameter expression="${installer.prunsrvLogPrefix}" default-value="daemon"
     */
    protected String prunsrvLogPrefix;
    /**
     * Prunsrv log level
     *
     * @parameter expression="${installer.prunsrvLogLevel}" default-value="INFO"
     */
    protected String prunsrvLogLevel;
      /**
     * Std out file, relative to APP_ROOT
     *
     * @parameter expression="${installer.prunsrvStdOutput}" default-value="logs\\std_out.txt"
     */
    protected String prunsrvStdOutput;
    /**
     * Std out file, relative to APP_ROOT
     *
     * @parameter expression="${installer.prunsrvStdError}" default-value="logs\\std_err.txt"
     */
    protected String prunsrvStdError;
    /**
     * Whether to start service immediately after installation
     *
     * @parameter expression="${installer.prunsrvStartOnInstrall}" default-value="true"
     */
    protected boolean prunsrvStartOnInstrall;

    // Build parameters

    /**
     * Base directory of compilation process
     *
     * @parameter expression="${installer.baseDir}" default-value="${project.build.directory}/izpack"
     */
    protected File izpackDir;
    /**
     * Directory of distribution before for packaging
     *
     * @parameter expression="${installer.distDir}" default-value="${project.build.directory}/izpack/dist"
     */
    protected File distDir;
    /**
     * IzPack config file
     *
     * @parameter expression="${installer.installConfigFile}"
     */
    protected File installConfigFile;
    /**
     * Base directory of compilation process
     *
     * @parameter expression="${installer.buildOutputFile}" default-value="${project.build.directory}/izpack/build.log"
     */
    protected File buildOutputFile;
    /**
     * IzPack output file path
     *
     * @parameter default-value="${project.build.directory}/install.jar"
     * @readonly
     */
    protected File izpackOutputFile;
    /**
     * Output file (installer)
     *
     * @parameter expression="${installer.installerOutputFile}" default-value="${project.build.directory}/${project.artifactId}-${project.version}-installer.zip"
     */
    protected File installerOutputFile;
    /**
     * Whether to build unix tar.gz distribution
     *
     * @parameter expression="${installer.buildUnixDist}" default-value="false"
     */
    protected boolean buildUnixDist;
    /**
     * Output file (distibution)
     *
     * @parameter expression="${installer.distOutputFile}" default-value="${project.build.directory}/${project.artifactId}-dist.tgz"
     */
    protected File distOutputFile;
    /**
     * Freemarker work directory
     *
     * @parameter default-value="${project.build.directory}/freemarker"
     * @readonly
     */
    protected File freemarkerDir;
     /**
     * Launcher project base dir
     *
     * @parameter default-value="${project.baseDir}"
     * @readonly
     */
    protected File baserDir;
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    // getters for freemarker

    public String getIzpackAppName() {
        return izpackAppName;
    }

    public String getIzpackAppVersion() {
        return izpackAppVersion;
    }

    public String getIzpackLang() {
        return izpackLang;
    }

    public String getIzpackCompress() {
        return izpackCompress;
    }

    public String getIzpackDefaultInstallDir() {
        return izpackDefaultInstallDir;
    }

    public String getIzpackAppFilesPackName() {
        return izpackAppFilesPackName;
    }

    public String getIzpackAppFilesPackDescription() {
        return izpackAppFilesPackDescription;
    }

    public String getIzpackJREPackName() {
        return izpackJREPackName;
    }

    public String getIzpackJREPackDescription() {
        return izpackJREPackDescription;
    }

    public String getIzpackWindowsServicePackName() {
        return izpackWindowsServicePackName;
    }

    public String getIzpackWindowsServicePackDescription() {
        return izpackWindowsServicePackDescription;
    }

    public String getPrunsrvLauncherJarFile() {
        return prunsrvLauncherJarFile;
    }

    public String getPrunsrvStartupMode() {
        return prunsrvStartupMode;
    }

    public String getPrunsrvServiceName() {
//        http://stackoverflow.com/questions/8519669/replace-non-ascii-character-from-string/8519863#8519863
        return prunsrvServiceName.replaceAll("[^\\x00-\\x7F]", "_");
    }

    public String getPrunsrvStartClass() {
        return prunsrvStartClass;
    }

    public String getPrunsrvDaemonLauncherClass() {
        return prunsrvDaemonLauncherClass;
    }

    public String getPrunsrvStartParams() {
        return null != prunsrvStartParams ? prunsrvStartParams : "start;" + prunsrvDaemonLauncherClass;
    }

    public String getPrunsrvStopClass() {
        return prunsrvStopClass;
    }

    public String getPrunsrvStopParams() {
        return null != prunsrvStopParams ? prunsrvStopParams : "stop;" + prunsrvDaemonLauncherClass;
    }

    public String getPrunsrvJvmOptions() {
        return prunsrvJvmOptions;
    }

    public int getPrunsrvJvmMs() {
        return prunsrvJvmMs;
    }

    public int getPrunsrvJvmMx() {
        return prunsrvJvmMx;
    }

    public int getPrunsrvJvmSs() {
        return prunsrvJvmSs;
    }

    public String getPrunsrvDisplayName() {
        return prunsrvDisplayName.replace("\"","\\\"");
    }

    public String getPrunsrvDescription() {
        return prunsrvDescription.replace("\"","\\\"");
    }

    public int getPrunsrvStopTimeout() {
        return prunsrvStopTimeout;
    }

    public String getPrunsrvLogPath() {
        return prunsrvLogPath;
    }

    public String getPrunsrvLogPrefix() {
        return prunsrvLogPrefix;
    }

    public String getPrunsrvLogLevel() {
        return prunsrvLogLevel;
    }

    public String getPrunsrvStdOutput() {
        return prunsrvStdOutput;
    }

    public String getPrunsrvStdError() {
        return prunsrvStdError;
    }

    public boolean isPrunsrvStartOnInstrall() {
        return prunsrvStartOnInstrall;
    }
}
