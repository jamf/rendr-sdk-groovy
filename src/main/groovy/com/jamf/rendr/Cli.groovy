package com.jamf.rendr

import org.codehaus.groovy.control.CompilerConfiguration

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

import java.util.concurrent.Callable

@Command(name = 'rendr-sdk-groovy', version = 'v1.0.0',
         mixinStandardHelpOptions = true, // add --help and --version options
         description = 'A Groovy script runner for @|bold rendr|@')
class Cli implements Callable<Integer> {

    @Parameters(index = '0', description = 'The script file to run.')
    File script

    @Option(names = ['-v', '--value'], paramLabel = '<key=value>', description = 'Value used in script (flag may be repeated).')
    Map<String, String> values = [:]

    @Option(names = ['-s', '--stacktrace'], description = 'Print stacktrace on error.')
    boolean stacktrace

    static void main(String[] args) {
        int exitCode = new CommandLine(new Cli()).execute(args)
        System.exit(exitCode)
    }

    Integer call() {
        try {
            def context = values + [values: values]
            def binding = new Binding(context)
            def compilerConfiguration = new CompilerConfiguration(scriptBaseClass: RendrScript.class.name)
            def shell = new GroovyShell(this.class.classLoader, binding, compilerConfiguration)
            shell.evaluate(script)
        } catch (e) {
            System.err.println "Failed to run script: $e.message"
            if (stacktrace) {
                e.printStackTrace()
            }
            return 1
        }
        return 0
    }
}
