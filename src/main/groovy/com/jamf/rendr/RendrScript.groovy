package com.jamf.rendr

abstract class RendrScript extends Script {

    List actions = []

    abstract def runScript()

    def run() {
        runScript()
        println "Running actions:"
        actions.each { println "- $it" }
        actions.each {
            it.validate()
            it.run()
        }
        actions
    }

    PrependAction prepend(text) {
        def action = new PrependAction(text: text)
        actions << action
        action
    }

    AppendAction append(text) {
        def action = new AppendAction(text: text)
        actions << action
        action
    }

    InsertAction insert(text) {
        def action = new InsertAction(text: text)
        actions << action
        action
    }

    ReplaceAction replace(pattern) {
        def action = new ReplaceAction(pattern: pattern)
        actions << action
        action
    }

    GitAction git(command) {
        def action = new GitAction(command: command)
        actions << action
        action
    }

    ScriptAction script(Closure block) {
        script('Run script block', block)
    }

    ScriptAction script(String name, Closure block) {
        def action = new ScriptAction(name: name, block: block)
        actions << action
        action
    }

    File create(String name) {
        create(new File(name))
    }

    File create(File file) {
        file.createNewFile()
        file
    }

    MoveAction move(String path) {
        move(new File(path))
    }

    MoveAction move(File file) {
        def action = new MoveAction(file: file)
        actions << action
        action
    }

    Dir create(Dir dir) {
        dir.mkdirs()
        dir
    }

    File file(path) {
        new File(path)
    }

    File file(File parent, path) {
        new File(parent, path)
    }

    Dir dir(path) {
        new Dir(path)
    }

    Dir dir(File parent, path) {
        new Dir(parent, path)
    }
}

class Dir {
    @Delegate File file

    Dir(path) {
        file = new File(path)
    }

    Dir(File parent, path) {
        file = new File(parent, path)
    }
}

abstract class Action {
    abstract void run()
    abstract void validate()

    void toss(String message) {
        throw new RendrException(message)
    }

    String clean(String s) {
        s.replaceAll(/\n/, '\\\\n')
    }
}

class RendrException extends Exception {
    RendrException(String message) {
        super(message)
    }
}

class InsertAction extends Action {
    File file
    String text
    String after

    InsertAction after(String text) {
        this.after = text
        this
    }

    InsertAction into(file) {
        this.file = new File(file)
        this
    }

    void run() {
        file.text = file.text.replace(after, "$after$text")
    }

    void validate() {
        file.exists() ?: toss("Insert failed: file '$file' does not exist")
        file.text.contains(after) ?: toss("Insert failed: file '$file.name' does not contain '${clean(after)}'")
    }

    String toString() {
        "Insert '${clean(text)}' after '${clean(after)}' in $file.name"
    }
}

class ReplaceAction extends Action {
    File file
    String text
    String pattern

    ReplaceAction with(String text) {
        this.text = text
        this
    }

    ReplaceAction inside(file) {
        this.file = new File(file)
        this
    }

    void run() {
        file.text = file.text.replaceAll(pattern, text)
    }

    void validate() {
        file.exists() ?: toss("Replace failed: file '$file' does not exist")
        // file.text ==~ ~/(m).*${pattern}.*/ ?: toss("Replace failed: file '$file.name' does not contain '${clean(pattern)}'")
    }

    String toString() {
        "Replace '${clean(pattern)}' with '${clean(text)}' in $file.name"
    }
}

class AppendAction extends Action {
    File file
    String text

    AppendAction to(file) {
        this.file = new File(file)
        this
    }

    void run() {
        file.append(text)
    }

    void validate() {
        file.exists() ?: toss("Append failed: file '$file' does not exist")
    }

    String toString() {
        "Append '${clean(text)}' on $file.name"
    }
}

class PrependAction extends Action {
    File file
    String text

    PrependAction to(file) {
        this.file = new File(file)
        this
    }

    void run() {
        file.text = text + file.text
    }

    void validate() {
        file.exists() ?: toss("Prepend failed: file '$file' does not exist")
    }

    String toString() {
        "Prepend '${clean(text)}' on $file.name"
    }
}

class GitAction extends Action {
    String command

    void run() {
        def p = ['sh', '-c', "git $command"].execute()
        p.waitFor()
    }

    void validate() {}

    String toString() {
        "Run git $command"
    }
}

class ScriptAction extends Action {
    String name
    Closure block

    void run() {
        block()
    }

    void validate() {}

    String toString() {
        name
    }
}

class MoveAction extends Action {
    File file
    File target

    MoveAction to(target) {
        this.target = new File(target)
        this
    }

    void run() {
        file.renameTo(target)
    }

    void validate() {
        file.exists() ?: toss("Move failed: file '$file' does not exist")
    }

    String toString() {
        "Move $file to $target"
    }
}
