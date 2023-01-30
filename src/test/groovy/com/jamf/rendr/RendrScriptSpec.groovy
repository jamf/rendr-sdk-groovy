package com.jamf.rendr

import org.codehaus.groovy.control.CompilerConfiguration
import org.intellij.lang.annotations.Language
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

    List<Action> evaluate(@Language('groovy') String script) {
        shell.evaluate(script) as List<Action>
    }

    def 'script can create file with text'() {
        given:
        def f = new File(tmp, 'foo.txt')

        when:
        evaluate("create '$f.path' write 'some text'")

        then:
        f.text == 'some text'
    }

    def 'script can append text into file'() {
        given:
        def f = new File(tmp, 'foo.txt')

        when:
        evaluate("file '$f.path' append 'some text'")

        then:
        f.text.endsWith 'some text'
    }

    def 'script can insert text into file after pattern'() {
        given:
        def f = new File(tmp, 'foo.txt')
        f.text = 'abc\n123'

        when:
        def actions = evaluate("""insert '''\nfoo''' into '$f.path' after 'abc' """)

        then:
        actions.size() == 1

        actions.first() instanceof InsertAction
        def action = actions.first() as InsertAction
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

        when:
        def actions = evaluate("""replace 'abc' with 'foo' inside '$f.path' """)

        then:
        actions.size() == 1

        actions.first() instanceof ReplaceAction
        def action = actions.first() as ReplaceAction
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

        when:
        def actions = evaluate("git 'init'; git 'add .'")

        then:
        actions.size() == 2

        actions.first() instanceof GitAction
        def g1 = actions.first() as GitAction
        g1.command == 'init'

        actions.last() instanceof GitAction
        def g2 = actions.last() as GitAction
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

        when:
        def actions = evaluate("""append '''\nfoo''' to '$f.path'""")

        then:
        actions.first() instanceof AppendAction
        def action = actions.first() as AppendAction
        action.file == f
        action.text == '\nfoo'

        and:
        f.text == '42\nfoo'
    }

    def 'script can prepend text to file'() {
        given:
        def f = new File(tmp, 'foo.txt')
        f.text = '42'

        when:
        def actions = evaluate("prepend '''foo\n''' to '$f.path'")

        then:
        actions.first() instanceof PrependAction
        def action = actions.first() as PrependAction
        action.file == f
        action.text == 'foo\n'

        and:
        f.text == 'foo\n42'
    }

    def 'move file to new directory'() {
        given:
        def f = new File(tmp, 'foo.txt')
        f.text = '42'
        def sub = new File(tmp, 'sub')
        sub.mkdirs()

        when:
        evaluate("move '$f.path' to '$sub.path/foo.txt'")

        then:
        def moved = new File(tmp, 'sub/foo.txt')
        moved.exists()
        moved.text == '42'
    }
}
