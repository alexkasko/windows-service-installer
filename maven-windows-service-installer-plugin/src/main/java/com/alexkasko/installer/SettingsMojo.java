package com.alexkasko.installer;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

import static ru.concerteza.util.io.CtzResourceUtils.readResourceToString;

/**
 * Supertype for plugin, encapsulates settings
 *
 * @author alexkasko
 * Date: 5/3/12
 */
public abstract class SettingsMojo extends AbstractMojo {
    // Application parameters

    /**
     * Application directories to be included into installer
     *
     * @parameter property="${installer.appDataDirs}"
     * @required
     */
    protected List<String> appDataDirs;

    // JRE parameters

    /**
     * Path to windows JRE
     *
     * @parameter property="${installer.jreDir}" default-value="${java.home}"
     */
    protected File jreDir;
    /**
     * Whether JRE is 64 bit
     *
     * @parameter property="${installer.use64BitJre}" default-value="false"
     */
    protected boolean use64BitJre;
    /**
     * Use x86 launcher binaries for x64 installers instead of default x64 ones
     *
     * @parameter property="${installer.useX86LaunchersForX64Installer}" default-value="false"
     *
     */
    protected boolean useX86LaunchersForX64Installer;
    /**
     * Resource path to x86 installer launcher
     *
     * @parameter property="${installer.installLauncher32Path}" default-value="classpath:/launchers/x86/install.exe"
     */
    protected String installLauncher32Path;
    /**
     * Resource path to x86 uninstaller launcher
     *
     * @parameter property="${installer.uninstallLauncher32Path}" default-value="classpath:/launchers/x86/uninstall.exe"
     */
    protected String uninstallLauncher32Path;
    /**
     * Resource path to x64 installer launcher
     *
     * @parameter property="${installer.uninstallLauncher32Path}" default-value="classpath:/launchers/x64/install.exe"
     */
    protected String installLauncher64Path;
    /**
     * Resource path to x64 uninstaller launcher
     *
     * @parameter property="${installer.uninstallLauncher32Path}" default-value="classpath:/launchers/x64/uninstall.exe"
     */
    protected String uninstallLauncher64Path;

    // IzPack parameters

    /**
     * Application name displayed in installer
     *
     * @parameter property="${installer.izpackAppName}" default-value="${project.name}"
     */
    protected String izpackAppName;
    /**
     * Application version displayed in installer
     *
     * @parameter property="${installer.izpackAppVersion}" default-value="${project.version}"
     */
    protected String izpackAppVersion;
    /**
     * Installer language, see http://izpack.org/documentation/installation-files.html#the-localization-element-locale
     *
     * @parameter property="${installer.izpackLang}" default-value="xxx"
     */
    protected String izpackLang;
    /**
     * Installer compress option, values are 'raw' (default), 'deflate' and 'bzip2'
     *
     * @parameter property="${installer.izpackCompress}" default-value="raw"
     */
    protected String izpackCompress;
    /**
     * Default installation directory
     *
     * @parameter property="${installer.izpackDefaultInstallDir}" default-value="$USER_HOME\\${project.name}"
     */
    protected String izpackDefaultInstallDir;
    /**
     * Application Files pack name
     *
     * @parameter property="${installer.izpackAppFilesPackName}" default-value="Application Files"
     */
    protected String izpackAppFilesPackName;
    /**
     * Application Files pack description
     *
     * @parameter property="${installer.izpackAppFilesPackDescription}" default-value="Necessary application files"
     */
    protected String izpackAppFilesPackDescription;
    /**
     * JRE pack name
     *
     * @parameter property="${installer.izpackJREPackName}" default-value="Java Runtime Environment"
     */
    protected String izpackJREPackName;
    /**
     * JRE pack description
     *
     * @parameter property="${installer.izpackJREPackName}" default-value="Java Runtime Environment"
     */
    protected String izpackJREPackDescription;
    /**
     * Windows Service pack name
     *
     * @parameter property="${installer.izpackWindowsServicePackName}" default-value="Windows Service Installer"
     */
    protected String izpackWindowsServicePackName;
    /**
     * Windows Service pack description
     *
     * @parameter property="${installer.izpackWindowsServicePackDescription}"
     * default-value="Application will be installed as Windows Service using prunsrv tool from Apache Commons Daemon project, see http://commons.apache.org/daemon"
     */
    protected String izpackWindowsServicePackDescription;
    /**
     * Icon to be used on top of installer frame
     *
     * @parameter property="${installer.izpackFrameIconPath}" default-value="classpath:/izpack/install.png"
     */
    protected String izpackFrameIconPath;
    /**
     * Icon to be used on Hello screen
     *
     * @parameter property="${installer.izpackHelloIconPath}" default-value="classpath:/izpack/install.png"
     */
    protected String izpackHelloIconPath;
    /**
     * Definition of additional installer packs
     *
     * @parameter property="${installer.izpackAdditionalPacksPath}" default-value="classpath:/izpack/addpacks.xml"
     */
    protected String izpackAdditionalPacksPath;
    /**
     * Definition of additional resources
     *
     * @parameter property="${installer.izpackAdditionalResourcePaths}"
     */
    protected List<String> izpackAdditionalResourcePaths;

