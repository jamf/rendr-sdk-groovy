# Rendr SDK for Groovy

_Library to enable scripting in Rendr blueprints_

## Usage

Blueprint authors can use this library to simplify creating scripts, especially
for blueprint upgrades from one version to the next. The library provides a
number of DSL commands:

* `create` files or directories
* `append` text to file
* `prepend` text to file
* `insert` text in file
* `replace` text in file
* `git` commands
* `script` for generic Groovy code

Here is a sample script using these DSL commands:

```groovy
insert 'echo "hi!"' into 'hello.sh' after '#!sh\n'
replace 'hello, world!' with 'hi, world!' inside 'hello.sh'
git 'mv hello.sh hi.sh'

create file('hello.log')
git 'add hello.log'

script {
    files = []
    dir('src').eachFileRecurse(FileType.FILES) { file ->
        files << file
    }
    println "All files in src:"
    files.sort().each { println it.path }
}
```

## Functions

Function                                                      | Return Type | Example
-------                                                       | ------      | -------
`file(String path)`                                           | `File`      | `file('hello.sh')`
`dir(String path)`                                            | `Dir`       | `dir('foo')`
`create(File file)`                                           | `File`      | `create file('hello.sh')`
`create(Dir dir)`                                             | `File`      | `create dir('app')`
`append(String text).to(String file)`                         | none        | `append 'echo "hi $1"' to 'hello.sh'`
`prepend(String text).to(String file)`                        | none        | `prepend 'echo "hi $1"' to 'hello.sh'`
`insert(String text).into(String file).after(String pattern)` | none        | `insert 'echo "hi!"' into 'hello.sh' after '#!sh\n'`
`replace(String text).with(String text).inside(String file)`  | none        | `replace 'hello, world!' with 'hi, world!' inside 'hello.sh'`
`git(String command)`                                         | none        | `git 'add hello.sh'`
`script(Closure block)`                                       | none        | `script { println "System properties: ${args.split().findAll { it.startsWith('-D') }}"}`
