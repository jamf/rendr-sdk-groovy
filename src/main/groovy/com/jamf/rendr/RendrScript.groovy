package com.jamf.rendr

abstract class RendrScript extends Script {

    List<Action> actions = []

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