    // Prunsrv parameters

    /**
     * For non-ASCII service display name and description non-ASCII characters must be encoded in
     * .bat files in appropriate MS-DOS CpXXX encoding
     *
     * @parameter property="${installer.prunsrvScriptsEncoding}" default-value="UTF-8"
     */
    protected String prunsrvScriptsEncoding;
    /**
     * Name of the launcher JAR file
     *
     * @parameter property="${installer.prunsrvLauncherJarFile}" default-value="${project.artifactId}.jar"
     */
    protected String prunsrvLauncherJarFile;
    /**
     * Name of the launcher JAR file
     *
     * @parameter property="${installer.prunsrvStartupMode}" default-value="auto"
     */
    protected String prunsrvStartupMode;
    /**
     * Windows service name
     *
     * @parameter property="${installer.prunsrvServiceName}" default-value="${project.artifactId}"
     */
    protected String prunsrvServiceName;
    /**
     * Start class that will be used with prunsrv
     *
     * @parameter property="${installer.prunsrvStartClass}" default-value="com.alexkasko.installer.StandardLauncher"
     */
    protected String prunsrvStartClass;
    /**
     * Application class implementing com.alexkasko.installer.DaemonLauncher.
     * Will be called by StandardLauncher (prunsrvStartClass) by default
     *
     * @parameter property="${installer.prunsrvDaemonLauncherClass}"
     * @required
     */
    protected String prunsrvDaemonLauncherClass;
    /**
     * Input params for prunsrvStartMethod
     * Default value 'start;${installer.prunsrvDaemonLauncherClass}' will be substituted in getter
     *
     * @parameter property="${installer.prunsrvStartParams}"
     */
    protected String prunsrvStartParams;
    /**
     * Stop class that will be used with prunsrv
     * Default value '${installer.prunsrvDaemonLauncherClass}' will be substituted in getter
     *
     * @parameter property="${installer.prunsrvStopClass}" default-value="com.alexkasko.installer.StandardLauncher"
     */
    protected String prunsrvStopClass;
    /**
     * Input params for prunsrvStopParams
     * Default value 'stop;${installer.prunsrvDaemonLauncherClass}' will be substituted in getter
     *
     * @parameter property="${installer.prunsrvStopParams}"
     */
    protected String prunsrvStopParams;
    /**
     * JVM options, delimiter is ';', use separate maven options for XMs, XMx and XSs
     *
     * @parameter property="${installer.prunsrvJvmOptions}"
     */
    protected String prunsrvJvmOptions = "";
    /**
     * JVM XMs option in MB
     *
     * @parameter property="${installer.prunsrvJvmMs}" default-value="64"
     */
    protected int prunsrvJvmMs;
    /**
     * JVM XMx option in MB
     *
     * @parameter property="${installer.prunsrvJvmMx}" default-value="1024"
     */
    protected int prunsrvJvmMx;
    /**
     * JVM XSs option in KB
     *
     * @parameter property="${installer.prunsrvJvmSs}" default-value="512"
     */
    protected int prunsrvJvmSs;
    /**
     * Windows service display name
     *
     * @parameter property="${installer.prunsrvDisplayName}" default-value="${project.name}"
     */
    protected String prunsrvDisplayName;
    /**
     * Windows service description
     *
     * @parameter property="${installer.prunsrvDescription}" default-value="${project.name}"
     */
    protected String prunsrvDescription;
    /**
     * Prunsrv service stop timeout in seconds
     *
     * @parameter property="${installer.prunsrvStopTimeout}" default-value="0"
     */
    protected int prunsrvStopTimeout;
    /**
     * Prunsrv log path, relative to APP_ROOT
     *
     * @parameter property="${installer.prunsrvLogPath}" default-value="logs"
     */
    protected String prunsrvLogPath;
    /**
     * Prunsrv log prefix
     *
     * @parameter property="${installer.prunsrvLogPrefix}" default-value="daemon"
     */
    protected String prunsrvLogPrefix;
    /**
     * Prunsrv log level
     *
     * @parameter property="${installer.prunsrvLogLevel}" default-value="INFO"
     */
    protected String prunsrvLogLevel;
      /**
     * Std out file, relative to APP_ROOT
     *
     * @parameter property="${installer.prunsrvStdOutput}" default-value="logs\\std_out.txt"
     */
    protected String prunsrvStdOutput;
    /**
     * Std out file, relative to APP_ROOT
     *
     * @parameter property="${installer.prunsrvStdError}" default-value="logs\\std_err.txt"
     */
    protected String prunsrvStdError;
    /**
     * Whether to start service immediately after installation
     *
     * @parameter property="${installer.prunsrvStartOnInstrall}" default-value="true"
     */
    protected boolean prunsrvStartOnInstrall;

