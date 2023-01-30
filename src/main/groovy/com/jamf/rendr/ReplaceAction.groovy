package com.jamf.rendr

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
