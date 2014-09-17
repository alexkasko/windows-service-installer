package com.alexkasko.installer;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.izforge.izpack.compiler.CompilerConfig;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.*;
import org.apache.commons.lang.UnhandledException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.core.io.Resource;
import ru.concerteza.util.archive.TarFunction;
import ru.concerteza.util.archive.ZipFunction;
import ru.concerteza.util.freemarker.FreemarkerEngine;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copyLarge;
import static ru.concerteza.util.string.CtzConstants.UTF8;
import static ru.concerteza.util.io.CtzCopyCheckLMUtils.*;
import static ru.concerteza.util.collection.CtzCollectionUtils.fireTransform;
import static ru.concerteza.util.io.CtzIOUtils.listFiles;
import static ru.concerteza.util.io.CtzIOUtils.mkdirs;
import static ru.concerteza.util.io.CtzResourceUtils.*;
import static ru.concerteza.util.io.CtzResourceUtils.RESOURCE_LOADER;
import static ru.concerteza.util.io.CtzResourceUtils.path;

/**
 * Maven plugin, creates izPack installer
 *
 * @author alexkasko
 * Date: 4/19/12
 * @goal installer
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class InstallerMojo extends SettingsMojo {
    private final FreemarkerEngine freemarker = new FreemarkerEngine();
    private final MarkExecutableFunction markExecutableFunction = new MarkExecutableFunction();

    /**
     * Plugin entry point
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Dirs dirs = prepareDirs();
            copyBin(dirs.bin);
            copyIzpack();
            copyUninstall(dirs.uninstall);
            copyPrunsrv(dirs.bin);
            copyAppData();
            markBinExecutable(dirs.bin);
            copyLibs(dirs.lib);
            copyLauncher(dirs.bin);
            File jre = copyJRE();
            copyIzPackResources();
            runIzPackCompiler();
            packInstaller(jre);
            if(buildUnixDist) packDist();
        } catch (Exception e) {
            throw new MojoFailureException("IzPack error", e);
        }
    }

    private Dirs prepareDirs() throws IOException {
        mkdirs(izpackDir);
        mkdirs(distDir);
        File bin = mkdirs(new File(distDir, "bin"));
        File lib = mkdirs(new File(distDir, "lib"));
        File uninstall = mkdirs(new File(distDir, "uninstall"));
        mkdirs(new File(distDir, "temp"));
        mkdirs(new File(distDir, prunsrvLogPath));
        return new Dirs(bin, lib, uninstall);
    }

    private void copyBin(File binDir) throws IOException {
        {
            Resource[] resources = RESOURCE_RESOLVER.getResources("classpath:/bin/*");
            List<Resource> list = asList(resources);
            List<File> copied = Lists.transform(list, new FtlCopyFunction(binDir));
            fireTransform(copied);
        }
        {
            File daemonDir = new File(binDir, "java-daemon");
            Resource[] daemon = RESOURCE_RESOLVER.getResources("classpath:/bin/java-daemon/*");
            List<Resource> daemonList = asList(daemon);
            List<File> daemonCopied = Lists.transform(daemonList, new CopyFunction(daemonDir));
            fireTransform(daemonCopied);
        }
    }

    private void copyIzpack() throws IOException {
        Resource[] resources = RESOURCE_RESOLVER.getResources("classpath:/izpack/*");
        List<Resource> list = asList(resources);
        List<File> copied = Lists.transform(list, new FtlCopyFunction(izpackDir));
        fireTransform(copied);
        writeStringToFile(new File(izpackDir, "default-install-dir.txt"), izpackDefaultInstallDir, UTF8);
    }

    private void copyUninstall(File uninstallDir) {
        if(use64BitJre && !useX86LaunchersForX64Installer) {
            copyResourceToDir(installLauncher64Path, izpackDir);
            copyResourceToDir(uninstallLauncher64Path, uninstallDir);
        } else {
            copyResourceToDir(installLauncher32Path, izpackDir);
            copyResourceToDir(uninstallLauncher32Path, uninstallDir);
        }
    }

    private void copyPrunsrv(File binDir) throws IOException {
        Resource[] resources = RESOURCE_RESOLVER.getResources("classpath:/prunsrv/*");
        List<Resource> list = asList(resources);
        Predicate<Resource> notExe = Predicates.not(new PostfixPredicate(".exe"));
        Iterable<Resource> filtered = Iterables.filter(list, notExe);
        Iterable<File> copied = Iterables.transform(filtered, new FtlCopyFunction(binDir, prunsrvScriptsEncoding));
        fireTransform(copied);
        String prunsrvPath = use64BitJre ? "classpath:/prunsrv/prunsrv_x86_64.exe" : "classpath:/prunsrv/prunsrv_x86_32.exe";
        File prunsrvTarget = new File(binDir, "prunsrv.exe");
        copyResource(prunsrvPath, prunsrvTarget);
    }

    private void copyAppData() throws IOException {
        for(String dir : appDataDirs) {
            File source = new File(dir);
            copyDirectoryToDirectory(source, distDir);
        }
    }

    private void markBinExecutable(File binDir) {
        List<File> files = listFiles(binDir, new SuffixFileFilter(".sh"), false);
        List<Boolean> marked = Lists.transform(files, markExecutableFunction);
        fireTransform(marked);
    }

    @SuppressWarnings("unchecked")
    private void copyLibs(File libDir) throws IOException {
        Set<Artifact> artifacts = project.getArtifacts();
        for(Artifact ar : artifacts) {
            copyFileToDirectory(ar.getFile(), libDir);
        }
    }

    private void copyLauncher(File binDir) throws IOException {
        final File dest = new File(binDir, prunsrvLauncherJarFile);
        final File launcher;
        if(null == project.getArtifact().getFile()) {
            // unbinded build here
            launcher = new File(project.getBasedir(), "target/" + project.getArtifactId() + "-" + project.getVersion() + ".jar");
        } else {
            launcher = project.getArtifact().getFile();
        }
        copyFile(launcher, dest);
    }

    // copy JRE to use loose pack feature
    private File copyJRE() throws IOException {
        File innerJre = new File(izpackDir, "jre");
        copyDirectory(jreDir, innerJre);
        return innerJre;
    }

    private void copyIzPackResources() {
        copyResourceToDir(izpackFrameIconPath, izpackDir);
        copyResourceToDir(izpackHelloIconPath, izpackDir);
        File lpdir = new File(izpackDir, "bin/langpacks/installer");
        File flagdir = new File(izpackDir, "bin/langpacks/flags");
        copyResourceToDir("classpath:/izpack/xxx.xml", lpdir);
        copyResourceToDir("classpath:/izpack/xxx.gif", flagdir);
        if (null != izpackAdditionalResourcePaths) {
            File addres = new File(izpackDir, "addres");
            for (String re : izpackAdditionalResourcePaths) {
                copyResourceToDir(re, addres);
            }
        }
    }

    private void runIzPackCompiler() throws Exception {
        PrintStream console = System.out;
        try {
            // redirect izPack output to file
            PrintStream ps = new PrintStream(openOutputStream(buildOutputFile), true, "UTF-8");
            System.setOut(ps);
            File installFile = null != installConfigFile ? installConfigFile : new File(izpackDir, "izpack.xml");
            CompilerConfig compilerConfig = new CompilerConfig(installFile.getAbsolutePath(), izpackDir.getAbsolutePath(), "standard", izpackOutputFile.getAbsolutePath(), izpackCompress, null);
            CompilerConfig.setIzpackHome(izpackDir.getAbsolutePath());
            compilerConfig.executeCompiler();
        } finally {
            System.setOut(console);
        }
    }

    private void packInstaller(File jre) throws IOException {
        ZipOutputStream zip = null;
        InputStream resStream = null;
        try {
            OutputStream out = openOutputStream(installerOutputFile);
            String prefix = getBaseName(installerOutputFile.getPath());
            zip = new ZipOutputStream(out);
            zip.putNextEntry(new ZipEntry(prefix + "/install.jar"));
            FileUtils.copyFile(izpackOutputFile, zip);
            zip.putNextEntry(new ZipEntry(prefix + "/install.exe"));
            if(use64BitJre && !useX86LaunchersForX64Installer) {
                resStream = RESOURCE_LOADER.getResource(installLauncher64Path).getInputStream();
            } else {
                resStream = RESOURCE_LOADER.getResource(installLauncher32Path).getInputStream();
            }
            copyLarge(resStream, zip);
            ZipFunction zipper = new ZipFunction(jre, prefix + "/jre", zip);
            Collection<File> files = listFiles(jre, true);
            Collection<String> zipped = Collections2.transform(files, zipper);
            fireTransform(zipped);
            getLog().info("Installer written to: [" + installerOutputFile.getPath() + "]");
        } finally {
            closeQuietly(zip);
            closeQuietly(resStream);
        }
    }

    private void packDist() throws IOException {
        TarArchiveOutputStream tar = null;
        try {
            OutputStream out = openOutputStream(distOutputFile);
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            String prefix = getBaseName(distOutputFile.getPath());
            tar = new TarArchiveOutputStream(gzip);
            TarFunction tarfun = new TarFunction(distDir, prefix, tar);
            IOFileFilter uninstallFilter = new NotFileFilter(new NameFileFilter("uninstall"));
            Collection<File> files = listFiles(distDir, TrueFileFilter.TRUE, uninstallFilter, true);
            Collection<String> tarred = Collections2.transform(files, tarfun);
            fireTransform(tarred);
        } finally {
            IOUtils.closeQuietly(tar);
        }
    }

    private class CopyFunction implements Function<Resource, File> {
        protected final File dir;

        private CopyFunction(File dir) {
            this.dir = dir;
        }

        public File apply(Resource input) {
            File file = new File(dir, input.getFilename());
            return copyResource(input, file);
        }
    }

    private class FtlCopyFunction extends CopyFunction {
        private final String ftlOutputEncoding;

        private FtlCopyFunction(File dir) {
            this(dir, UTF8);
        }

        private FtlCopyFunction(File dir, String ftlOutputEncoding) {
            super(dir);
            this.ftlOutputEncoding = ftlOutputEncoding;
        }

        public File apply(Resource input) {
            String name = input.getFilename();
            final File file;
            if(name.endsWith(".ftl")) {
                OutputStream out = null;
                try {
                    file = new File(dir, name.substring(0, name.length() - 4));
                    OutputStream os = openOutputStream(file);
                    freemarker.process(input.getInputStream(), InstallerMojo.this, os, ftlOutputEncoding);
                } catch (IOException e) {
                    throw new UnhandledException(e);
                } finally {
                    closeQuietly(out);
                }
            } else {
                file = super.apply(input);
            }
            return file;
        }
    }

    private class MarkExecutableFunction implements Function<File, Boolean> {
        public Boolean apply(File input) {
            return input.setExecutable(true);
        }
    }

    private class PostfixPredicate implements Predicate<Resource> {
        private final String postfix;

        private PostfixPredicate(String postfix) {
            this.postfix = postfix;
        }

        public boolean apply(Resource input) {
            return path(input).endsWith(postfix);
        }
    }

    private class Dirs {
        private final File bin;
        private final File lib;
        private final File uninstall;

        private Dirs(File bin, File lib, File uninstall) {
            this.bin = bin;
            this.lib = lib;
            this.uninstall = uninstall;
        }
    }
}
