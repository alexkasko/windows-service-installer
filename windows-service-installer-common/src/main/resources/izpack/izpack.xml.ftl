[#ftl encoding="UTF-8"/]
<?xml version="1.0" encoding="utf-8" standalone="yes" ?>
<!-- Use this for default installer only, for real customization you need custom panels -->
<installation version="1.0">
    <info>
        <appname>${izpackAppName}</appname>
        <appversion>${izpackAppVersion}</appversion>
        <!--<appsubpath>myapp</appsubpath>-->
        <uninstaller path="$INSTALL_PATH/uninstall" name="uninstall.jar"/>
        <!-- http://izpack.org/documentation/installation-files.html#built-in-conditions -->
        <!--<run-privileged condition="izpack.windowsinstall.vista|izpack.macinstall"/>-->
    </info>
    <guiprefs width="640" height="480" resizable="no">
        <laf name="looks">
            <os family="unix"/>
        </laf>
        <laf name="looks">
            <os family="windows"/>
        </laf>
        <modifier key="useHeadingPanel" value="yes"/>
        <modifier key="useHeadingForSummary" value="yes"/>
        <modifier key="headingLineCount" value="2"/>
        <modifier key="headingFontSize" value="1.5"/>
        <modifier key="headingBackgroundColor" value="0x00ffffff"/>
        <modifier key="headingPanelCounter" value="text"/>
        <modifier key="headingPanelCounterPos" value="inHeading"/>
    </guiprefs>
    <locale>
        <!-- http://izpack.org/documentation/installation-files.html#the-localization-element-locale -->
        <langpack iso3="${izpackLang}"/>
    </locale>

    <panels>
        <panel classname="HelloPanel"/>
        <panel classname="TargetPanel"/>
        <panel classname="PacksPanel"/>
        <panel classname="InstallPanel"/>
        <panel classname="SimpleFinishPanel"/>
    </panels>

    <resources>
        <res id="TargetPanel.dir.windows" src="default-install-dir.txt"/>
    </resources>

    <packs>
        <pack name="${izpackAppFilesPackName}" required="yes" installGroups="appfiles_pack">
            <description>${izpackAppFilesPackDescription}</description>
            <fileset dir="dist" targetdir="$INSTALL_PATH"/>
        </pack>
        <pack name="${izpackJREPackName}" required="yes" installGroups="jre_pack" loose="true">
            <description>${izpackJREPackDescription}</description>
            <fileset dir="jre" targetdir="$INSTALL_PATH/jre"/>
        </pack>
        <pack name="${izpackWindowsServicePackName}" required="no" preselected="yes" installGroups="windows_service_pack">
            <os family="windows"/>
            <description>${izpackWindowsServicePackDescription}</description>
            <executable targetfile="$INSTALL_PATH/bin/install_service.bat"
                        stage="postinstall"
                        keep="true">
            </executable>
            <executable targetfile="$INSTALL_PATH/bin/uninstall_service.bat"
                        stage="uninstall"
                        keep="false">
            </executable>
        </pack>
    </packs>
</installation>