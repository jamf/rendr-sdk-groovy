package com.jamf.rendr

import org.codehaus.groovy.control.CompilerConfiguration
import spock.lang.Shared
import spock.lang.Specification

class RendrScriptSpec extends Specification {

    @Shared GroovyShell shell
    @Shared File tmp

    def setup() {
        tmp = File.createTempDir()

        def binding = new Binding()
        def compilerConfiguration = new CompilerConfiguration(scriptBaseClass: RendrScript.class.name)

        shell = new GroovyShell(this.class.classLoader, binding, compilerConfiguration)
    }

    def 'script can append text into file'() {
        given:
        def f = new File(tmp, 'foo.txt')
        def script = "file '$f.path' append 'some text'"

        when:
        def result = shell.evaluate(script)

        then:
        f.text.endsWith 'some text'
    }

    def 'script can insert text into file after pattern'() {
        given:
        def f = new File(tmp, 'foo.txt')
        f.text = 'abc\n123'
        def script = """insert '''\nfoo''' into '$f.path' after 'abc' """

        when:
        def actions = shell.evaluate(script)

        then:
        actions.size() == 1

        def action = actions.first()
        action instanceof InsertAction
        action.text == '\nfoo'
        action.file == f
        action.after == 'abc'

        and:
        f.text == 'abc\nfoo\n123'
    }

    def 'script can replace text in file'() {
        given:
        def f = new File(tmp, 'foo.txt')
        f.text = 'abc\n123'
        def script = """replace 'abc' with 'foo' inside '$f.path' """

        when:
        def actions = shell.evaluate(script)

        then:
        actions.size() == 1

        def action = actions.first()
        action instanceof ReplaceAction
        action.file == f
        action.pattern == 'abc'
        action.text == 'foo'

        and:
        f.text == 'foo\n123'
    }

    def 'script can perform git commands'() {
        given:
        def repoDir = new File(tmp, 'repo')
        repoDir.mkdirs()
        def f = new File(repoDir, 'foo.txt')
        f.text = 'foo'
        def script = "git 'init'; git 'add .'"

        when:
        def actions = shell.evaluate(script)

        then:
        actions.size() == 2

        def g1 = actions.first()
        g1 instanceof GitAction
        g1.command == 'init'

        def g2 = actions.last()
        g2 instanceof GitAction
        g2.command == 'add .'

        and:
        def p2 = "cd $repoDir && git status".execute()
        p2.in.text == ''
        p2.err.text == ''
        p2.exitValue() == 0
    }

    def 'script can append text to file'() {
        given:
        def f = new File(tmp, 'foo.txt')
        f.text = '42'
        def script = """append '''\nfoo''' on '$f.path'"""

        when:
        def actions = shell.evaluate(script)

        then:
        def action = actions.first()
        action instanceof AppendAction
        action.file == f
        action.text == '\nfoo'

        and:
        f.text == '42\nfoo'
    }

    def 'script can prepend text to file'() {
        given:
        def f = new File(tmp, 'foo.txt')
        f.text = '42'
        def script = "prepend '''foo\n''' on '$f.path'"

        when:
        def actions = shell.evaluate(script)

        then:
        def action = actions.first()
        action instanceof PrependAction
        action.file == f
        action.text == 'foo\n'

        and:
        f.text == 'foo\n42'
    }
}
