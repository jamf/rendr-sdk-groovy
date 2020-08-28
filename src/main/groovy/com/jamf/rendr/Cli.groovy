package com.jamf.rendr

import org.codehaus.groovy.control.CompilerConfiguration

class Cli {
    static void main(String[] args) {
        if (!args) {
            die "Error: no parameters provided. Expected [script_path]"
        }

        def file = new File(args[0])
        if (!file.exists()) {
            die "Error: specified file does not exist: $file.absolutePath"
        }

        try {
            new Cli().execute(file)
        } catch (e) {
            die "Error: failed to run script: $e.message"
        }
    }

    def execute(File script) {
        def binding = new Binding()
        def compilerConfiguration = new CompilerConfiguration(scriptBaseClass: RendrScript.class.name)
        def shell = new GroovyShell(this.class.classLoader, binding, compilerConfiguration)
        shell.evaluate(script)
    }

    private static void die(String message) {
        System.err.println(message)
        System.exit(1)
    }
}
