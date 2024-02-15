package de.gamedude.evt.script;

import net.minecraft.client.MinecraftClient;

import javax.tools.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// does not work due to fabrics custom ClassLoader
@SuppressWarnings("unused")
public class OldScriptManager extends ClassLoader {

    private final Path destinationPath = Path.of(MinecraftClient.getInstance().runDirectory.getPath(), "/config/evt/scripts/");
    private final Map<String, Script> scriptCache;

    public OldScriptManager() {
        this.scriptCache = new HashMap<>();
        if(!destinationPath.toFile().exists())
            destinationPath.toFile().mkdir();
    }

    public void clearCache() {
        this.scriptCache.clear();
    }

    public Script getScript(String scriptName) {
        String filePath = destinationPath.resolve(scriptName + ".txt").toString();
        // String scriptName =  Path.of(filePath).getFileName().toString().split("\\.")[0]; // path\to\file\NAME.extension
        String scriptContent = wrapScript(loadScriptContent(filePath), scriptName);

        if(!scriptCache.containsKey(scriptName)) { // On every restart script will recompile
            compileClass(scriptName, scriptContent);
        } else
            return scriptCache.get(scriptName);

        Script script = compileScript(scriptName);
        scriptCache.put(scriptContent, script);
        return script;
    }


    // read script content
    private String loadScriptContent(String filePath) {
        try {
            String content = String.join("\n", Files.readAllLines(Path.of(filePath)));
            if(content.contains("automationEngine"))
                throw new AccessDeniedException("You are not allowed to use that method!");
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // make script a fully java file
    private String wrapScript(String scriptContent, String className) {
        return "public class " + className + " extends de.gamedude.evt.script.Script {" +
                "\n" + scriptContent +
                "\n}";
    }

    // compile java source code to .class file
    private void compileClass(String className, String javaCode) {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        try ( StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(diagnosticCollector, null, null) ) {

            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(destinationPath.toFile()));
            Iterable<? extends JavaFileObject> units = Collections.singletonList(new StringSourceJavaObject(className, javaCode));
            boolean successful = javaCompiler.getTask(null, fileManager, diagnosticCollector, null, null, units).call();

            if(!successful) {
                for ( Diagnostic<? extends JavaFileObject> diagnostic : diagnosticCollector.getDiagnostics()) {
                    System.out.println(diagnostic.getMessage(null));
                    System.out.format("Error on line %d in %s%n",
                            diagnostic.getLineNumber(),
                            "");
                }
            }

        } catch ( IOException e) {
            throw new RuntimeException("Could not compile script, make sure to have a jdk installed!");
        }
    }

    // create & load class
    private Script compileScript(String className){
        try {
            byte[] b = Files.readAllBytes(destinationPath.resolve(className + ".class"));
            Class<?> aClass = defineClass(className, b, 0, b.length);

            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class StringSourceJavaObject extends SimpleJavaFileObject {
        private final String code;

        public StringSourceJavaObject(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }



}