    // Build parameters

    /**
     * Base directory of compilation process
     *
     * @parameter property="${installer.baseDir}" default-value="${project.build.directory}/izpack"
     */
    protected File izpackDir;
    /**
     * Directory of distribution before for packaging
     *
     * @parameter property="${installer.distDir}" default-value="${project.build.directory}/izpack/dist"
     */
    protected File distDir;
    /**
     * IzPack config file
     *
     * @parameter property="${installer.installConfigFile}"
     */
    protected File installConfigFile;
    /**
     * Base directory of compilation process
     *
     * @parameter property="${installer.buildOutputFile}" default-value="${project.build.directory}/izpack/build.log"
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
     * @parameter property="${installer.installerOutputFile}" default-value="${project.build.directory}/${project.artifactId}-${project.version}-installer.zip"
     */
    protected File installerOutputFile;
    /**
     * Whether to build unix tar.gz distribution
     *
     * @parameter property="${installer.buildUnixDist}" default-value="false"
     */
    protected boolean buildUnixDist;
    /**
     * Output file (distibution)
     *
     * @parameter property="${installer.distOutputFile}" default-value="${project.build.directory}/${project.artifactId}-dist.tgz"
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

    public boolean isUseX86LaunchersForX64Installer() {
        return useX86LaunchersForX64Installer;
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

    public String getIzpackFrameIcon() {
        return FilenameUtils.getName(izpackFrameIconPath);
    }

    public String getIzpackHelloIcon() {
        return FilenameUtils.getName(izpackHelloIconPath);
    }

    public String getIzpackAdditionalPacks() {
        return readResourceToString(izpackAdditionalPacksPath);
    }
}
