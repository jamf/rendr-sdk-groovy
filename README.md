# Rendr SDK for Groovy

_Library to enable scripting in Rendr blueprints_

## Usage

### Running a script

Check out the help text for details on usage:

    ❯ rendr-sdk-groovy --help

    Usage: rendr-sdk-groovy [-hsV] [-v=<key=value>]... <script>
    A Groovy script runner for rendr
          <script>              The script file to run.
      -h, --help                Show this help message and exit.
      -s, --stacktrace          Print stacktrace on error.
      -v, --value=<key=value>   Value used in script (flag may be repeated).
      -V, --version             Print version information and exit.

Running a script looks like this:

    rendr-sdk-groovy upgrade-v3.groovy --value name=foo --value version=42

### Writing a script

Blueprint authors can use this library to simplify creating scripts, especially
for blueprint upgrades from one version to the next. The library provides a
number of DSL commands:

* `create` files or directories
* `write` text to file
* `append` text to file
* `prepend` text to file
* `insert` text in file
* `replace` text in file
* `git` commands
* `script` for generic Groovy code

Here is a sample script using these DSL commands:

```groovy
insert 'echo "hi!"' into 'hello.sh' after '#!sh\n'
replace 'hello, world!' with "hello, $name!" inside 'hello.sh'  // references 'name' variable from values flag
git "mv hello.sh ${values.name}.sh"  // this also references 'name', but this time from the 'values' map

create 'hello.log'
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

### Available functions

Function                                                      | Example
-------                                                       | -------
`file(String path)`                                           | `file('hello.sh')`
`dir(String path)`                                            | `dir('foo')`
`create(String file)`                                         | `create 'hello.sh'`
`create(File file)`                                           | `create file('hello.sh')`
`create(Dir dir)`                                             | `create dir('app')`
`create(String file).write(String text)`                      | `create 'hello.sh' write 'echo hi'`
`append(String text).to(String file)`                         | `append 'echo "hi $1"' to 'hello.sh'`
`prepend(String text).to(String file)`                        | `prepend 'echo "hi $1"' to 'hello.sh'`
`insert(String text).into(String file).after(String pattern)` | `insert 'echo "hi!"' into 'hello.sh' after '#!sh\n'`
`replace(String text).with(String text).inside(String file)`  | `replace 'hello, world!' with 'hi, world!' inside 'hello.sh'`
`git(String command)`                                         | `git 'add hello.sh'`
`script(Closure block)`                                       | `script { println "System properties: ${args.split().findAll { it.startsWith('-D') }}"}`
